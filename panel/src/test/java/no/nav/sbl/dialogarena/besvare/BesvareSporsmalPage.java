package no.nav.sbl.dialogarena.besvare;

import javax.inject.Inject;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import org.apache.wicket.markup.html.WebPage;


public class BesvareSporsmalPage extends WebPage {

    @Inject
    private SporsmalOgSvarPortType webservice;

    public BesvareSporsmalPage() {
        add(
                new BesvareSporsmalPanel("besvare-sporsmal", webservice.plukkMeldingForBesvaring()),
                new AlleMeldingerPanel("sporsmal-og-svar-liste"));
    }

}
