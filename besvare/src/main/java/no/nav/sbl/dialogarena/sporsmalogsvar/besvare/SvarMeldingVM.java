package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;

import java.io.Serializable;

public class SvarMeldingVM implements Serializable {

	public String behandlingsId, saksid, fritekst, overskrift;
    public boolean sensitiv;
    
    public SvarMeldingVM() {
	}
    
	public SvarMeldingVM(Melding svar) {
		behandlingsId = svar.id;
		overskrift = svar.overskrift;
    }
    
}
