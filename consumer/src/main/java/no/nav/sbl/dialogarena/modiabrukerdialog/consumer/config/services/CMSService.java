package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.modig.content.ValueRetriever;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.cms.CmsEndpointConfig.DEFAULT_LOCALE;

public class CMSService extends CmsContentRetriever {

    @Inject
    private ValueRetriever siteContentRetriever;

    public CMSService() {
        setDefaultLocale(DEFAULT_LOCALE);
    }

    @PostConstruct
    public void setRetrievers() {
        setTeksterRetriever(siteContentRetriever);
        setArtikkelRetriever(siteContentRetriever);
    }
}
