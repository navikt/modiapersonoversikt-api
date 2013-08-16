package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.sendsporsmal;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Sporsmal implements Serializable {
    
	private String tema, fritekst;
	public DateTime innsendingsTidspunkt;

    public void setTema(String tema) {
        this.tema = tema;
    }

    public void setFritekst(String fritekst) {
        this.fritekst = fritekst;
    }

    public String getTema() {
        return tema;
    }

    public String getFritekst() {
        return fritekst;
    }

}