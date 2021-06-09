package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;

public class Melding implements Serializable {

    public String id, traadId, fnrBruker, navIdent, oppgaveId, temagruppe, temagruppeNavn, kanal, kontorsperretEnhet,
            journalpostId, journalfortTema, journalfortTemanavn, journalfortSaksId, journalfortAvNavIdent, eksternAktor,
            tilknyttetEnhet, brukersEnhet, statusTekst, statusKlasse, lestStatus, visningsDatoTekst,
            journalfortDatoTekst, ferdigstiltUtenSvarDatoTekst, markertSomFeilsendtDatoTekst, kontorsperretDatoTekst, ikontekst, kontorsperretAvNavIdent, markertSomFeilsendtAvNavIdent,
            ferdigstiltUtenSvarAvNavIdent;
    public DateTime lestDato, opprettetDato, journalfortDato, ferdigstiltDato, ferdigstiltUtenSvarDato,
            markertSomFeilsendtDato, kontorsperretDato;
    public Meldingstype meldingstype;
    public Temagruppe gjeldendeTemagruppe;
    public Status status;
    public boolean kassert, ingenTilgangJournalfort, erDokumentMelding, erOppgaveMelding, erFerdigstiltUtenSvar;
    public Boolean erTilknyttetAnsatt;
    public Saksbehandler kontorsperretAv, markertSomFeilsendtAv, ferdigstiltUtenSvarAv;
    public Person journalfortAv = new Person("", "");
    public Person skrevetAv = new Person("", "");

    private final List<Fritekst> fritekster;

    public Melding() {
        fritekster = new ArrayList<>();
    }

    public Melding(String id, Meldingstype meldingstype, DateTime dato) {
        this.id = id;
        this.meldingstype = meldingstype;
        this.ferdigstiltDato = dato;
        this.fritekster = new ArrayList<>();
    }

    public Melding withId(String id) {
        this.id = id;
        return this;
    }

    public Melding withTraadId(String traadId) {
        this.traadId = traadId;
        return this;
    }

    public Melding withFnr(String fnrBruker) {
        this.fnrBruker = fnrBruker;
        return this;
    }

    public Melding withOppgaveId(String oppgaveId) {
        this.oppgaveId = oppgaveId;
        return this;
    }

    public Melding withType(Meldingstype type) {
        this.meldingstype = type;
        return this;
    }

    public Melding withTemagruppe(String temagruppe) {
        this.temagruppe = temagruppe;
        return this;
    }

    public Melding withKanal(String kanal) {
        this.kanal = kanal;
        return this;
    }

    public Melding withFritekst(Fritekst... fritekster) {
        if (asList(fritekster).contains(null)) {
            throw new IllegalArgumentException("Fritekst kan ikke være null");
        }
        this.fritekster.clear();
        this.fritekster.addAll(asList(fritekster));
        return this;
    }

    public Melding withKontorsperretEnhet(String kontorsperretEnhet) {
        this.kontorsperretEnhet = kontorsperretEnhet;
        return this;
    }

    public Melding withFerdigstiltDato(DateTime dato) {
        this.ferdigstiltDato = dato;
        return this;
    }

