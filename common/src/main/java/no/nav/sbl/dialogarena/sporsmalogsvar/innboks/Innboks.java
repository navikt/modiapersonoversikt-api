package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.AlleMeldingerPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public class Innboks extends Panel implements MeldingslisteDelegat {

    public static final String VALGT_MELDING_EVENT = "hendelser.valgte_melding";
    public static final String MELDINGER_OPPDATERT = "hendelser.meldinger_oppdatert";

    @Inject
    MeldingService service;

    private final InnboksModell innboksModell;
    private final String fodselsnr;

    private List<HarMeldingsliste> meldingslister = new ArrayList<>();

    public Innboks(String id, String fodselsnr) {
        super(id);
        this.fodselsnr = fodselsnr;
        this.innboksModell = new InnboksModell(new InnboksVM(service.hentAlleMeldinger(fodselsnr)));
        setDefaultModel(this.innboksModell);
        setOutputMarkupId(true);

        AlleMeldingerPanel alleMeldinger = new AlleMeldingerPanel("meldinger", this);
        add(alleMeldinger);
        meldingslister.add(alleMeldinger);
        
        DetaljvisningPanel detaljvisning = new DetaljvisningPanel("detaljpanel", this, innboksModell.getInnboksVM().getValgtMelding());
        meldingslister.add(detaljvisning);

        add(alleMeldinger, detaljvisning);

        add(new AttributeAppender("class", " innboks clearfix"));
    }

    @RunOnEvents(MELDINGER_OPPDATERT)
    public void messagesUpdated(AjaxRequestTarget target) {
        this.innboksModell.getObject().oppdaterMeldingerFra(service.hentAlleMeldinger(fodselsnr));
        target.add(this);
    }

    @Override
    public void meldingValgt(AjaxRequestTarget target, MeldingVM valgteMelding, boolean oppdaterScroll) {
        MeldingVM forrigeMelding = innboksModell.getInnboksVM().getValgtMelding();
        innboksModell.getInnboksVM().setValgtMelding(valgteMelding);
        for (HarMeldingsliste meldingsliste : meldingslister) {
            meldingsliste.valgteMelding(target, forrigeMelding, valgteMelding, oppdaterScroll);
        }
    }

    @Override
    public IModel<Boolean> erMeldingValgt(MeldingVM melding) {
        return innboksModell.erValgtMelding(melding);
    }
}
