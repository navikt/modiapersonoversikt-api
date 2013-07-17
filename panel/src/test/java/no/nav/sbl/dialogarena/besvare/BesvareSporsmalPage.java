package no.nav.sbl.dialogarena.besvare;

import java.util.List;
import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMelding;
import org.apache.wicket.markup.html.WebPage;


public class BesvareSporsmalPage extends WebPage {

    @Inject
    private SporsmalOgSvarPortType webservice;

    public BesvareSporsmalPage() {
        List<WSMelding> meldinger = webservice.hentSporsmalOgSvarListe("28088834986");
        add(
                new BesvareSporsmalPanel("besvare-sporsmal",
                        meldinger.isEmpty() ? new WSMelding() : meldinger.get(0)),
                new AlleMeldingerPanel("sporsmal-og-svar-liste"));
    }

}
