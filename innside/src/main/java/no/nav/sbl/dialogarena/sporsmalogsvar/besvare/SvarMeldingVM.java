package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import java.io.Serializable;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;

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
