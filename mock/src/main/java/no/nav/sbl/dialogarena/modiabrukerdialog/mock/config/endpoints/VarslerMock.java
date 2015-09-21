package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.melding.domene.brukerdialog.varsler.v1.VarslerPorttype;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.WSHentVarslerRequest;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.WSHentVarslerResponse;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.WSVarsel;
import no.nav.melding.domene.brukerdialog.varsler.v1.meldinger.WSVarselListe;

public class VarslerMock implements VarslerPorttype {

    @Override
    public WSHentVarslerResponse hentVarsler(WSHentVarslerRequest wsHentVarslerRequest) {
        return new WSHentVarslerResponse()
                .withVarselListe(new WSVarselListe().withVarsel(
                    new WSVarsel().withVarseltype("SMS"),
                    new WSVarsel().withVarseltype("EPOST"),
                    new WSVarsel().withVarseltype("NAV.NO")));
    }

    @Override
    public void ping() {

    }
}