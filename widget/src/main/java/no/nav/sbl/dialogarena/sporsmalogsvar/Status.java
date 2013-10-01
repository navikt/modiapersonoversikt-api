package no.nav.sbl.dialogarena.sporsmalogsvar;

public enum Status {
    IKKE_BESVART("Ikke besvart"),
    IKKE_BESVART_INNEN_FRIST("Ikke besvart innen frist"),
    IKKE_LEST_AV_BRUKER("Ikke lest av bruker"),
    LEST_AV_BRUKER("Lest av bruker");

    String statusTekst;
    Status(String statusTekst) {
        this.statusTekst = statusTekst;
    }

    @Override
    public String toString() {
        return statusTekst;
    }
}
