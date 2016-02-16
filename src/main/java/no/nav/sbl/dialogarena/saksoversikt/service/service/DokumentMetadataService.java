package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Kommunikasjonsretning;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Entitet;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;

public class DokumentMetadataService {

    public static final String JOURNALPOST_INNGAAENDE = "I";
    public static final String JOURNALPOST_UTGAAENDE = "U";
    public static final String JOURNALPOST_INTERN = "N";
    public static final String DOKTYPE_HOVEDDOKUMENT = "HOVEDDOKUMENT";
    public static final String DOKTYPE_VEDLEGG = "VEDLEGG";
    public static final String DOKTYPE_SAMMENSATT_DOK = "SAMMENSATT_DOK"; /*Denne er ikke i bruk hos Joark*/
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

    public List<DokumentMetadata> hentDokumentMetadata(List<Sak> saker, String fnr) {
        List<DokumentMetadata> joarkMetadata = innsynJournalService.joarkSakhentTilgjengeligeJournalposter(saker)
                .orElseGet(() -> empty())
                .map(jp -> dokumentMetadataFraJournalPost(jp))
                .collect(toList());

        Predicate<DokumentMetadata> finnesIJoark = henvendelseMetadata ->
                joarkMetadata
                        .stream().anyMatch(jp -> jp.getJournalpostId().equals(henvendelseMetadata.getJournalpostId()));

        Predicate<DokumentMetadata> finnesIkkeIJoark = finnesIJoark.negate();

        return concat(
                joarkMetadata.stream(),
                henvendelseService.hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus.FERDIG, fnr)
                        .stream()
                        .map(record -> dokumentMetadataFraHenvendelse(record))
                        .filter(finnesIkkeIJoark)
        )
                .collect(toList());
    }

    public DokumentMetadata dokumentMetadataFraJournalPost(Journalpost journalpost) throws RuntimeException {

        Map<String, List<DokumentinfoRelasjon>> relasjoner = byggRelasjonsMap(journalpost);

        no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument hoveddokument = finnHoveddokument(opprettDokument, relasjoner);
        Stream<no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> vedlegg = finnVedlegg(opprettDokument, relasjoner);
        Stream<no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> logiskeVedlegg = finnLogiskeVedlegg(opprettLogiskDokument, relasjoner);

        LocalDate dato = finnDato(journalpost);

        Pair<Entitet, Entitet> avsenderMottaker = finnAvsenderMottaker(journalpost);

        return new DokumentMetadata()
                .withJournalpostId(journalpost.getJournalpostId())
                .withHoveddokument(hoveddokument)
                .withVedlegg(
                        concat(vedlegg, logiskeVedlegg)
                                .collect(toList()))
                .withDato(dato)
                .withAvsender(avsenderMottaker.getLeft())
                .withMottaker(avsenderMottaker.getRight())
                .withNavn(journalpost.getEksternPart())
                .withTemakode(journalpost.getArkivtema().getValue())
                .withRetning(Kommunikasjonsretning.fraJournalpostretning(journalpost.getKommunikasjonsretning().getValue()))
                .withBaksystem(Baksystem.JOARK)
                .withTilhorendeSakid(journalpost.getGjelderSak().getSakId())
                .withTemakodeVisning(bulletproofKodeverkService.getTemanavnForTemakode(journalpost.getArkivtema().getValue(), BulletproofKodeverkService.ARKIVTEMA));
    }

    public Optional<String> finnTittelForDokumentReferanseIJournalpost(Journalpost journalpost, String dokumentreferanse) {
        DokumentMetadata metadata = dokumentMetadataFraJournalPost(journalpost);
        return concat(
                singletonList(metadata.getHoveddokument()).stream(),
                metadata.getVedlegg().stream())
                .filter(d -> d.getDokumentreferanse().equals(dokumentreferanse))
                .findFirst()
                .map(d -> d.getTittel());
    }

    private Pair<Entitet, Entitet> finnAvsenderMottaker(Journalpost journalpost) {

        if (meldingFraBrukerTilNAV(journalpost)) {
            return new ImmutablePair<>(Entitet.SLUTTBRUKER, Entitet.NAV);
        } else if (meldingFraNAVtilBruker(journalpost)) {
            return new ImmutablePair<>(Entitet.NAV, Entitet.SLUTTBRUKER);
        } else if (meldingFraEksternPartTilNAV(journalpost)) {
            return new ImmutablePair<>(Entitet.EKSTERN_PART, Entitet.NAV);
        } else if (meldingFraNavTilEksternPart(journalpost)) {
            return new ImmutablePair<>(Entitet.NAV, Entitet.EKSTERN_PART);
        } else if (meldingIntern(journalpost)) {
            return new ImmutablePair<>(Entitet.NAV, Entitet.NAV);
        } else {
            return new ImmutablePair<>(Entitet.UKJENT, Entitet.UKJENT);
        }
    }

    private LocalDate finnDato(Journalpost journalpost) {
        switch (journalpost.getKommunikasjonsretning().getValue()) {
            case JOURNALPOST_INNGAAENDE:
                return journalpost.getMottatt().toGregorianCalendar().toZonedDateTime().toLocalDate();
            case JOURNALPOST_UTGAAENDE:
                return journalpost.getSendt().toGregorianCalendar().toZonedDateTime().toLocalDate();
            case JOURNALPOST_INTERN:
                return journalpost.getSendt().toGregorianCalendar().toZonedDateTime().toLocalDate();
            default:
                return LocalDate.now();
        }
    }

    private Stream<no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> finnLogiskeVedlegg(Function<SkannetInnhold, no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> opprettLogiskDokument, Map<String, List<DokumentinfoRelasjon>> relasjoner) {
        return Optional.ofNullable(relasjoner.get(DOKTYPE_HOVEDDOKUMENT)).orElse(new ArrayList<>())
                .stream()
                .map(dokumentRel -> dokumentRel.getJournalfoertDokument().getSkannetInnholdListe())
                .flatMap(List::stream)
                .map(opprettLogiskDokument);
    }

    private Stream<no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> finnVedlegg(Function<DokumentinfoRelasjon, no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> opprettDokument, Map<String, List<DokumentinfoRelasjon>> relasjoner) {
        return Optional.ofNullable(relasjoner.get(DOKTYPE_VEDLEGG)).orElse(new ArrayList<>())
                .stream()
                .map(opprettDokument);
    }

    private no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument finnHoveddokument(Function<DokumentinfoRelasjon, no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> opprettDokument, Map<String, List<DokumentinfoRelasjon>> relasjoner) {
        return Optional.ofNullable(relasjoner.get(DOKTYPE_HOVEDDOKUMENT)).orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .map(opprettDokument)
                .orElseThrow(() -> new RuntimeException("Fant sak uten hoveddokument!"));
    }

    private Map<String, List<DokumentinfoRelasjon>> byggRelasjonsMap(Journalpost journalpost) throws RuntimeException {
        return journalpost.getDokumentinfoRelasjonListe()
                .stream()
                .collect(
                        groupingBy(dokumentinfoRelasjon ->
                                Java8Utils.optional(dokumentinfoRelasjon.getDokumentTilknyttetJournalpost())
                                        .orElseThrow(() -> new RuntimeException("Ugyldig tilstand i svar fra Joark"))
                                        .getValue())
                );
    }

    private DokumentMetadata dokumentMetadataFraHenvendelse(Record<Soknad> soknadRecord) {

        no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument hovedDokument = soknadRecord.get(Soknad.DOKUMENTER)
                .stream()
                .filter(erHoveddokument)
                .map(dokumentRecord ->
                        new no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument()
                                .withTittel(kodeverk.getTittel(dokumentRecord.get(Dokument.KODEVERK_REF)))
                                .withKanVises(true)
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
                                .withKanVises(true)
                                .withLogiskDokument(false)
                                .withDokumentreferanse(dokumentRecord.get(Dokument.ARKIVREFERANSE))
                )
                .collect(toList());

        String temakode = kodeverk.getKode(soknadRecord.get(Soknad.SKJEMANUMMER_REF), Kodeverk.Nokkel.TEMA);

        return new DokumentMetadata()
                .withJournalpostId(soknadRecord.get(Soknad.JOURNALPOST_ID))
                .withHoveddokument(hovedDokument)
                .withVedlegg(vedlegg)
                .withDato(soknadRecord.get(Soknad.INNSENDT_DATO).toGregorianCalendar().toZonedDateTime().toLocalDate())
                .withAvsender(Entitet.SLUTTBRUKER)
                .withMottaker(Entitet.NAV)
                .withTemakode(temakode)
                .withBaksystem(Baksystem.HENVENDELSE)
                .withRetning(Kommunikasjonsretning.INN)
                .withTemakodeVisning(bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA));
    }

    Function<DokumentinfoRelasjon, no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> opprettDokument = (dokumentRel) -> new no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument().withTittel(dokumentRel.getJournalfoertDokument().getTittel())
            .withKanVises(dokumentRel.getJournalfoertDokument().getInnsynDokument().equals(InnsynDokument.JA))
            .withLogiskDokument(false)
            .withDokumentreferanse(dokumentRel.getJournalfoertDokument().getDokumentId());

    Function<SkannetInnhold, no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument> opprettLogiskDokument = skannetInnhold ->
            new no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument()
                    .withTittel(skannetInnhold.getVedleggInnhold())
                    .withDokumentreferanse(skannetInnhold.getSkannetInnholdId())
                    .withLogiskDokument(true)
                    .withKanVises(true);

    private static boolean meldingFraNAVtilBruker(Journalpost journalpost) {
        return JOURNALPOST_UTGAAENDE.equals(journalpost.getKommunikasjonsretning().getValue()) && journalpost.getBrukerErAvsenderMottaker().equals(AvsenderMottaker.JA);
    }

    private static boolean meldingFraBrukerTilNAV(Journalpost journalpost) {
        return JOURNALPOST_INNGAAENDE.equals(journalpost.getKommunikasjonsretning().getValue()) && journalpost.getBrukerErAvsenderMottaker().equals(AvsenderMottaker.JA);
    }

    private static boolean meldingFraNavTilEksternPart(Journalpost journalpost) {
        return JOURNALPOST_UTGAAENDE.equals(journalpost.getKommunikasjonsretning().getValue()) && journalpost.getBrukerErAvsenderMottaker().equals(AvsenderMottaker.NEI);
    }

    private static boolean meldingFraEksternPartTilNAV(Journalpost journalpost) {
        return JOURNALPOST_INNGAAENDE.equals(journalpost.getKommunikasjonsretning().getValue()) && journalpost.getBrukerErAvsenderMottaker().equals(AvsenderMottaker.NEI);
    }

    private static boolean meldingIntern(Journalpost journalpost) {
        return JOURNALPOST_INTERN.equals(journalpost.getKommunikasjonsretning().getValue()) && journalpost.getBrukerErAvsenderMottaker().equals(AvsenderMottaker.NEI);
    }


}
