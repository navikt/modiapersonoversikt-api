package no.nav.sbl.dialogarena.sporsmalogsvar;

import java.io.Serializable;
import no.nav.modig.modia.model.FeedItemVM;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class MeldingVM implements FeedItemVM, Serializable {

    private String id;
    private String avsender, tema;
    private DateTime dato;

    public MeldingVM(String id) {
        this.id = id;
    }


    public String getDato() {
        return DateTimeFormat.forPattern("dd.MM.yyyy 'kl' HH.mm").print(dato);
    }

    public void setDato(DateTime dato) {
        this.dato = dato;
    }

    public String getAvsender() {
        return avsender;
    }

    public void setAvsender(String avsender) {
        this.avsender = avsender;
    }

    public String getTema() {
        return tema;
    }


    public void setTema(String tema) {
        this.tema = tema;
    }

    @Override
    public String getType() {
        return "Melding";
    }

    @Override
    public String getId() {
        return id;
    }
}
