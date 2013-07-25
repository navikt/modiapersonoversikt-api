package no.nav.sbl.dialogarena.sporsmalogsvar.web;

import javax.inject.Inject;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.SporsmalOgSvarPanel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SporsmalOgSvarPage extends WebPage {
    @Inject
    MeldingService service;

    public SporsmalOgSvarPage(PageParameters parameters) {
        super(parameters);
        add(
                new Innboks("innboks", "28088834986"),
                new SporsmalOgSvarPanel("besvar", "28088834986"));
    }
}
