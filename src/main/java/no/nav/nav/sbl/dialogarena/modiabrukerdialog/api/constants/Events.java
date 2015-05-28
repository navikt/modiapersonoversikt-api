package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants;

public class Events {

    public static class SporsmalOgSvar {
        private static final String PREFIX = "sporsmalogsvar.";

        public static final String SVAR_AVBRUTT = PREFIX + "svar.avbrutt";
        public static final String LEGG_TILBAKE_UTFORT = PREFIX + "leggtilbake.utfort";
        public static final String MELDING_VALGT = PREFIX + "melding.valgt";
        public static final String SVAR_PAA_MELDING = PREFIX + "svar.paa.melding";
        public static final String MELDING_SENDT_TIL_BRUKER = PREFIX + "melding.sendt.til.bruker";
    }

    public static class Brukerprofil {
        private static final String PREFIX = "brukerprofil";

        //Sendes fra no.nav.brukerprofil.BrukerprofilPanel
        public static final String BRUKERPROFIL_OPPDATERT = PREFIX + "Oppdatert";
    }
}
