package no.nav.sbl.dialogarena.besvare;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;


public class BesvareSporsmalPage extends WebPage {

    public BesvareSporsmalPage() {
        final CompoundPropertyModel<BesvareSporsmalVM> modell = new CompoundPropertyModel<>(new BesvareSporsmalVM());
        BesvareSporsmalPanel besvareSporsmalPanel = new BesvareSporsmalPanel("besvare-sporsmal", modell);
        besvareSporsmalPanel.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return modell.getObject().behandlingsId != null;
            }
        }));
        add(
                besvareSporsmalPanel,
                new AlleSporsmalOgSvarPanel("sporsmal-og-svar-liste", modell));
    }

}
