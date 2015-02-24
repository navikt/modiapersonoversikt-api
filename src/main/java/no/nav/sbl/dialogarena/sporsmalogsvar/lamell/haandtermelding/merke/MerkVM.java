package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import java.io.Serializable;

public class MerkVM implements Serializable {
    public enum MerkType {KONTORSPERRET, BIDRAG, FEILSENDT}

    private MerkType merkType;

    public MerkType getMerkType() {
        return merkType;
    }

    public void setMerkType(MerkType merkType) {
        this.merkType = merkType;
    }

    public boolean erKontorsperret() {
        return MerkType.KONTORSPERRET == merkType;
    }

    public boolean erFeilsendt() {
        return MerkType.FEILSENDT == merkType;
    }

    public boolean erMerketBidrag() {
        return MerkType.BIDRAG == merkType;
    }
}
