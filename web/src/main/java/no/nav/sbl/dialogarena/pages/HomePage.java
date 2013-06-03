package no.nav.sbl.dialogarena.pages;

import no.nav.sbl.dialogarena.BasePage;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.model.Model;

import java.util.Date;


public class HomePage extends BasePage {

    public HomePage() {
        add(DateLabel.forDatePattern("year", Model.of(new Date()), "yyyy"));
    }
}
