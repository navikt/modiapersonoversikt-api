package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

public class Saker {
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

    public boolean sakerFinnes(){
        return (sakerListeFagsak != null || sakerListeGenerelle != null)
                && (!getSakerListeFagsak().isEmpty() || !getSakerListeGenerelle().isEmpty());
    }
}
