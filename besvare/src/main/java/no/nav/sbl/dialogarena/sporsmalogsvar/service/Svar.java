package no.nav.sbl.dialogarena.sporsmalogsvar.service;

import org.joda.time.DateTime;

import java.io.Serializable;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype.UTGAENDE;

/**
 * Mutable svar-objekt som brukes ved innlegging av svar fra saksbehandler.
 * Når svaret er ferdig kan {@link #getFerdigSvar()} kalles for å få et
 * immutable {@link Melding}-objekt for inkludering i tråden.
 */
public class Svar implements Serializable {

    public boolean sensitiv;

    public String behandlingId,
                  tema,
                  fritekst;


    public Melding getFerdigSvar() {
        Melding svar = new Melding(behandlingId, UTGAENDE, DateTime.now(), fritekst);
        svar.tidligereHenvendelse.setObject(false);
        return svar;
    }

}
