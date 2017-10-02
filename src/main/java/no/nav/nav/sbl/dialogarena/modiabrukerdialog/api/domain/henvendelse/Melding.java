package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.*;

import static java.util.Comparator.comparing;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.*;

public class Melding implements Serializable {

    public String id, traadId, fnrBruker, navIdent, oppgaveId, temagruppe, temagruppeNavn, kanal, fritekst, kontorsperretEnhet, journalfortTema,
            journalfortTemanavn, journalfortSaksId, journalfortAvNavIdent, eksternAktor, tilknyttetEnhet, brukersEnhet, markertSomFeilsendtAv, statusTekst, statusKlasse,
            lestStatus, visningsDatoTekst , journalfortDatoTekst;
    public DateTime lestDato, opprettetDato, journalfortDato, ferdigstiltDato, visningsDato;
    public Meldingstype meldingstype;
    public Temagruppe gjeldendeTemagruppe;
    public Status status;
    public boolean kassert, ingenTilgangJournalfort, erDokumentMelding, erOppgaveMelding;
    public Boolean erTilknyttetAnsatt;
    public Person skrevetAv = new Person("", ""), journalfortAv = new Person("", "");

    public Melding() {
    }

    public Melding(String id, Meldingstype meldingstype, DateTime opprettetDato) {
        this.id = id;
        this.meldingstype = meldingstype;
        this.opprettetDato = opprettetDato;
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

    public Melding withNavIdent(String navIdent) {
        this.navIdent = navIdent;
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

    public Melding withFritekst(String fritekst) {
        this.fritekst = fritekst;
        return this;
    }

    public Melding withKontorsperretEnhet(String kontorsperretEnhet) {
        this.kontorsperretEnhet = kontorsperretEnhet;
        return this;
    }

    public Melding withOpprettetDato(DateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
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

    public DateTime getVisningsDato() {
        if (erDokumentMelding) {
            return ferdigstiltDato;
        }
        return opprettetDato;
    }

    public String getTraadId() {
        return traadId;
    }

    public boolean erFraSaksbehandler() {
        return FRA_NAV.contains(meldingstype);
    }

    public boolean erVarsel() {
        return VARSEL.contains(meldingstype);
    }

    public boolean erSporsmal() {
        return SPORSMAL.contains(meldingstype);
    }

    public boolean erBesvart() {
        return status != Status.IKKE_BESVART;
    }

    public static final Comparator<Melding> ELDSTE_FORST = (o1, o2) -> o1.getVisningsDato().compareTo(o2.getVisningsDato());

    public static final Comparator<Melding> NYESTE_FORST = (o1, o2) -> o2.getVisningsDato().compareTo(o1.getVisningsDato());

    public static final Transformer<Melding, String> ID = melding -> melding.id;

    public static final Transformer<Melding, String> TRAAD_ID = melding -> melding.traadId;

    public static final Transformer<Melding, Meldingstype> TYPE = melding -> melding.meldingstype;

    public static Optional<Melding> siste(List<Melding> traad) {
        return traad.stream()
                .sorted(comparing(Melding::getVisningsDato).reversed())
                .findFirst();
    }

}
