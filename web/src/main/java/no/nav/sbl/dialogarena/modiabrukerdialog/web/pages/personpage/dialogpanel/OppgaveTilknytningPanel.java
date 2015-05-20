package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel;

import no.nav.modig.modia.aria.AriaHelpers;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.personpage.dialogpanel.HenvendelseVM.OppgaveTilknytning;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import java.text.MessageFormat;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;

public class OppgaveTilknytningPanel extends GenericPanel<HenvendelseVM> {

    public OppgaveTilknytningPanel(String id, IModel<HenvendelseVM> model, GrunnInfo grunnInfo) {
        this(id, model, grunnInfo, Model.of(true));
    }

    public OppgaveTilknytningPanel(String id, IModel<HenvendelseVM> model, final GrunnInfo grunnInfo, IModel<Boolean> kanEndres) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);

        final IModel<Boolean> isOpen = Model.of(false);

        final WebMarkupContainer oppgaveTilknytningPopup = new WebMarkupContainer("oppgaveTilknytningPopup");
        oppgaveTilknytningPopup.setOutputMarkupPlaceholderTag(true);
        oppgaveTilknytningPopup.add(visibleIf(isOpen));
        Label oppgaveTilknytningTekst = new Label("oppgaveTilknytningTekst", new StringResourceModel("oppgavetilknytning.tekst.${oppgaveTilknytning}", getModel(), new Object[]{grunnInfo.saksbehandler.enhet}));
        final AjaxLink aapnePopup = new AjaxLink("aapnePopup") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                isOpen.setObject(true);
                target.add(this, oppgaveTilknytningPopup);
            }
        };
        aapnePopup.add(visibleIf(kanEndres));

        AriaHelpers.toggleButtonConnector(aapnePopup, oppgaveTilknytningPopup, isOpen);

        RadioChoice<OppgaveTilknytning> oppgaveTilknytningValg = new RadioChoice<>("oppgaveTilknytning", asList(OppgaveTilknytning.values()), new IChoiceRenderer<OppgaveTilknytning>() {
            @Override
            public Object getDisplayValue(OppgaveTilknytning object) {
                String key = "oppgavetilknytning." + object.name();
                if (object == OppgaveTilknytning.ENHET) {
                    return MessageFormat.format(getString(key), grunnInfo.saksbehandler.enhet);
                }
                return getString(key);
            }

            @Override
            public String getIdValue(OppgaveTilknytning object, int index) {
                return object.name();
            }
        });
        oppgaveTilknytningValg.add(new AjaxFormChoiceComponentUpdatingBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(OppgaveTilknytningPanel.this);
            }
        });
        AjaxLink lukkPopup = new AjaxLink("lukkPopup") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                isOpen.setObject(false);
                target.add(aapnePopup, oppgaveTilknytningPopup);
            }
        };
        oppgaveTilknytningPopup.add(oppgaveTilknytningValg, lukkPopup);

        add(oppgaveTilknytningTekst, aapnePopup, oppgaveTilknytningPopup);
    }
}
