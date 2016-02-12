package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import no.nav.sbl.dialogarena.sak.service.interfaces.BulletproofCmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * En CmsContentRetriever som alltid returnerer en streng selv om Enonic er nede eller key ikke finnes. Hensikten er å ikke ødelegge for resten av Modia som stort sett ikke bruker CMS
 */
public class BulletproofCmsServiceImpl implements BulletproofCmsService {

    private Logger log = LoggerFactory.getLogger(BulletproofCmsServiceImpl.class);

    @Inject
    private CmsContentRetriever cmsContentRetriever;

    @Override
    public String hentTekst(String key) {
        try {
            return cmsContentRetriever.hentTekst(key);
        } catch (RuntimeException e) {
            log.error("Exception i BulletproofCmsService", e);
            return String.format("[Fant ikke %s i CMS]", key);
        }
    }

    @Override
    public Boolean eksistererTekst(String key) {
        try {
            cmsContentRetriever.hentTekst(key);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public Boolean eksistererArtikkel(String key) {
        try {
            cmsContentRetriever.hentArtikkel(key);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public String hentArtikkel(String key) {
        try {
            return cmsContentRetriever.hentArtikkel(key);
        } catch (RuntimeException e) {
            log.error("Exception i BulletproofCmsService", e);
            return String.format("[Fant ikke %s i CMS]", key);
        }
    }
}
