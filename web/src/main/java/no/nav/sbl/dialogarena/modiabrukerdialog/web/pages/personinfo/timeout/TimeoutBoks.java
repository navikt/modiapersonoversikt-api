package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personinfo.timeout;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personinfo.PersonPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Panel som dukker opp ved timeout av sesjonen.
 */
public class TimeoutBoks extends Panel {

    public TimeoutBoks(String id, String fnr) {
        super(id);
        add(
                new BookmarkablePageLink<PersonPage>("fortsettlink", PersonPage.class, new PageParameters().set("fnr", fnr)),
                new BookmarkablePageLink<HentPersonPage>("forsidelink", HentPersonPage.class)
        );
        add(new TimeoutBehaviour());
    }

}
