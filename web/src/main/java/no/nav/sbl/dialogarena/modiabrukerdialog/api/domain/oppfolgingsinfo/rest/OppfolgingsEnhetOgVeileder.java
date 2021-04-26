package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.oppfolgingsinfo.rest;

public class OppfolgingsEnhetOgVeileder {
    private Oppfolgingsenhet oppfolgingsenhet;
    private String veilederId;

    public Oppfolgingsenhet getOppfolgingsenhet() {
        return oppfolgingsenhet;
    }

    public void setOppfolgingsenhet(Oppfolgingsenhet oppfolgingsenhet) {
        this.oppfolgingsenhet = oppfolgingsenhet;
    }

    public String getVeilederId() {
        return veilederId;
    }

    public void setVeilederId(String veilederId) {
        this.veilederId = veilederId;
    }
}
