package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Kommunikasjonsretning;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.*;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Feilmelding.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.HenvendelseStatus.FERDIG;

public class DokumentMetadataService {
    public static final String JOURNALPOST_INNGAAENDE = "I";
    public static final String JOURNALPOST_UTGAAENDE = "U";
    public static final String JOURNALPOST_INTERN = "N";
    public static final String DOKTYPE_HOVEDDOKUMENT = "HOVEDDOKUMENT";
    public static final String DOKTYPE_VEDLEGG = "VEDLEGG";

    public static final String DOKUMENT_LASTET_OPP = "LASTET_OPP";
    public static final Predicate<Record<no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument>> erVedlegg = dokumentRecord -> !dokumentRecord.get(Dokument.HOVEDSKJEMA);
    public static final Predicate<Record<no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument>> erHoveddokument = dokumentRecord -> dokumentRecord.get(Dokument.HOVEDSKJEMA);
    public static final Predicate<Record<no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument>> erLastetOpp = dokumentRecord -> DOKUMENT_LASTET_OPP.equals(dokumentRecord.get(Dokument.INNSENDINGSVALG).name());

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

    public DokumentMetadataResultatWrapper hentDokumentMetadata(List<Sak> saker, String fnr) {
        Set<Baksystem> feilendeBaksystem = new HashSet<>();

        List<DokumentMetadata> joarkMetadataListe;
        List<DokumentMetadata> innsendteSoknaderIHenvendelse;

        try {
            joarkMetadataListe = innsynJournalService.joarkSakhentTilgjengeligeJournalposter(saker, fnr)
                    .orElseGet(() -> empty())
                    .collect(toList());
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystem.add(e.getBaksystem());
            joarkMetadataListe = emptyList();
        }
        try {
            innsendteSoknaderIHenvendelse = henvendelseService.hentHenvendelsessoknaderMedStatus(FERDIG, fnr)
                    .stream()
                    .map(this::dokumentMetadataFraHenvendelse)
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
                .filter(finnesIkkeIJoark(joarkMetadataListe));

        return new DokumentMetadataResultatWrapper(Java8Utils.concat(
                populerEttersendelserFraHenvendelse(joarkMetadataListe, innsendteSoknaderIHenvendelse),
                innsendteSoknaderSomBareFinnesIHenvendelse,
                soknaderSomHarEndretTema
        ).collect(Collectors.toList()), feilendeBaksystem);
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


    private DokumentMetadata dokumentMetadataFraHenvendelse(Record<Soknad> soknadRecord) {

        String temakode = kodeverk.getKode(soknadRecord.get(Soknad.SKJEMANUMMER_REF), Kodeverk.Nokkel.TEMA);
        boolean kanVises = !"BID".equals(temakode);

        no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument hovedDokument = soknadRecord.get(Soknad.DOKUMENTER)
                .stream()
                .filter(erHoveddokument)
                .map(dokumentRecord ->
                        new no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument()
                                .withTittel(kodeverk.getTittel(dokumentRecord.get(Dokument.KODEVERK_REF)))
                                .withKanVises(kanVises)
                                .withLogiskDokument(false)
                                .withDokumentreferanse(dokumentRecord.get(Dokument.ARKIVREFERANSE))
                )
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Respons fra henvendelse inneholdt ikke hoveddokument"));

        List<no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> vedlegg = soknadRecord.get(Soknad.DOKUMENTER)
                .stream()
                .filter(erVedlegg)
                .filter(erLastetOpp)
                .map(dokumentRecord ->
                        new no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument()
                                .withTittel(kodeverk.getTittel(dokumentRecord.get(Dokument.KODEVERK_REF)))
                                .withKanVises(kanVises)
                                .withLogiskDokument(false)
                                .withDokumentreferanse(dokumentRecord.get(Dokument.ARKIVREFERANSE))
                )
                .collect(toList());

        return new DokumentMetadata()
                .withJournalpostId(soknadRecord.get(Soknad.JOURNALPOST_ID))
                .withHoveddokument(hovedDokument)
                .withEttersending(soknadRecord.get(Soknad.ETTERSENDING))
                .withVedlegg(vedlegg)
                .withDato(soknadRecord.get(Soknad.INNSENDT_DATO).toGregorianCalendar().toZonedDateTime().toLocalDateTime())
                .withAvsender(Entitet.SLUTTBRUKER)
                .withMottaker(Entitet.NAV)
                .withTemakode(temakode)
                .withBaksystem(Baksystem.HENVENDELSE)
                .withRetning(Kommunikasjonsretning.INN)
                .withTemakodeVisning(bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA));
    }
}
