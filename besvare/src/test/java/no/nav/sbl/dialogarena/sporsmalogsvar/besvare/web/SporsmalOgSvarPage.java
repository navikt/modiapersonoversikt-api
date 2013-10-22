package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.web;

import no.nav.sbl.dialogarena.mottaksbehandling.Mottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.context.MottaksbehandlingKontekst;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.TraadPanel;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.apache.wicket.markup.html.WebPage;

import javax.inject.Inject;

public class SporsmalOgSvarPage extends WebPage {

    @Inject
    HenvendelsePortType henvendelsePortType;

    public SporsmalOgSvarPage() {
        super();
        MottaksbehandlingKontekst ctx = new MottaksbehandlingKontekst(null, null, null, null, null, null);

        add(new TraadPanel("besvar", "10108000398", new Mottaksbehandling(ctx), henvendelsePortType));
    }

}
