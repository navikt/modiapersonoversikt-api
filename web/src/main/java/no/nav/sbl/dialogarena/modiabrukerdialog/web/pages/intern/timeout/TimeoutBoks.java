package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.timeout;

import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
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
                new BookmarkablePageLink<Intern>("fortsettlink", Intern.class, new PageParameters().set("fnr", fnr)),
                new BookmarkablePageLink<HentPersonPage>("forsidelink", HentPersonPage.class)
        );
        add(new TimeoutBehaviour());
    }

}
