package no.nav.sbl.dialogarena.sporsmalogsvar.web;

import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.SporsmalOgSvarPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import org.apache.wicket.markup.html.WebPage;

import javax.inject.Inject;

public class SporsmalOgSvarPage extends WebPage {
    @Inject
    MeldingService service;

    public SporsmalOgSvarPage() {
        super();
        add(
                new Innboks("innboks", "28088834986"),
                new SporsmalOgSvarPanel("besvar", "28088834986"));
    }
}
