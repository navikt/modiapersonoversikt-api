package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.HaandterMeldingPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing.JournalforingsPanel.TRAAD_JOURNALFORT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.merke.MerkePanel.TRAAD_KONTORSPERRET;


public class TraaddetaljerPanel extends Panel {

    private final InnboksVM innboksVM;
    private WebMarkupContainer kontorSperretInfo;

    public TraaddetaljerPanel(String id, final InnboksVM innboksVM) {
        super(id);
        setOutputMarkupId(true);

        this.innboksVM = innboksVM;

        kontorSperretInfo = new WebMarkupContainer("kontorsperret-info");
        kontorSperretInfo.setOutputMarkupId(true);
        kontorSperretInfo.add(new Label("enhet", new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return innboksVM.getValgtTraad().getKontorsperretEnhet().get();
            }
        }));
        kontorSperretInfo.add(visibleIf(new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return innboksVM.getValgtTraad().erKontorsperret();
            }
        }));

        add(new HaandterMeldingPanel("haandter-melding", innboksVM));
        add(kontorSperretInfo);
        add(new NyesteMeldingPanel("nyeste-melding", innboksVM));
        add(new TidligereMeldingerPanel("tidligere-meldinger", innboksVM));
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }

    @RunOnEvents({MELDING_SENDT_TIL_BRUKER, TRAAD_JOURNALFORT})
    public void oppdaterMeldingerHvisSynlig(AjaxRequestTarget target) {
        if (this.isVisibleInHierarchy()) {
            innboksVM.oppdaterMeldinger();
            target.add(this);
        }
    }

    @RunOnEvents(TRAAD_KONTORSPERRET)
    public void oppdaterTraadKontorSperret(AjaxRequestTarget target) {
        innboksVM.oppdaterMeldinger();
        target.add(this);
    }
}
