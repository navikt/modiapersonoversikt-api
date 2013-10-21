package no.nav.sbl.dialogarena.mottaksbehandling.oppgave;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Tema {

	ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT("BAR"),
    FAMILIE_OG_BARN("BID"),
    HJELPEMIDLER("HJE"),
    INTERNASJONALT("BIL"),
    PENSJON("PEN"),
    SOSIALE_TJENESTER("ENF"),
    UFOREPENSJON("FOR"),
    OVRIGE_HENVENDELSER("GRA");

    public static final Map<String, Tema> FAGOMRADEKODER;

    static {
    	Map<String, Tema> mapping = new HashMap<>();
    	for (Tema tema : values()) {
    		mapping.put(tema.fagomradekode, tema);
    	}
    	FAGOMRADEKODER = Collections.unmodifiableMap(mapping);
    }

    public final String fagomradekode;

    private Tema(String fagomradekode) {
        this.fagomradekode = fagomradekode;
    }

}
