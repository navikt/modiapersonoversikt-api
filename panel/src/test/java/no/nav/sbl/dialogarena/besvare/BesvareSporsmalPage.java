package no.nav.sbl.dialogarena.besvare;

import org.apache.wicket.markup.html.WebPage;


public class BesvareSporsmalPage extends WebPage {

    public BesvareSporsmalPage() {
        add(new BesvareSporsmalPanel("besvare-sporsmal"));
    }
}
