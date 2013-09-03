package no.nav.sbl.dialogarena.sporsmalogsvar;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.innboks.Innboks;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

public class InnboksTestPage extends WebPage {
    public InnboksTestPage(PageParameters parameters) {
        super(parameters);
        add(new Innboks("innboks", "14108643790", new MeldingService() {
            @Override
            public List<Melding> hentAlleMeldinger(String aktorId) {
                return Arrays.asList(new Melding()
                        .withType(Meldingstype.SPORSMAL)
                        .withId("1")
                        .withTraadId("1")
                        .withOpprettet(DateTime.now())
                        .withOverskrift("Overskrift")
                        .withTema("Tema")
                        .withFritekst("Fritekst"));
            }
        }));
    }
}
