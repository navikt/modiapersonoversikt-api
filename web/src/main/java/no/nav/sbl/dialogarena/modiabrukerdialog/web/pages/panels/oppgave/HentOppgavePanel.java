package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.panels.oppgave;

import no.nav.modig.wicket.component.modal.ModigModalWindow;
import no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.intern.Intern;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.informasjon.WSPlukkOppgaveResultat;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.modig.wicket.conditional.ConditionalUtils.hasCssClassIf;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class HentOppgavePanel extends Panel {

    @Inject
    OppgavebehandlingPortType service;

    private final WebMarkupContainer temaboks;
    private final Model<String> tema;
    private Model<Boolean> visTema = new Model<>(false);

    ModigModalWindow modalWindow = new ModigModalWindow("tomForOppgaverMelding");

    public HentOppgavePanel(String id) {
        super(id);
        Serializable temaAttr = getSession().getAttribute("valgtTema");
        String temaStr = temaAttr != null ? temaAttr.toString() : null;
        tema = new Model<>(temaStr);
        setDefaultModel(tema);
        setOutputMarkupId(true);

        Temaliste temaliste = new Temaliste(
            "tema", asList(
                "Dagpenger",
                "Sykepenger",
                "Langtekst"
            ));

        temaboks = new WebMarkupContainer("temaboks");
        temaboks.setOutputMarkupPlaceholderTag(true);
        temaboks.add(temaliste);
        temaboks.add(visibleIf(visTema));
        add(temaboks, modalWindow);

        add(new AjaxLink("plukk-oppgave") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                if (isNotBlank(tema.getObject())) {
                    valgteTema(tema.getObject(), target);
                } else {
                    visTema.setObject(!visTema.getObject());
                    target.add(temaboks);
                }
            }
        });
        add(new AjaxLink("plukk-valg") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                visTema.setObject(!visTema.getObject());
                target.add(temaboks);
            }
        });
    }

    private IModel<Boolean> erValgteTema(final String t) {
        return new AbstractReadOnlyModel <Boolean>() {
            @Override
            public Boolean getObject() {
                return t.equals(tema.getObject());
            }
        };
    }

    class Temaliste extends PropertyListView<String> {

        public Temaliste(String id, List<? extends String> list) {
            super(id, list);
            setOutputMarkupId(true);
        }

        @Override
        protected void populateItem(final ListItem<String> item) {
            item.add(
                new Label("temanavn",
                    new StringResourceModel(item.getModelObject(), this, null)));
            item.add(hasCssClassIf("valgt", erValgteTema(item.getModelObject())));
            item.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    visTema.setObject(false);
                    tema.setObject(item.getModelObject());
                    HentOppgavePanel.this.valgteTema(item.getModelObject(), target);
                    target.add(temaboks);
                }
            });
        }
    }

    public void valgteTema(String tema, AjaxRequestTarget target) {
        WSPlukkOppgaveResultat oppgaveResultat = service.plukkOppgave(tema);
        if (oppgaveResultat == null) {
            modalWindow.setContent(
                new TomtForOppgaverPanel(modalWindow.getContentId(), modalWindow));
            modalWindow.show(target);
            return;
        }

        getSession().setAttribute("valgtTema", tema);

        PageParameters pageParameters = new PageParameters();
        pageParameters.add("fnr", oppgaveResultat.getFodselsnummer());
        pageParameters.add("oppgaveId", oppgaveResultat.getOppgaveId());
        setResponsePage(Intern.class, pageParameters);
    }
}
