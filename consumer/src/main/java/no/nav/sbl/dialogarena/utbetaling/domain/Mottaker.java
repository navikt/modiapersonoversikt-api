package no.nav.sbl.dialogarena.utbetaling.domain;


import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;

import java.io.Serializable;

public class Mottaker implements Serializable {

    public static final String BRUKER = "bruker";
    public static final String ARBEIDSGIVER = "arbeidsgiver";
    private String mottakertypeType;
    private String navn;

    public Mottaker(String mottakertypeKode, String navn) {
        this.mottakertypeType = mottakertypeKode;
        this.navn = navn;
    }

    public Mottaker(String fnr, WSMottaker wsMottaker) {
        if (wsMottaker == null) {
            return;
        }
        this.navn = wsMottaker.getNavn();
        this.mottakertypeType = transformTilType(wsMottaker.getMottakerId(), fnr);
    }

    private String transformTilType(String mottakerId1, String fnr) {
        return (fnr == null || fnr.equalsIgnoreCase(mottakerId1)) ? BRUKER : ARBEIDSGIVER;
    }

    public String getMottakertypeType() {
        return mottakertypeType;
    }

    public String getNavn() {
        return navn;
    }

}