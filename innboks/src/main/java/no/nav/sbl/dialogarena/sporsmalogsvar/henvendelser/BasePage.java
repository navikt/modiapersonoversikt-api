package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser;

import no.nav.modig.content.CmsContentRetriever;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import javax.inject.Inject;

public class BasePage extends WebPage {

	@Inject
	protected CmsContentRetriever cmsContentRetriever;

	public BasePage() {
		add(new Label("tittel", "Spørsmål og svar"));
	}
	
}
