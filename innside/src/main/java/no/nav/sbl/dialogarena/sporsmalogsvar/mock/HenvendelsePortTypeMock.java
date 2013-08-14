package no.nav.sbl.dialogarena.sporsmalogsvar.mock;

import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock av webservice henvendelse, returnerer tom liste med Henvendelser.
 */
public class HenvendelsePortTypeMock implements HenvendelsePortType {
    @Override
    public boolean ping() {
        return true;
    }

    @Override
    public List<WSHenvendelse> hentHenvendelseListe(@WebParam(name = "fodselsnummer", targetNamespace = "") String s) {
        return new ArrayList<>();
    }
}
