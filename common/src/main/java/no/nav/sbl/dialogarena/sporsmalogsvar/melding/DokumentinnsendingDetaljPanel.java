package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class DokumentinnsendingDetaljPanel extends Panel {

	public DokumentinnsendingDetaljPanel(String id, CompoundPropertyModel<MeldingVM> modell) {
		super(id, modell);
		setOutputMarkupId(true);
		add(new Label("overskrift"));
	}

}
