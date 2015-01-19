package no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain;

import java.io.Serializable;

public class Saker implements Serializable {
    private SakerListe sakerListeFagsak;
    private SakerListe sakerListeGenerelle;

    public Saker() {
    }

    public Saker(SakerListe sakerListeFagsak, SakerListe sakerListeGenerelle) {
        this.sakerListeFagsak = sakerListeFagsak;
        this.sakerListeGenerelle = sakerListeGenerelle;
    }

    public SakerListe getSakerListeFagsak() {
        return sakerListeFagsak;
    }

    public SakerListe getSakerListeGenerelle() {
        return sakerListeGenerelle;
    }

    public boolean sakerFinnes() {
        return (getSakerListeFagsak() != null && !getSakerListeFagsak().isEmpty()) ||
                (getSakerListeGenerelle() != null && !getSakerListeGenerelle().isEmpty());
    }
}
