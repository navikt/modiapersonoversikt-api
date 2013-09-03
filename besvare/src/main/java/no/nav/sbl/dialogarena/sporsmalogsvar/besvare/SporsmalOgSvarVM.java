package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;


import java.io.Serializable;
import java.util.Map;

public class SporsmalOgSvarVM implements Serializable {

    SvarMeldingVM svar;
	Map<String, String> sakTemaMapping;

    public SporsmalOgSvarVM() {}

    public SporsmalOgSvarVM(SvarMeldingVM svar, Map<String, String> sakTemaMapping) {
        this.svar = svar;
		this.sakTemaMapping = sakTemaMapping;
    }
    
}