    public Melding withJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
        return this;
    }

    public Melding withJournalfortTemaNavn(String journalfortTemanavn) {
        this.journalfortTemanavn = journalfortTemanavn;
        return this;
    }

    public Melding withJournalfortTema(String journalfortTema) {
        this.journalfortTema = journalfortTema;
        return this;
    }

    public Melding withJournalfortSaksId(String journalfortSaksId) {
        this.journalfortSaksId = journalfortSaksId;
        return this;
    }

    public Melding withJournalfortAvNavIdent(String journalfortAvNavIdent) {
        this.journalfortAvNavIdent = journalfortAvNavIdent;
        return this;
    }

    public Melding withJournalfortDato(DateTime journalfortDato) {
        this.journalfortDato = journalfortDato;
        return this;
    }

    public Melding withEksternAktor(String eksternAktor) {
        this.eksternAktor = eksternAktor;
        return this;
    }

    public Melding withTilknyttetEnhet(String tilknyttetEnhet) {
        this.tilknyttetEnhet = tilknyttetEnhet;
        return this;
    }

    public Melding withBrukersEnhet(String brukersEnhet) {
        this.brukersEnhet = brukersEnhet;
        return this;
    }

    public Melding withErTilknyttetAnsatt(Boolean erTilknyttetAnsatt) {
        this.erTilknyttetAnsatt = erTilknyttetAnsatt;
        return this;
    }

    public Melding withGjeldendeTemagruppe(Temagruppe gjeldendeTemagruppe) {
        this.gjeldendeTemagruppe = gjeldendeTemagruppe;
        return this;
    }

    public Melding withErFerdigstiltUtenSvar(boolean erFerdigstiltUtenSvar) {
        this.erFerdigstiltUtenSvar = erFerdigstiltUtenSvar;
        return this;
    }

    public DateTime getVisningsDato() {
        return ferdigstiltDato;
    }

    public String getTraadId() {
        return traadId;
    }

    public boolean erFraSaksbehandler() {
        return VisningUtils.FRA_NAV.contains(meldingstype);
    }

    public boolean erVarsel() {
        return VisningUtils.VARSEL.contains(meldingstype);
    }

    public boolean erSporsmal() {
        return VisningUtils.SPORSMAL.contains(meldingstype);
    }

    public boolean erBesvart() {
        return status != Status.IKKE_BESVART;
    }

    public boolean erSvarSkriftlig() {
        return meldingstype.equals(Meldingstype.SVAR_SKRIFTLIG);
    }

    public boolean erSporsmalSkriftlig() {
        return meldingstype.equals(Meldingstype.SPORSMAL_SKRIFTLIG)
                || meldingstype.equals(Meldingstype.SPORSMAL_SKRIFTLIG_DIREKTE);
    }

    public boolean erDelvisSvar() {
        return Meldingstype.DELVIS_SVAR_SKRIFTLIG.equals(meldingstype);
    }

    public static final Comparator<Melding> ELDSTE_FORST = (o1, o2) -> o1.getVisningsDato().compareTo(o2.getVisningsDato());

    public static final Comparator<Melding> NYESTE_FORST = (o1, o2) -> o2.getVisningsDato().compareTo(o1.getVisningsDato());

    public static final Function<Melding, String> ID = melding -> melding.id;

    public static final Function<Melding, String> TRAAD_ID = melding -> melding.traadId;

    public static final Function<Melding, Meldingstype> TYPE = melding -> melding.meldingstype;

    public static Optional<Melding> siste(List<Melding> traad) {
        return traad.stream()
                .sorted(comparing(Melding::getVisningsDato).reversed())
                .findFirst();
    }

    public Melding withDelviseSvar(List<Melding> delviseSvar) {
        fritekster.addAll(delviseSvar.stream()
                .map(delvisSvar -> new Fritekst(delvisSvar.getFritekst(), delvisSvar.skrevetAv, delvisSvar.ferdigstiltDato))
                .collect(Collectors.toList()));
        return this;
    }

    public String getFritekst() {
        return this.getFriteksterMedEldsteForst()
                .stream()
                .map(Fritekst::getFritekst)
                .collect(Collectors.joining("\n\u00A0\n"));
    }

    public String getSkrevetAv() {
        return this.getFriteksterMedEldsteForst()
                .stream()
                .map((fritekst) ->
                        fritekst.getSaksbehandler()
                        .map((saksbehandler) -> String.format("%s (%s)", saksbehandler.navn, saksbehandler.getIdent()))
                        .orElse("Ukjent"))
                .collect(Collectors.joining(" og "));
    }

    public Melding withNavIdent(String navIdent) {
        this.navIdent = navIdent;
        return this;
    }

    public List<Fritekst> getFriteksterMedEldsteForst() {
        return fritekster.stream().sorted(Fritekst.ELDSTE_FORST).collect(Collectors.toList());
    }

    public Melding withSkrevetAv(Person person) {
        this.skrevetAv = person;
        return this;
    }
}
