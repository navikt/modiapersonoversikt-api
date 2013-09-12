package no.nav.sbl.dialogarena.sporsmalogsvar.mock;

import java.util.Random;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.BesvareHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.besvare.v1.informasjon.WSSvar;
import org.joda.time.DateTime;

public class BesvareHenvendelsePortTypeMock implements BesvareHenvendelsePortType {

    public static final String TRAAD = "1";

    @Override
    public void besvarSporsmal(WSSvar wsSvar) {
    }

    @Override
    public WSSporsmalOgSvar hentSporsmalOgSvar(String oppgaveId) {
        Random random = new Random();
        WSSporsmal spsm = new WSSporsmal()
                .withFritekst("Lorem ipsum dolor sit amet, " +
                        "consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad " +
                        "minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure " +
                        "dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto")
                .withOpprettet(DateTime.now())
                .withTema("Pensjon")
                .withTraad(TRAAD);
        WSSvar svar = new WSSvar().withBehandlingsId("" + random.nextInt());
        return new WSSporsmalOgSvar().withSporsmal(spsm).withSvar(svar);
    }

    @Override
    public boolean ping() {
    	return true;
    }
    
}
