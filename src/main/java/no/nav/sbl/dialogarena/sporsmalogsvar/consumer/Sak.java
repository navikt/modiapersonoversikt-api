package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import org.joda.time.DateTime;

import java.io.Serializable;

public class Sak implements Serializable {

    public String saksId, tema;
    public DateTime opprettetDato;
}
