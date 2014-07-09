package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.svarogreferatpanel.svarpanel;

import no.nav.modig.wicket.component.urlparsinglabel.URLParsingMultiLineLabel;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;
import org.joda.time.DateTime;

import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;

public class TidligereMeldingPanel extends Panel {
    public TidligereMeldingPanel(String id, String temagruppe, DateTime opprettetDato, final String fritekst, boolean minimert) {
        super(id);

        final URLParsingMultiLineLabel fritekstFelt = new URLParsingMultiLineLabel("fritekst", fritekst);

        final WebMarkupContainer overskriftContainer = new WebMarkupContainer("overskriftContainer");
        overskriftContainer
                .add(
                        new Label("overskrift", new ResourceModel("tidligeremelding.overskrift." + id)),
                        new WebMarkupContainer("ekspanderingspil").add(hasCssClassIf("ekspandert", new AbstractReadOnlyModel<Boolean>() {
                            @Override
                            public Boolean getObject() {
                                return fritekstFelt.isVisibilityAllowed();
                            }
                        })))
                .add(
                        new AjaxEventBehavior("click") {
                            @Override
                            protected void onEvent(AjaxRequestTarget target) {
                                fritekstFelt.setVisibilityAllowed(!fritekstFelt.isVisibilityAllowed());
                                target.add(overskriftContainer, fritekstFelt);
                            }
                        });

        add(overskriftContainer.setOutputMarkupId(true));
        add(new Label("temagruppe", new ResourceModel(temagruppe)));
        add(new Label("dato", Datoformat.kortMedTid(opprettetDato)));
        add(fritekstFelt.setVisibilityAllowed(!minimert).setOutputMarkupPlaceholderTag(true));
    }
}
