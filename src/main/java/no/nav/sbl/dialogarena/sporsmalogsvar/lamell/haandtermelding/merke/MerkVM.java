package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke;

import java.io.Serializable;

public class MerkVM implements Serializable {
    public enum MERK_TYPE { KONTORSPERRET, FEILSENDT }

    private MERK_TYPE merkType;

    public MERK_TYPE getMerkType() {
        return merkType;
    }

    public void setMerkType(MERK_TYPE merkType) {
        this.merkType = merkType;
    }

}
