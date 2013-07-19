package no.nav.sbl.dialogarena.sporsmalogsvar.web;

import javax.inject.Inject;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.panel.BesvareSporsmalPanel;
import org.apache.wicket.markup.html.WebPage;


public class BesvareSporsmalPage extends WebPage {

    @Inject
    MeldingService service;

    public BesvareSporsmalPage() {
        add(new BesvareSporsmalPanel("sporsmalogsvar-sporsmal"));
    }

}
