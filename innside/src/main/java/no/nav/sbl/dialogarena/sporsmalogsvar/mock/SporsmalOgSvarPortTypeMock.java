package no.nav.sbl.dialogarena.sporsmalogsvar.mock;

import java.util.List;
import javax.jws.WebParam;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSvar;
import org.joda.time.DateTime;

import static no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMeldingstype.SPORSMAL;
import static no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMeldingstype.SVAR;

public class SporsmalOgSvarPortTypeMock implements SporsmalOgSvarPortType {

    @Override
    public WSSporsmalOgSvar plukkMeldingForBesvaring(String aktorId) {
        WSMelding sporsmal = new WSMelding()
                .withFritekst("Jeg lurer på ... ???")
                .withOverskrift("Et spørsmål")
                .withType(SPORSMAL)
                .withOpprettet(DateTime.now())
                .withTema("Tema")
                .withId("1")
                .withTraadId("1");

        WSMelding svar = new WSMelding()
                .withFritekst("Et svar.")
                .withType(SVAR)
                .withTraadId("1")
                .withId("2")
                .withTema("Tema")
                .withOpprettet(DateTime.now().plusMinutes(1));

        return new WSSporsmalOgSvar().withSporsmal(sporsmal).withSvar(svar);
    }

    @Override
    public String opprettSporsmal(@WebParam(name = "sporsmal", targetNamespace = "") WSSporsmal wsSporsmal, @WebParam(name = "aktorId", targetNamespace = "") String s) {
        return null;
    }

    @Override
    public void besvarSporsmal(@WebParam(name = "svar", targetNamespace = "") WSSvar wsSvar) {
    }

    @Override
    public boolean ping() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WSMelding hentMelding(@WebParam(name = "behandlingsId", targetNamespace = "") String s) {
        return null;
    }

    @Override
    public List<WSMelding> hentMeldingListe(@WebParam(name = "aktorId", targetNamespace = "") String s) {
        return null;
    }
}
