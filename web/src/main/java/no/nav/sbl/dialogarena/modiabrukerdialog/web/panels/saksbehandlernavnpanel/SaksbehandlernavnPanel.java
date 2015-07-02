package no.nav.sbl.dialogarena.modiabrukerdialog.web.panels.saksbehandlernavnpanel;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Person;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;

import javax.inject.Inject;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

public class SaksbehandlernavnPanel extends Panel {

    @Inject
    private LDAPService ldap;

    public SaksbehandlernavnPanel(String id) {
        super(id);
        String ident = getSubjectHandler().getUid();
        Person saksbehandler = ldap.hentSaksbehandler(ident);
        add(new Label("navn", String.format("%s (%s)", saksbehandler.navn, ident)));
    }
}
