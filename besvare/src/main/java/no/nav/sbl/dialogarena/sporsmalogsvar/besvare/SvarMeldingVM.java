package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;


import java.io.Serializable;

public class SvarMeldingVM implements Serializable {

	public String behandlingsId, saksid, fritekst, overskrift;
    public boolean sensitiv;
    
    public SvarMeldingVM() {
	}
    
}
