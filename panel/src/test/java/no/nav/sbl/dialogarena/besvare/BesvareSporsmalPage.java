package no.nav.sbl.dialogarena.besvare;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.CompoundPropertyModel;


public class BesvareSporsmalPage extends WebPage {

    public BesvareSporsmalPage() {
        CompoundPropertyModel<SporsmalOgSvar> modell = new CompoundPropertyModel<>(new SporsmalOgSvar(null, null, null, null));
        add(
                new BesvareSporsmalPanel("besvare-sporsmal", modell),
                new AlleSporsmalOgSvarPanel("sporsmal-og-svar-liste", modell));
    }

}
