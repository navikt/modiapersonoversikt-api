package no.nav.sbl.dialogarena.besvare;

import org.apache.wicket.markup.html.WebPage;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;


public class BesvareSporsmalPage extends WebPage {

    public BesvareSporsmalPage() {
        final BesvareSporsmalCompoundPropertyModel modell = new BesvareSporsmalCompoundPropertyModel(new BesvareSporsmalVM());
        BesvareSporsmalPanel besvareSporsmalPanel = new BesvareSporsmalPanel("besvare-sporsmal", modell);
        besvareSporsmalPanel.add(visibleIf(modell.erSynlig()));
        add(
                besvareSporsmalPanel,
                new AlleSporsmalOgSvarPanel("sporsmal-og-svar-liste", modell));
    }

}
