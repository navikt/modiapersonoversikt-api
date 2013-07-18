package no.nav.sbl.dialogarena.besvare.web;

import javax.inject.Inject;
import no.nav.sbl.dialogarena.besvare.consumer.MeldingService;
import no.nav.sbl.dialogarena.besvare.consumer.SporsmalOgSvar;
import org.apache.wicket.markup.html.WebPage;


public class BesvareSporsmalPage extends WebPage {

    @Inject
    MeldingService service;

    public BesvareSporsmalPage() {
        SporsmalOgSvar sporsmalOgSvar = service.plukkMelding();
        add(
                new BesvareSporsmalPanel("besvare-sporsmal", sporsmalOgSvar),
                new AlleMeldingerPanel("sporsmal-og-svar-liste", sporsmalOgSvar.sporsmal, "28088834986"));
    }

}
