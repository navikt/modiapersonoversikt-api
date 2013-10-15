package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgavevalg;

public enum Aarsak {

    INHABIL("Jeg er inhabil"),
    ANNEN("Annen årsak");

    private final String tekst;

    private Aarsak(String tekst) {
        this.tekst = tekst;
    }

    public String getTekst() {
        return tekst;
    }

}
