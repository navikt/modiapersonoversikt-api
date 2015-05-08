package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants;

public class Events {

    public static class SporsmalOgSvar {
        private static final String PREFIX = "sporsmalogsvar.";

        public static final String SVAR_AVBRUTT = PREFIX + "svar.avbrutt";
        public static final String LEGG_TILBAKE_UTFORT = PREFIX + "leggtilbake.utfort";
        public static final String VALGT_MELDING_EVENT = PREFIX + "melding.valgt";
    }

    public static class Brukerprofil {
        private static final String PREFIX = "brukerprofil";

        //Sendes fra no.nav.brukerprofil.BrukerprofilPanel
        public static final String BRUKERPROFIL_OPPDATERT = PREFIX + "Oppdatert";
    }
}
