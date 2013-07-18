package no.nav.sbl.dialogarena.sporsmalogsvar.web;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.panel.Innboks;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.util.List;

public class InnboksPage extends WebPage {
    @Inject
    MeldingService service;

    public InnboksPage(PageParameters parameters) {
        super(parameters);
//        String behandling1 = service.stillSporsmal("Dette er spørsmål 1", "Overskrift 1", "Tema 1", "28088834986");
//        String behandling2 = service.stillSporsmal("Dette er spørsmål 2", "Overskrift 2", "Tema 2", "28088834986");
//        String behandling3 = service.stillSporsmal("Dette er spørsmål 3", "Overskrift 3", "Tema 3", "28088834986");
//        service.besvar(new Svar().withFritekst("Svar på spørsmål 1").withId("100000001").withTema("Tema 1x").withSensitiv(false));
        List<Melding> meldinger = service.hentAlleMeldinger("28088834986");
        add(new Innboks("innboks", meldinger.get(0), meldinger));
    }
}
