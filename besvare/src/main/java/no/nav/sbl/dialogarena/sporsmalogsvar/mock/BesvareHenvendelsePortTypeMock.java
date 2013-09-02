package no.nav.sbl.dialogarena.sporsmalogsvar.mock;

import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;

public class BesvareHenvendelsePortTypeMock implements BesvareHenvendelsePortType {

    @Override
    public void besvarSporsmal(WSSvar wsSvar) {
    }

    @Override
    public boolean ping() {
    	return true;
    }
    
}
