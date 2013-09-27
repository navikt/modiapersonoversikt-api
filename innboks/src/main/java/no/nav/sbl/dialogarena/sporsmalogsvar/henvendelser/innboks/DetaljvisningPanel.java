package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.modig.wicket.events.annotations.RunOnEvents;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.sporsmalogsvar.TraaddetaljerPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.either;
import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelsetype.SVAR;

public class DetaljvisningPanel extends Panel {

    public DetaljvisningPanel(String id, InnboksModell innboksModell) {
		super(id);
		setOutputMarkupId(true);
        TraaddetaljerPanel traaddetaljerPanel = new TraaddetaljerPanel("traad", innboksModell);
        traaddetaljerPanel.add(visibleIf(either(innboksModell.valgtHenvendelseAvType(SPORSMAL)).or(innboksModell.valgtHenvendelseAvType(SVAR))));
		add(traaddetaljerPanel);
	}

    @RunOnEvents(Innboks.VALGT_HENVENDELSE)
    public void meldingerOppdatert(AjaxRequestTarget target) {
        target.add(this);
    }

}
