package no.nav.sbl.dialogarena.utbetaling.domain;


import no.nav.virksomhet.okonomi.utbetaling.v2.WSMottaker;

import java.io.Serializable;

public class Mottaker implements Serializable {

    private String mottakerId;
    private String mottakertypeKode;
    private String navn;

    public Mottaker(String mottakerId, String mottakertypeKode, String navn) {
        this.mottakerId = mottakerId;
        this.mottakertypeKode = mottakertypeKode;
        this.navn = navn;
    }

    public Mottaker(WSMottaker wsMottaker) {
        if (wsMottaker == null) { return; }
        this.mottakerId = wsMottaker.getMottakerId();
        this.mottakertypeKode = wsMottaker.getMottakertypeKode();
        this.navn = wsMottaker.getNavn();
    }
    public String getMottakerId() {
        return mottakerId;
    }

    public String getMottakertypeKode() {
        return mottakertypeKode;
    }

    public String getNavn() {
        return navn;
    }

}