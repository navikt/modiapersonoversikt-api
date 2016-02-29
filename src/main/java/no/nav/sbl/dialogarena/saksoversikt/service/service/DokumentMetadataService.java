package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Kommunikasjonsretning;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Entitet;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;

import javax.inject.Inject;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

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


    public List<DokumentMetadata> hentDokumentMetadata(List<Sak> saker, String fnr) {
        List<DokumentMetadata> joarkMetadataListe = innsynJournalService.joarkSakhentTilgjengeligeJournalposter(saker)
                .orElseGet(() -> empty())
                .collect(toList());

        List<DokumentMetadata> innsendteSoknaderIHenvendelse = henvendelseService.hentHenvendelsessoknaderMedStatus(FERDIG, fnr)
                .stream()
                .map(this::dokumentMetadataFraHenvendelse)
                .collect(toList());


        Stream<DokumentMetadata> soknaderSomHarEndretTema = innsendteSoknaderIHenvendelse
                .stream()
                .filter(henvendelseDokumentmetadata -> harJournalforingEndretTema(henvendelseDokumentmetadata, joarkMetadataListe))
                .map(dokumentMetadata -> dokumentMetadata.withFeilWrapper(JOURNALFORT_ANNET_TEMA));

        Stream<DokumentMetadata> innsendteSoknaderSomBareFinnesIHenvendelse = innsendteSoknaderIHenvendelse.stream().filter(finnesIkkeIJoark(joarkMetadataListe));
        return concat(concat(
                populerEttersendelserFraHenvendelse(joarkMetadataListe, innsendteSoknaderIHenvendelse),
                innsendteSoknaderSomBareFinnesIHenvendelse),
                soknaderSomHarEndretTema).collect(toList());
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
