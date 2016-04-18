package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Soknad;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.JOURNALFORT_ANNET_TEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Soknad.HenvendelseStatus.FERDIG;

@SuppressWarnings("squid:S1166")
public class DokumentMetadataService {
    public static final String JOURNALPOST_INNGAAENDE = "I";
    public static final String JOURNALPOST_UTGAAENDE = "U";
    public static final String JOURNALPOST_INTERN = "N";
    public static final String DOKTYPE_HOVEDDOKUMENT = "HOVEDDOKUMENT";
    public static final String DOKTYPE_VEDLEGG = "VEDLEGG";

    public static final String DOKUMENT_LASTET_OPP = "LASTET_OPP";
    public static final Predicate<DokumentFraHenvendelse> erVedlegg
            = dokument -> !dokument.erHovedskjema();
    public static final Predicate<DokumentFraHenvendelse> erHoveddokument
            = dokument -> dokument.erHovedskjema();
    public static final Predicate<DokumentFraHenvendelse> erLastetOpp
            = dokument -> DOKUMENT_LASTET_OPP.equals(dokument.getInnsendingsvalg().name());

    @Inject
    private InnsynJournalService innsynJournalService;

    @Inject
    private HenvendelseService henvendelseService;

    @Inject
    private Kodeverk kodeverk;

    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;

    private Predicate<DokumentMetadata> finnesIJoark(List<DokumentMetadata> joarkMetadata) {
        return henvendelseMetadata -> joarkMetadata.stream()
                .anyMatch(jp -> jp.getJournalpostId().equals(henvendelseMetadata.getJournalpostId()));
    }

    private Predicate<DokumentMetadata> finnesIkkeIJoark(List<DokumentMetadata> joarkMetadata) {
        return finnesIJoark(joarkMetadata).negate();
    }

    public ResultatWrapper<List<DokumentMetadata>> hentDokumentMetadata(List<Sak> saker, String fnr) {
        Set<Baksystem> feilendeBaksystem = new HashSet<>();

        List<DokumentMetadata> joarkMetadataListe;
        List<DokumentMetadata> innsendteSoknaderIHenvendelse;

        try {
            ResultatWrapper<List<DokumentMetadata>> dokumentMetadataResultatWrapper = innsynJournalService.joarkSakhentTilgjengeligeJournalposter(saker, fnr);
            joarkMetadataListe = dokumentMetadataResultatWrapper.resultat;
            feilendeBaksystem.addAll(dokumentMetadataResultatWrapper.feilendeSystemer);
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
            joarkMetadataListe = emptyList();
        }
        try {
            innsendteSoknaderIHenvendelse = henvendelseService.hentHenvendelsessoknaderMedStatus(FERDIG, fnr)
                    .stream()
                    .map(soknad -> dokumentMetadataFraHenvendelse(soknad))
                    .collect(toList());
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
            innsendteSoknaderIHenvendelse = emptyList();
        }

        final List<DokumentMetadata> finalListe = joarkMetadataListe;

        Stream<DokumentMetadata> soknaderSomHarEndretTema = innsendteSoknaderIHenvendelse
                .stream()
                .filter(henvendelseDokumentmetadata -> harJournalforingEndretTema(henvendelseDokumentmetadata, finalListe))
                .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(JOURNALFORT_ANNET_TEMA));

        Stream<DokumentMetadata> innsendteSoknaderSomBareFinnesIHenvendelse = innsendteSoknaderIHenvendelse
                .stream()
                .filter(finnesIkkeIJoark(joarkMetadataListe))
                .map(dokumentMetadata -> dokumentMetadata.withIsJournalfort(FALSE).withLeggTilEttersendelseTekstDersomEttersendelse());

        return new ResultatWrapper<>(Java8Utils.concat(
                populerEttersendelserFraHenvendelse(joarkMetadataListe, innsendteSoknaderIHenvendelse),
                innsendteSoknaderSomBareFinnesIHenvendelse,
                soknaderSomHarEndretTema
        ).collect(toList()), feilendeBaksystem);
    }

    private boolean harJournalforingEndretTema(DokumentMetadata henvendelseDokumentMetadata, List<DokumentMetadata> joarkDokumentMetadataListe) {
        return joarkDokumentMetadataListe
                .stream()
                .filter(dokumentMetadata -> dokumentMetadata.getJournalpostId().equals(henvendelseDokumentMetadata.getJournalpostId()))
                .filter(dokumentMetadata -> !dokumentMetadata.getTemakode().equals(henvendelseDokumentMetadata.getTemakode()))
                .findAny()
                .isPresent();
    }

    private Stream<DokumentMetadata> populerEttersendelserFraHenvendelse(List<DokumentMetadata> joarkMetadata, List<DokumentMetadata> ferdigeHenvendelser) {
        return joarkMetadata.stream().map(joarkDokumentMetadata -> {
            boolean erEttersending = ferdigeHenvendelser.stream().anyMatch(henvendelse -> joarkDokumentMetadata.getJournalpostId().equals(henvendelse.getJournalpostId())
                    && henvendelse.isEttersending());
            return joarkDokumentMetadata.withEttersending(erEttersending);
        });
    }


    private DokumentMetadata dokumentMetadataFraHenvendelse(Soknad soknad) {
        String temakode = kodeverk.getKode(soknad.getSkjemanummerRef(), Kodeverk.Nokkel.TEMA);
        boolean kanVises = !"BID".equals(temakode);

        Dokument hovedDokument = soknad.getDokumenter()
                .stream()
                .filter(erHoveddokument)
                .map(dokument ->
                        new Dokument()
                                .withTittel(kodeverk.getTittel(dokument.getKodeverkRef()))
                                .withKanVises(kanVises)
                                .withLogiskDokument(false)
                                .withDokumentreferanse(dokument.getArkivreferanse())
                )
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Respons fra henvendelse inneholdt ikke hoveddokument"));

        List<Dokument> vedlegg = soknad.getDokumenter()
                .stream()
                .filter(erVedlegg)
                .filter(erLastetOpp)
                .map(dokument ->
                        new Dokument()
                                .withTittel(kodeverk.getTittel(dokument.getKodeverkRef()))
                                .withKanVises(kanVises)
                                .withLogiskDokument(false)
                                .withDokumentreferanse(dokument.getArkivreferanse())
                )
                .collect(toList());

        ResultatWrapper temanavnForTemakode = bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA);
        return new DokumentMetadata()
                .withJournalpostId(soknad.getJournalpostId())
                .withHoveddokument(hovedDokument)
                .withEttersending(soknad.getEttersending())
                .withVedlegg(vedlegg)
                .withDato(soknad.getInnsendtDato().toGregorianCalendar().toZonedDateTime().toLocalDateTime())
                .withAvsender(Entitet.SLUTTBRUKER)
                .withMottaker(Entitet.NAV)
                .withTemakode(temakode)
                .withBaksystem(Baksystem.HENVENDELSE)
                .withRetning(Kommunikasjonsretning.INN)
                .withTemakodeVisning((String) temanavnForTemakode.resultat);
    }
}
