package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util;

import no.nav.virksomhet.tjenester.utbetaling.v2.UtbetalingPortType;

public class UtbetalingPortTypeWrapper {

    private UtbetalingPortType portType;

    public UtbetalingPortTypeWrapper(UtbetalingPortType portType) {
        this.portType = portType;
    }

    public UtbetalingPortType getPortType() {
        return portType;
    }
}
