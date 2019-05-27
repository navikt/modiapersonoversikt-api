package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.personoppslag;

import java.util.List;

public class PersonOppslagResponse {
    private List<KontaktiformasjonForDoedsbo> kontaktinformasjonForDoedsbo;

    public List<KontaktiformasjonForDoedsbo> getKontaktinformasjonForDoedsbo() {
        return kontaktinformasjonForDoedsbo;
    }

    public void setKontaktinformasjonForDoedsbo(List<KontaktiformasjonForDoedsbo> kontaktinformasjonForDoedsbo) {
        this.kontaktinformasjonForDoedsbo = kontaktinformasjonForDoedsbo;
    }
}
