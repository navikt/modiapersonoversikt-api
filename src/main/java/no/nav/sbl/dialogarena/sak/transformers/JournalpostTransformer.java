package no.nav.sbl.dialogarena.sak.transformers;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet;
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
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.JOARK;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Entitet.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.DokumentMetadataService.*;

public class JournalpostTransformer {

    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;

    public DokumentMetadata dokumentMetadataFraJournalPost(WSJournalpost journalpost, String fnr) throws RuntimeException {
        Map<String, List<WSDokumentinfoRelasjon>> relasjoner = byggRelasjonsMap(journalpost);

        Dokument hoveddokument = finnHoveddokument(opprettDokument, relasjoner);
        Stream<Dokument> vedlegg = finnVedlegg(opprettDokument, relasjoner);
        Stream<Dokument> logiskeVedlegg = finnLogiskeVedlegg(opprettLogiskDokument, relasjoner);

        LocalDateTime dato = finnDato(journalpost);
        Pair<Entitet, Entitet> avsenderMottaker = finnAvsenderMottaker(journalpost.getKommunikasjonsretning(), isErSluttbruker(journalpost, fnr));

        List<WSDokumentinfoRelasjon> dokumentinfoRelasjonListe = journalpost.getDokumentinfoRelasjonListe();
        String kategori = hentKategoriNotatNavn(dokumentinfoRelasjonListe, journalpost.getKommunikasjonsretning());

        return new DokumentMetadata()
                .withJournalpostId(journalpost.getJournalpostId())
                .withHoveddokument(hoveddokument)
                .withVedlegg(
                        concat(vedlegg, logiskeVedlegg)
                                .collect(toList()))
                .withDato(dato)
                .withAvsender(avsenderMottaker.getLeft())
                .withMottaker(avsenderMottaker.getRight())
                .withNavn(finnNavn(journalpost.getEksternPart(), fnr, journalpost.getEksternPartNavn()))
                .withTemakode(journalpost.getArkivtema().getValue())
                .withRetning(Kommunikasjonsretning.fraJournalpostretning(journalpost.getKommunikasjonsretning().getValue()))
                .withBaksystem(JOARK)
                .withKategoriNotat(kategori)
                .withTilhorendeSakid(journalpost.getGjelderSak().getSakId())
                .withTemakodeVisning(bulletproofKodeverkService.getTemanavnForTemakode(journalpost.getArkivtema().getValue(), BulletproofKodeverkService.ARKIVTEMA));
    }

    private String hentKategoriNotatNavn(List<WSDokumentinfoRelasjon> dokumentinfoRelasjonListe, WSKommunikasjonsretninger kommunikasjonsretninger) {
        if (meldingIntern(kommunikasjonsretninger)) {
            return dokumentinfoRelasjonListe.get(0).getJournalfoertDokument().getKategori().getValue();
        }
        return "";
    }

    private boolean isErSluttbruker(WSJournalpost journalpost, String fnr) {
        boolean erSluttbruker = false;
        if (journalpost.getEksternPart() instanceof WSPerson && ((WSPerson) journalpost.getEksternPart()).getIdent().equals(fnr)) {
            erSluttbruker = true;
        }
        return erSluttbruker;
    }

    private String finnNavn(WSAktoer aktoer, String fnr, String fallbacknavn) {
        if (aktoer instanceof WSPerson) {
            WSPerson wsPerson = (WSPerson) aktoer;
            if (!fnr.equals(wsPerson.getIdent())) {
                return wsPerson.getNavn();
            }
            return wsPerson.getNavn();
        } else if (aktoer instanceof WSOrganisasjon) {
            return ((WSOrganisasjon) aktoer).getNavn();
        } else if (fallbacknavn != null && !fallbacknavn.isEmpty()) {
            return fallbacknavn;
        }
        return "ukjent";
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

    Function<WSDokumentinfoRelasjon, Dokument> opprettDokument = (dokumentRel) -> new Dokument()
            .withTittel(dokumentRel.getJournalfoertDokument().getTittel())
            .withLogiskDokument(false)
            .withKanVises(true)
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

    private Pair<Entitet, Entitet> finnAvsenderMottaker(WSKommunikasjonsretninger kommunikasjonsretninger, boolean sluttbruker) {
        if (meldingFraBrukerTilNAV(kommunikasjonsretninger, sluttbruker)) {
            return new ImmutablePair<>(SLUTTBRUKER, NAV);
        } else if (meldingFraNAVtilBruker(kommunikasjonsretninger, sluttbruker)) {
            return new ImmutablePair<>(NAV, SLUTTBRUKER);
        } else if (meldingFraEksternPartTilNAV(kommunikasjonsretninger, sluttbruker)) {
            return new ImmutablePair<>(EKSTERN_PART, NAV);
        } else if (meldingFraNavTilEksternPart(kommunikasjonsretninger, sluttbruker)) {
            return new ImmutablePair<>(NAV, EKSTERN_PART);
        } else if (meldingIntern(kommunikasjonsretninger)) {
            return new ImmutablePair<>(NAV, NAV);
        } else {
            return new ImmutablePair<>(UKJENT, UKJENT);
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

    private static boolean meldingFraNAVtilBruker(WSKommunikasjonsretninger kommunikasjonsretning, boolean erSluttbruker) {
        return JOURNALPOST_UTGAAENDE.equals(kommunikasjonsretning.getValue()) && erSluttbruker;
    }

    private static boolean meldingFraBrukerTilNAV(WSKommunikasjonsretninger kommunikasjonsretning, boolean erSluttbruker) {
        return JOURNALPOST_INNGAAENDE.equals(kommunikasjonsretning.getValue()) && erSluttbruker;
    }

    private static boolean meldingFraNavTilEksternPart(WSKommunikasjonsretninger kommunikasjonsretning, boolean erSluttbruker) {
        return JOURNALPOST_UTGAAENDE.equals(kommunikasjonsretning.getValue()) && !erSluttbruker;
    }

    private static boolean meldingFraEksternPartTilNAV(WSKommunikasjonsretninger kommunikasjonsretning, boolean erSluttbruker) {
        return JOURNALPOST_INNGAAENDE.equals(kommunikasjonsretning.getValue()) && !erSluttbruker;
    }

    private static boolean meldingIntern(WSKommunikasjonsretninger kommunikasjonsretning) {
        return JOURNALPOST_INTERN.equals(kommunikasjonsretning.getValue());
    }
}
