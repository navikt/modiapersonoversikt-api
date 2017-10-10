package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.Boolean.FALSE;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.JOURNALFORT_ANNET_TEMA;

@SuppressWarnings("squid:S1166")
public class DokumentMetadataService {
    public static final String JOURNALPOST_INNGAAENDE = "I";
    public static final String JOURNALPOST_UTGAAENDE = "U";
    public static final String JOURNALPOST_INTERN = "N";
    public static final String DOKTYPE_HOVEDDOKUMENT = "HOVEDDOKUMENT";
    public static final String DOKTYPE_VEDLEGG = "VEDLEGG";
    public static final String DOKMOT_TEMA = "BIL";

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
                .anyMatch(jp -> henvendelseLikJournalpost(henvendelseMetadata, jp));
    }

    private Predicate<DokumentMetadata> finnesIkkeIJoark(List<DokumentMetadata> joarkMetadata) {
        return finnesIJoark(joarkMetadata).negate();
    }

    private boolean henvendelseLikJournalpost(DokumentMetadata henvendelseMetadata, DokumentMetadata jp) {
        return jp.getJournalpostId().equals(henvendelseMetadata.getJournalpostId())
                || DOKMOT_TEMA.equals(henvendelseMetadata.getTemakode()) && henvendelseMetadata.getDato().equals(jp.getDato());
    }

    public ResultatWrapper<List<DokumentMetadata>> hentDokumentMetadata(List<Sak> saker, String fnr) {
        Set<Baksystem> feilendeBaksystem = new HashSet<>();

        List<DokumentMetadata> joarkMetadataListe = new ArrayList<>();
        List<DokumentMetadata> innsendteSoknaderIHenvendelse = new ArrayList<>();

        try {
            ResultatWrapper<List<DokumentMetadata>> dokumentMetadataResultatWrapper = innsynJournalService.joarkSakhentTilgjengeligeJournalposter(saker, fnr);
            joarkMetadataListe.addAll(dokumentMetadataResultatWrapper.resultat);
            feilendeBaksystem.addAll(dokumentMetadataResultatWrapper.feilendeSystemer);
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
        }
        try {
            innsendteSoknaderIHenvendelse.addAll(henvendelseService.hentInnsendteSoknader(fnr)
                    .stream()
                    .map(soknad -> dokumentMetadataFraHenvendelse(soknad))
                    .collect(toList()));
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
        }

        joarkMetadataListe.forEach(jp -> {
            if (innsendteSoknaderIHenvendelse.stream().anyMatch(henvendelse -> henvendelseLikJournalpost(henvendelse, jp))) {
                jp.withBaksystem(Baksystem.HENVENDELSE);
            }
        });


        Stream<DokumentMetadata> soknaderSomHarEndretTema = innsendteSoknaderIHenvendelse
                .stream()
                .filter(henvendelseDokumentmetadata -> harJournalforingEndretTema(henvendelseDokumentmetadata, joarkMetadataListe))
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
                .filter(dokumentMetadata -> henvendelseLikJournalpost(henvendelseDokumentMetadata, dokumentMetadata))
                .filter(dokumentMetadata -> !dokumentMetadata.getTemakode().equals(henvendelseDokumentMetadata.getTemakode()))
                .findAny()
                .isPresent();
    }

    private Stream<DokumentMetadata> populerEttersendelserFraHenvendelse(List<DokumentMetadata> joarkMetadata, List<DokumentMetadata> ferdigeHenvendelser) {
        return joarkMetadata.stream().map(joarkDokumentMetadata -> {
            boolean erEttersending = ferdigeHenvendelser.stream().anyMatch(henvendelse -> henvendelseLikJournalpost(henvendelse, joarkDokumentMetadata)
                    && henvendelse.isEttersending());
            return joarkDokumentMetadata.withEttersending(erEttersending);
        });
    }


    private DokumentMetadata dokumentMetadataFraHenvendelse(Soknad soknad) {
        String temakode = bulletproofKodeverkService.getKode(soknad.getSkjemanummerRef(), Kodeverk.Nokkel.TEMA);
        boolean kanVises = !"BID".equals(temakode);

        Dokument hovedDokument = soknad.getDokumenter()
                .stream()
                .filter(erHoveddokument)
                .map(dokument ->
                        new Dokument()
                                .withTittel(bulletproofKodeverkService.getSkjematittelForSkjemanummer(dokument.getKodeverkRef()))
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
                                .withTittel(bulletproofKodeverkService.getSkjematittelForSkjemanummer(dokument.getKodeverkRef()))
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
