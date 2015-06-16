package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import static no.nav.modig.modia.aria.AriaHelpers.toggleButtonConnector;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class SosialeTjenesterRadioChoices extends Panel {

    public SosialeTjenesterRadioChoices(String id, String sakstypePropertyKey, final IModel<Boolean> open) {
        super(id);
        setOutputMarkupId(true);

        AjaxLink header = new AjaxLink("link") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                open.setObject(!open.getObject());
                target.add(SosialeTjenesterRadioChoices.this);
            }
        };
        header.add(
                new Label("sakstype", new ResourceModel(sakstypePropertyKey)),
                new PilOppNed("pilSaker", open)
        );

        Sak sak = new Sak();
        sak.temaKode = Temagruppe.OKSOS.name();
        sak.temaNavn = getString("journalfor.sakstype.tekst.sosiale.temanavn");
        WebMarkupContainer sakswrapper = new WebMarkupContainer("sakswrapper", Model.of(sak));
        sakswrapper.add(
                new Radio<>("sak", sakswrapper.getDefaultModel()),
                new Label("beskrivelse", new ResourceModel("journalfor.sakstype.tekst.sosiale.temabeskrivelse")));
        sakswrapper.add(visibleIf(open));

        toggleButtonConnector(header, sakswrapper, open);
        add(header, sakswrapper);
    }
}
