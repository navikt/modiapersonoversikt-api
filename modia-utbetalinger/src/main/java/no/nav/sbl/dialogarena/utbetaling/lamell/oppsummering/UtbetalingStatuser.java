package no.nav.sbl.dialogarena.utbetaling.lamell.oppsummering;

public enum UtbetalingStatuser {
    RETURNERT_TIL_NAV("Returnert til NAV for ny behandling");

    public final String utbetalingstatus;

    UtbetalingStatuser(String utbetalingstatus) {
        this.utbetalingstatus = utbetalingstatus;
    }
}
