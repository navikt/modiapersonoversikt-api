package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

public class Henvendelse implements Serializable {

    public String id, traadId, fnr, navIdent, oppgaveId, temagruppe, kanal, fritekst, kontorsperretEnhet,
            journalfortTema, journalfortSaksId, journalfortAvNavIdent;
    public DateTime opprettetDato, journalfortDato;
    public Henvendelsetype type;

    public enum Henvendelsetype {
        SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, REFERAT_OPPMOTE, REFERAT_TELEFON, SPORSMAL_SKRIFTLIG, SPORSMAL_MODIA_UTGAAENDE, SVAR_SBL_INNGAAENDE
    }

    public Henvendelse withId(String id) {
        this.id = id;
        return this;
    }

    public Henvendelse withTraadId(String traadId) {
        this.traadId = traadId;
        return this;
    }

    public Henvendelse withFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public Henvendelse withOppgaveId(String oppgaveId) {
        this.oppgaveId = oppgaveId;
        return this;
    }

    public Henvendelse withType(Henvendelsetype type) {
        this.type = type;
        return this;
    }

    public Henvendelse withNavIdent(String navIdent) {
        this.navIdent = navIdent;
        return this;
    }

    public Henvendelse withTemagruppe(String temagruppe) {
        this.temagruppe = temagruppe;
        return this;
    }

    public Henvendelse withKanal(String kanal) {
        this.kanal = kanal;
        return this;
    }

    public Henvendelse withFritekst(String fritekst) {
        this.fritekst = fritekst;
        return this;
    }

    public Henvendelse withKontorsperretEnhet(String kontorsperretEnhet) {
        this.kontorsperretEnhet = kontorsperretEnhet;
        return this;
    }

    public Henvendelse withOpprettetDato(DateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
        return this;
    }

    public Henvendelse withJournalfortTema(String journalfortTema) {
        this.journalfortTema = journalfortTema;
        return this;
    }

    public Henvendelse withJournalfortSaksId(String journalfortSaksId) {
        this.journalfortSaksId = journalfortSaksId;
        return this;
    }

    public Henvendelse withJournalfortAvNavIdent(String journalfortAvNavIdent) {
        this.journalfortAvNavIdent = journalfortAvNavIdent;
        return this;
    }

    public Henvendelse withJournalfortDato(DateTime journalfortDato) {
        this.journalfortDato = journalfortDato;
        return this;
    }

    public static final Comparator<Henvendelse> ELDSTE_FORST = new Comparator<Henvendelse>() {
        @Override
        public int compare(Henvendelse o1, Henvendelse o2) {
            return o1.opprettetDato.compareTo(o2.opprettetDato);
        }
    };

}
