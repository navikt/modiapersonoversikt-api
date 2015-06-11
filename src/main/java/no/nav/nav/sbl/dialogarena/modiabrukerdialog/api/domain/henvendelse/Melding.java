package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

public class Melding implements Serializable {

    public String id, traadId, fnrBruker, navIdent, oppgaveId, temagruppe, temagruppeNavn, kanal, fritekst, kontorsperretEnhet, journalfortTema,
            journalfortTemanavn, journalfortSaksId, journalfortAvNavIdent, eksternAktor, tilknyttetEnhet, markertSomFeilsendtAv, statusTekst, statusKlasse,
            lestStatus, opprettetDatoTekst;
    public DateTime lestDato, opprettetDato, journalfortDato;
    public Meldingstype meldingstype;
    public Temagruppe gjeldendeTemagruppe;
    public Status status;
    public boolean kassert, ingenTilgangJournalfort;
    public Boolean erTilknyttetAnsatt;

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

    public Melding withErTilknyttetAnsatt(Boolean erTilknyttetAnsatt) {
        this.erTilknyttetAnsatt = erTilknyttetAnsatt;
        return this;
    }

    public Melding withGjeldendeTemagruppe(Temagruppe gjeldendeTemagruppe) {
        this.gjeldendeTemagruppe = gjeldendeTemagruppe;
        return this;
    }

    public static final Comparator<Melding> ELDSTE_FORST = new Comparator<Melding>() {
        @Override
        public int compare(Melding o1, Melding o2) {
            return o1.opprettetDato.compareTo(o2.opprettetDato);
        }
    };


    public static final Comparator<Melding> NYESTE_FORST = new Comparator<Melding>() {
        @Override
        public int compare(Melding o1, Melding o2) {
            return o2.opprettetDato.compareTo(o1.opprettetDato);
        }
    };

    public static final Transformer<Melding, String> ID = new Transformer<Melding, String>() {
        @Override
        public String transform(Melding melding) {
            return melding.id;
        }
    };

    public static final Transformer<Melding, String> TRAAD_ID = new Transformer<Melding, String>() {
        @Override
        public String transform(Melding melding) {
            return melding.traadId;
        }
    };

}
