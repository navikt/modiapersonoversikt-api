package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.DokumentinnsendingDetaljPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingstraadPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.StringResourceModel;

import static no.nav.modig.wicket.conditional.ConditionalUtils.visibleIf;
import static no.nav.modig.wicket.model.ModelUtils.either;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.DOKUMENTINNSENDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.SVAR;

public class DetaljvisningPanel extends Panel implements HarMeldingsliste {

    public DetaljvisningPanel(String id, InnboksModell innboksModell, MeldingslisteDelegat delegat) {
		super(id);
		setOutputMarkupId(true);
        MeldingstraadPanel meldingstraadPanel = new MeldingstraadPanel("traad", delegat);
        DokumentinnsendingDetaljPanel dokumentinnsendingDetaljPanel = new DokumentinnsendingDetaljPanel("dokumentinnsendingDetaljPanel", innboksModell.getInnboksVM().getValgtMelding());
        dokumentinnsendingDetaljPanel.add(visibleIf(innboksModell.valgtMeldingAvType(DOKUMENTINNSENDING)));
        meldingstraadPanel.add(visibleIf(either(innboksModell.valgtMeldingAvType(SPORSMAL))
                .or(innboksModell.valgtMeldingAvType(SVAR))));
        add(new Label("ingen-valgt", new StringResourceModel("ingen-melding-valgt", this, null))
                .add(visibleIf(innboksModell.ingenMeldingValgt())));
		add(meldingstraadPanel, dokumentinnsendingDetaljPanel);
	}

	@Override
	public void valgteMelding(AjaxRequestTarget target, Optional<MeldingVM> forrigeMelding, MeldingVM valgteMelding, boolean oppdaterScroll) {
		target.add(this);
	}

}
