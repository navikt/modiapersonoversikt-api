package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.feil;

import no.nav.modig.wicket.errorhandling.panels.ErrorPanel;
import no.nav.modig.wicket.errorhandling.panels.ErrorPanelVM;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.hentperson.HentPersonPage;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

public class ModiaDefaultErrorPanel extends ErrorPanel {

    private final Class goToClass;

    public ModiaDefaultErrorPanel(String id, IModel<ErrorPanelVM> exceptionPanelVM) {
        this(id, exceptionPanelVM, HentPersonPage.class);
    }

    public ModiaDefaultErrorPanel(String id, IModel<ErrorPanelVM> exceptionPanelVM, Class goToClass) {
        super(id, exceptionPanelVM);
        this.goToClass = goToClass;

        add(
                createGoToLink()
        );
    }

    private Link createGoToLink() {
        return new BookmarkablePageLink("goToLink", goToClass);
    }

    @Override
    protected Component extraContent(String id) {
        return new WebMarkupContainer(id).setVisible(false);
    }
}
