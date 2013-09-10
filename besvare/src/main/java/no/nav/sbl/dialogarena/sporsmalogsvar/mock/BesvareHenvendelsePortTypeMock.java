package no.nav.sbl.dialogarena.sporsmalogsvar.mock;

import java.util.Random;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import org.joda.time.DateTime;

public class BesvareHenvendelsePortTypeMock implements BesvareHenvendelsePortType {

    @Override
    public void besvarSporsmal(WSSvar wsSvar) {
    }

    @Override
    public WSSporsmalOgSvar hentSporsmalOgSvar(String oppgaveId) {
        Random random = new Random();
        WSSporsmal spsm = new WSSporsmal()
                .withFritekst("Fritekst")
                .withOpprettet(DateTime.now())
                .withOverskrift("overskrift")
                .withTema("tema");
        WSSvar svar = new WSSvar().withBehandlingsId("" + random.nextInt());
        return new WSSporsmalOgSvar().withSporsmal(spsm).withSvar(svar);
    }

    @Override
    public boolean ping() {
    	return true;
    }
    
}
