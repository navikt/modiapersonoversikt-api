package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;

import java.io.Serializable;

public class ExpandableMelding implements Serializable {
    public Melding melding;
    boolean expanded;

    public ExpandableMelding(Melding melding, boolean expanded) {
        this.melding = melding;
        this.expanded = expanded;
    }

}