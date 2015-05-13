package no.nav.sbl.dialogarena.sak.tilgang;

public class TilgangsKontrollResult {

    private boolean harTilgang;
    private TilgangFeilmeldinger feilmelding;

    public TilgangsKontrollResult(boolean harTilgang) {
        this.harTilgang = harTilgang;
    }

    public TilgangsKontrollResult(boolean harTilgang, TilgangFeilmeldinger feilmelding) {
        this.harTilgang = harTilgang;
        this.feilmelding = feilmelding;
    }

    public TilgangFeilmeldinger getFeilmelding() {
        return feilmelding;
    }

    public boolean isHarTilgang() {
        return harTilgang;
    }
}