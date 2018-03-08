package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

public class MeldingerSokResultat {
    public String fritekst, temagruppe, arkivtema, dato, navIdent, statustekst, lestStatus, kanal, skrevetAvNavn,
            journalfortAvNavn, journalfortAvIdent, journalfortDato, journalfortSaksId, ikontekst;

    public MeldingerSokResultat withFritekst(String fritekst) {
        this.fritekst = fritekst;
        return this;
    }

    public MeldingerSokResultat withTemagruppe(String temagruppe) {
        this.temagruppe = temagruppe;
        return this;
    }

    public MeldingerSokResultat withArkivtema(String arkivtema) {
        this.arkivtema = arkivtema;
        return this;
    }

    public MeldingerSokResultat withDato(String dato) {
        this.dato = dato;
        return this;
    }

    public MeldingerSokResultat withNavident(String navIdent) {
        this.navIdent = navIdent;
        return this;
    }

    public MeldingerSokResultat withStatustekst(String statustekst) {
        this.statustekst = statustekst;
        return this;
    }

    public MeldingerSokResultat withLestStatus(String lestStatus) {
        this.lestStatus = lestStatus;
        return this;
    }

    public MeldingerSokResultat withKanal(String kanal) {
        this.kanal = kanal;
        return this;
    }

    public MeldingerSokResultat withSkrevetAvNavn(String skrevetAvNavn) {
        this.skrevetAvNavn = skrevetAvNavn;
        return this;
    }

    public MeldingerSokResultat withJournalfortAvNavn(String journalfortAvNavn) {
        this.journalfortAvNavn = journalfortAvNavn;
        return this;
    }

    public MeldingerSokResultat withJournalfortAvIdent(String journalfortAvIdent) {
        this.journalfortAvIdent = journalfortAvIdent;
        return this;
    }

    public MeldingerSokResultat withJournalfortDato(String journalfortDato) {
        this.journalfortDato = journalfortDato;
        return this;
    }

    public MeldingerSokResultat withJournalfortSaksId(String journalfortSaksId) {
        this.journalfortSaksId = journalfortSaksId;
        return this;
    }

    public MeldingerSokResultat withIkontekst(String ikontekst) {
        this.ikontekst = ikontekst;
        return this;
    }

}