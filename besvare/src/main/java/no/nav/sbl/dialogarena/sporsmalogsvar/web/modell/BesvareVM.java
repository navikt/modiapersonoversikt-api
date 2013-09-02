package no.nav.sbl.dialogarena.sporsmalogsvar.web.modell;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BesvareVM implements Serializable {

    public SporsmalVM sporsmal;
    public SvarVM svar;
	public Map<String, String> sakTemaMapping;

    public BesvareVM() {
        this.sporsmal = new SporsmalVM();
        this.svar = new SvarVM();
        this.sakTemaMapping = new HashMap<>();
    }

    public BesvareVM(SporsmalVM sporsmal, SvarVM svar, Map<String, String> sakTemaMapping) {
        this.sporsmal = sporsmal;
        this.svar = svar;
		this.sakTemaMapping = sakTemaMapping;
    }
    
}
