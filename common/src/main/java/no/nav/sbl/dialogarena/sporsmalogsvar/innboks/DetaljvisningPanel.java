package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.DokumentinnsendingDetaljPanel;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingstraadPanel;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class DetaljvisningPanel extends Panel implements HarMeldingsliste {

	private MeldingstraadPanel meldingstraadPanel;
	private DokumentinnsendingDetaljPanel dokumentinnsendingDetaljPanel;

	public DetaljvisningPanel(String id, MeldingslisteDelegat delegat, MeldingVM valgtMelding) {
		super(id);
		setOutputMarkupId(true);
		meldingstraadPanel = new MeldingstraadPanel("traad", delegat);
		dokumentinnsendingDetaljPanel = new DokumentinnsendingDetaljPanel("dokumentinnsendingDetaljPanel", new CompoundPropertyModel<>(valgtMelding));
		visValgtMelding(valgtMelding);
		add(meldingstraadPanel, dokumentinnsendingDetaljPanel);
	}

	@Override
	public void valgteMelding(AjaxRequestTarget target, MeldingVM forrigeMelding, MeldingVM valgteMelding, boolean oppdaterScroll) {
		visValgtMelding(valgteMelding);
		target.add(this);
	}

	private void visValgtMelding(MeldingVM valgteMelding) {
		if (valgteMelding.melding.type == Meldingstype.DOKUMENTINNSENDING) {
			dokumentinnsendingDetaljPanel.setVisible(true);
			meldingstraadPanel.setVisible(false);
		} else {
			meldingstraadPanel.setVisible(true);
			dokumentinnsendingDetaljPanel.setVisible(false);
		}
	}

}
