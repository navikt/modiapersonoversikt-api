package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import java.io.Serializable;

public class MerkVM implements Serializable {
    public enum MerkType {KONTORSPERRET, FEILSENDT}

    private MerkType merkType;

    public MerkType getMerkType() {
        return merkType;
    }

    public void setMerkType(MerkType merkType) {
        this.merkType = merkType;
    }

}
