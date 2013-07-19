package no.nav.sbl.dialogarena.sporsmalogsvar.web;

import java.util.List;
import javax.inject.Inject;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.panel.BesvareSporsmalPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.panel.Innboks;
import org.apache.wicket.markup.html.WebPage;


public class BesvareSporsmalPage extends WebPage {

    @Inject
    MeldingService service;

    public BesvareSporsmalPage() {
//        SporsmalOgSvar sporsmalOgSvar = service.plukkMelding();
        List<Melding> meldinger = service.hentAlleMeldinger("28088834986");
        add(
                new BesvareSporsmalPanel("sporsmalogsvar-sporsmal"), //, sporsmalOgSvar),
                new Innboks("sporsmalogsvar-liste", meldinger.get(0), meldinger));
    }

}
