package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.modia.events.InternalEvents.MELDING_SENDT_TIL_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.Innboks.VALGT_MELDING_EVENT;


public class TraaddetaljerPanel extends Panel {

    private final IModel<InnboksVM> innboksVMModel;

    public TraaddetaljerPanel(String id, IModel<InnboksVM> model) {
        super(id);
        setOutputMarkupId(true);

        this.innboksVMModel = model;

        add(new HaandterMeldingPanel("haandter-melding", innboksVMModel));
        add(new NyesteMeldingPanel("nyeste-melding", innboksVMModel));
        add(new TidligereMeldingerPanel("tidligere-meldinger"));
    }

    @RunOnEvents(VALGT_MELDING_EVENT)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }

    @RunOnEvents(MELDING_SENDT_TIL_BRUKER)
    public void meldingSendtTilBruker(AjaxRequestTarget target){
        if(this.isVisibleInHierarchy()){
            innboksVMModel.getObject().oppdaterMeldinger();
            target.add(this);
        }
    }
}
