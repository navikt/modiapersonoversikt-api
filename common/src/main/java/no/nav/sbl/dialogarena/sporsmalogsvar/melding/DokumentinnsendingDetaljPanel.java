package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.modig.lang.option.Optional;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;

public class DokumentinnsendingDetaljPanel extends Panel {

	public DokumentinnsendingDetaljPanel(String id, Optional<MeldingVM> valgtMelding) {
		super(id);
        if (valgtMelding.isSome()) {
            setDefaultModel(new CompoundPropertyModel<Object>(valgtMelding.get()));
        }
		setOutputMarkupId(true);
		add(new Label("overskrift"));
	}

}
