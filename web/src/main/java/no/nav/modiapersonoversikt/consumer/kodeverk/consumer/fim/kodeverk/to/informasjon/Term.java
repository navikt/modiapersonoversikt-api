package no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.informasjon;

import java.io.Serializable;

public class Term extends Kodeverkselement implements Serializable {

    private String spraak;
    private Tekstobjekt beskrivelse;

    public String getSpraak() {
        return spraak;
    }

    public void setSpraak(String spraak) {
        this.spraak = spraak;
    }

    public Tekstobjekt getBeskrivelse() {
        return beskrivelse;
    }

    public void setBeskrivelse(Tekstobjekt beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
}
