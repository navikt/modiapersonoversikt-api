package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;

public class SporsmalOgSvarVM implements Serializable {

    MeldingVM sporsmal;
    SvarMeldingVM svar;
	Map<String, String> sakTemaMapping;

    public SporsmalOgSvarVM() {
        this(new MeldingVM(new Melding()), new SvarMeldingVM(), new HashMap<String, String>());
    }

    public SporsmalOgSvarVM(MeldingVM sporsmal, SvarMeldingVM svar, Map<String, String> sakTemaMapping) {
        this.sporsmal = sporsmal;
        this.svar = svar;
		this.sakTemaMapping = sakTemaMapping;
    }
    
}
