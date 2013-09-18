package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

public enum Tema {
    UFORE("Uføre"), SYKEPENGER("Sykepenger"), PENSJON("Pensjon"), ANNET("Annet");

    private String navn;

    private Tema(String navn) {
        this.navn = navn;
    }

    public String navn() {
        return navn;
    }
}
