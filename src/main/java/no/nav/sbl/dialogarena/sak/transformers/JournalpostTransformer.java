package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Kommunikasjonsretning;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Entitet;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.DokumentMetadataService.*;

public class JournalpostTransformer {

    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;

    public DokumentMetadata dokumentMetadataFraJournalPost(WSJournalpost journalpost) throws RuntimeException {

        Map<String, List<WSDokumentinfoRelasjon>> relasjoner = byggRelasjonsMap(journalpost);

        Dokument hoveddokument = finnHoveddokument(opprettDokument, relasjoner);
        Stream<Dokument> vedlegg = finnVedlegg(opprettDokument, relasjoner);
        Stream<Dokument> logiskeVedlegg = finnLogiskeVedlegg(opprettLogiskDokument, relasjoner);

        LocalDateTime dato = finnDato(journalpost);

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
                .withNavn(finnNavn(journalpost.getEksternPart()))
                .withTemakode(journalpost.getArkivtema().getValue())
                .withRetning(Kommunikasjonsretning.fraJournalpostretning(journalpost.getKommunikasjonsretning().getValue()))
                .withBaksystem(Baksystem.JOARK)
                .withTilhorendeSakid(journalpost.getGjelderSak().getSakId())
                .withTemakodeVisning(bulletproofKodeverkService.getTemanavnForTemakode(journalpost.getArkivtema().getValue(), BulletproofKodeverkService.ARKIVTEMA));
    }

    private String finnNavn(WSAktoer aktoer) {
        if (aktoer instanceof WSPerson) {
            return ((WSPerson) aktoer).getNavn();
        } else if  (aktoer instanceof WSOrganisasjon) {
            ((WSOrganisasjon) aktoer).getNavn();
        }
        return "";
    }

    private Map<String, List<WSDokumentinfoRelasjon>> byggRelasjonsMap(WSJournalpost journalpost) throws RuntimeException {
        return journalpost.getDokumentinfoRelasjonListe()
                .stream()
                .collect(
                        groupingBy(dokumentinfoRelasjon ->
                                Java8Utils.optional(dokumentinfoRelasjon.getDokumentTilknyttetJournalpost())
                                        .orElseThrow(() -> new RuntimeException("Ugyldig tilstand i svar fra Joark"))
                                        .getValue())
                );
    }

    Function<WSDokumentinfoRelasjon, Dokument> opprettDokument = (dokumentRel) -> new Dokument().withTittel(dokumentRel.getJournalfoertDokument().getTittel())
            .withLogiskDokument(false)
            .withDokumentreferanse(dokumentRel.getJournalfoertDokument().getDokumentId());

    Function<WSSkannetInnhold, Dokument> opprettLogiskDokument = skannetInnhold ->
            new Dokument()
                    .withTittel(skannetInnhold.getVedleggInnhold())
                    .withDokumentreferanse(skannetInnhold.getSkannetInnholdId())
                    .withLogiskDokument(true)
                    .withKanVises(true);


    private Stream<Dokument> finnVedlegg(Function<WSDokumentinfoRelasjon, Dokument> opprettDokument, Map<String, List<WSDokumentinfoRelasjon>> relasjoner) {
        return Optional.ofNullable(relasjoner.get(DOKTYPE_VEDLEGG)).orElse(new ArrayList<>())
                .stream()
                .map(opprettDokument);
    }

    private Dokument finnHoveddokument(Function<WSDokumentinfoRelasjon, Dokument> opprettDokument, Map<String, List<WSDokumentinfoRelasjon>> relasjoner) {
        return Optional.ofNullable(relasjoner.get(DOKTYPE_HOVEDDOKUMENT)).orElse(new ArrayList<>())
                .stream()
                .findFirst()
                .map(opprettDokument)
                .orElseThrow(() -> new RuntimeException("Fant sak uten hoveddokument!"));
    }

    public Optional<String> finnTittelForDokumentReferanseIJournalpost(WSJournalpost journalpost, String dokumentreferanse) {
        DokumentMetadata metadata = dokumentMetadataFraJournalPost(journalpost);
        return concat(
                singletonList(metadata.getHoveddokument()).stream(),
                metadata.getVedlegg().stream())
                .filter(d -> d.getDokumentreferanse().equals(dokumentreferanse))
                .findFirst()
                .map(d -> d.getTittel());
    }

    private Pair<Entitet, Entitet> finnAvsenderMottaker(WSJournalpost journalpost) {

        if (inngaende(journalpost)) {
            return new ImmutablePair<>(Entitet.SLUTTBRUKER, Entitet.NAV);
        } else if (utgaende(journalpost)) {
            return new ImmutablePair<>(Entitet.NAV, Entitet.SLUTTBRUKER);
        } else if (inngaende(journalpost)) {
            return new ImmutablePair<>(Entitet.EKSTERN_PART, Entitet.NAV);
        } else if (utgaende(journalpost)) {
            return new ImmutablePair<>(Entitet.NAV, Entitet.EKSTERN_PART);
        } else if (intern(journalpost)) {
            return new ImmutablePair<>(Entitet.NAV, Entitet.NAV);
        } else {
            return new ImmutablePair<>(Entitet.UKJENT, Entitet.UKJENT);
        }
    }

    private LocalDateTime finnDato(WSJournalpost journalpost) {
        switch (journalpost.getKommunikasjonsretning().getValue()) {
            case JOURNALPOST_INNGAAENDE:
                return journalpost.getMottatt().toGregorianCalendar().toZonedDateTime().toLocalDateTime();
            case JOURNALPOST_UTGAAENDE:
                return journalpost.getSendt().toGregorianCalendar().toZonedDateTime().toLocalDateTime();
            case JOURNALPOST_INTERN:
                return journalpost.getFerdigstilt().toGregorianCalendar().toZonedDateTime().toLocalDateTime();
            default:
                return now();
        }
    }

    private Stream<Dokument> finnLogiskeVedlegg(Function<WSSkannetInnhold, Dokument> opprettLogiskDokument, Map<String, List<WSDokumentinfoRelasjon>> relasjoner) {
        return Optional.ofNullable(relasjoner.get(DOKTYPE_HOVEDDOKUMENT)).orElse(new ArrayList<>())
                .stream()
                .map(dokumentRel -> dokumentRel.getJournalfoertDokument().getSkannetInnholdListe())
                .flatMap(List::stream)
                .map(opprettLogiskDokument);
    }

    private static boolean utgaende(WSJournalpost journalpost) {
        return JOURNALPOST_UTGAAENDE.equals(journalpost.getKommunikasjonsretning().getValue());
    }

    private static boolean inngaende(WSJournalpost journalpost) {
        return JOURNALPOST_INNGAAENDE.equals(journalpost.getKommunikasjonsretning().getValue());
    }

    private static boolean intern(WSJournalpost journalpost) {
        return JOURNALPOST_INTERN.equals(journalpost.getKommunikasjonsretning().getValue());
    }
}
