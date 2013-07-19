package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;

public class ExpandableMelding {
    public Melding melding;
    boolean expanded;

    public ExpandableMelding(Melding melding, boolean expanded) {
        this.melding = melding;
        this.expanded = expanded;
    }

}