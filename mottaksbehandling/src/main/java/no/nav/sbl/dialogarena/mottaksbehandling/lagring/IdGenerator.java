package no.nav.sbl.dialogarena.mottaksbehandling.lagring;

public class IdGenerator {

    public static final String APPLIKASJONSID = "BD09";
    private static final String APPLIKASJON_PREFIX_BASE_36 = "17";

    public static String lagBehandlingsId(long databasenokkel) {
    	Long base = Long.parseLong(APPLIKASJON_PREFIX_BASE_36 + "0000000",36);
        String behandlingsId = Long.toString(base + databasenokkel, 36).toUpperCase().replace("O", "o").replace("I","i");
        if (!behandlingsId.startsWith(APPLIKASJON_PREFIX_BASE_36)) {
            throw new RuntimeException("Tildelt sekvensrom for behandlingsId er brukt opp. Kan ikke generere behandlingsId " + behandlingsId);
        }
		return behandlingsId;
    }

}
