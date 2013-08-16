package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser;

import static no.nav.modig.wicket.model.ModelUtils.FALSE;
import static no.nav.modig.wicket.model.ModelUtils.TRUE;
import static no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel.DIALOGARENA_FOOTER_BASEURL;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.webkomponent.footer.FooterPanel;
import no.nav.sbl.dialogarena.webkomponent.innstillinger.InnstillingerPanel;
import no.nav.sbl.dialogarena.webkomponent.navigasjon.NavigasjonPanel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public class BasePage extends WebPage {

	@Inject
	protected CmsContentRetriever cmsContentRetriever;

	public BasePage() {
		Map<String, String> footerLinks = new HashMap<>();
		footerLinks.put(DIALOGARENA_FOOTER_BASEURL, System.getProperty(FooterPanel.DIALOGARENA_FOOTER_BASEURL));
		add(new Label("tittel", "Spørsmål og svar"), new InnstillingerPanel("innstillinger", TRUE,
				cmsContentRetriever), new NavigasjonPanel("navigasjon", System.getProperty("navigasjonslink")), new FooterPanel("footer",
				footerLinks, TRUE, FALSE, cmsContentRetriever));
	}
	
}
