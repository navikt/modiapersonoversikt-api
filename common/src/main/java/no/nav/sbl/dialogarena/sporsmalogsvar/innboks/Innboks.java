package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import javax.inject.Inject;
import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.AlleMeldingerPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingstraadPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.panel.Panel;

public class Innboks extends Panel {

    public static final String VALGT_MELDING_EVENT = "hendelser.valgte_melding";
    public static final String MELDINGER_OPPDATERT = "hendelser.meldinger_oppdatert";

    @Inject
    MeldingService service;

    private final InnboksModell innboksModell;
    private final String fodselsnr;

    public Innboks(String id, String fodselsnr) {
        super(id);
        this.fodselsnr = fodselsnr;
        this.innboksModell = new InnboksModell(new InnboksVM(service.hentAlleMeldinger(fodselsnr)));
        setDefaultModel(this.innboksModell);
        setOutputMarkupId(true);

        add(new AlleMeldingerPanel("meldinger", this.innboksModell));
        add(new MeldingstraadPanel("traad", this.innboksModell));

        add(new AttributeAppender("class", " innboks clearfix"));
    }

    @RunOnEvents(MELDINGER_OPPDATERT)
    public void messagesUpdated(AjaxRequestTarget target) {
        this.innboksModell.getObject().oppdaterMeldingerFra(service.hentAlleMeldinger(fodselsnr));
        target.add(this);
    }
}
