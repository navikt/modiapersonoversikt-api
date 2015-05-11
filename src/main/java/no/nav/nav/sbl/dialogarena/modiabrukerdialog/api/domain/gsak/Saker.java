package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak;

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
        return fagsakerFinnes() || generelleSakerFinnes();
    }

    private boolean fagsakerFinnes() {
        return getSakerListeFagsak() != null && !getSakerListeFagsak().isEmpty();
    }

    private boolean generelleSakerFinnes() {
        return getSakerListeGenerelle() != null && !getSakerListeGenerelle().isEmpty();
    }
}
