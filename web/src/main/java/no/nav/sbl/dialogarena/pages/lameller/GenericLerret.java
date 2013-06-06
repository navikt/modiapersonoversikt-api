package no.nav.sbl.dialogarena.pages.lameller;

import no.nav.modig.modia.lamell.Lerret;
import org.apache.wicket.markup.html.panel.Panel;

public class GenericLerret extends Lerret {
	public GenericLerret(String id, Panel panel) {
		super(id);

		add(panel);
	}
}
