package no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain;

import java.io.Serializable;
import java.util.Objects;

public class Kodeverdi implements Serializable {

    private String kodeRef;
    private String kodeverkRef;
    private String beskrivelse;
    private boolean gyldig;

    public Kodeverdi(String kodeRef, String beskrivelse) {
        this.beskrivelse = beskrivelse;
        this.kodeRef = kodeRef;
    }

    public Kodeverdi() {
    }

    public String getKodeRef() {
        return kodeRef;
    }

    public void setKodeRef(String kodeRef) {
        this.kodeRef = kodeRef;
    }

    public String getKodeverkRef() {
        return kodeverkRef;
    }

    public void setKodeverkRef(String kodeverkRef) {
        this.kodeverkRef = kodeverkRef;
    }

    @Override
    public String toString() {
        return beskrivelse != null ? beskrivelse : kodeRef;
    }

    public String getBeskrivelse() {
        return beskrivelse != null ? beskrivelse : kodeRef;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }

    public boolean isGyldig() {
        return gyldig;
    }

    public void setGyldig(boolean gyldig) {
        this.gyldig = gyldig;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Kodeverdi kodeverdi = (Kodeverdi) other;
        return Objects.equals(this.kodeRef, kodeverdi.kodeRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kodeRef, kodeverkRef, beskrivelse, gyldig);
    }

    public static class With {

        private final Kodeverdi kodeverdi = new Kodeverdi();

        public With kodeRef(String kode) {
            kodeverdi.kodeRef = kode;
            return this;
        }

        public Kodeverdi done() {
            return kodeverdi;
        }
    }
}
