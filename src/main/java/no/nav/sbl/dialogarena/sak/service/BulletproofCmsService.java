package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.content.CmsContentRetriever;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * En CmsContentRetriever som alltid returnerer en streng selv om Enonic er nede eller key ikke finnes. Hensikten er å ikke ødelegge for resten av Modia som stort sett ikke bruker CMS
 */
public class BulletproofCmsService {
    private Logger log = LoggerFactory.getLogger(BulletproofCmsService.class);

    @Inject
    private CmsContentRetriever cmsContentRetriever;

    public String hentTekst(String key) {
        try {
            return cmsContentRetriever.hentTekst(key);
        } catch (RuntimeException e) {
            log.error("Exception i BulletproofCmsService", e);
            return String.format("[Fant ikke %s i CMS]", key);
        }
    }

    public Boolean eksistererTekst(String key) {
        try {
            cmsContentRetriever.hentTekst(key);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public Boolean eksistererArtikkel(String key) {
        try {
            cmsContentRetriever.hentArtikkel(key);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public String hentArtikkel(String key) {
        try {
            return cmsContentRetriever.hentArtikkel(key);
        } catch (RuntimeException e) {
            log.error("Exception i BulletproofCmsService", e);
            return String.format("[Fant ikke %s i CMS]", key);
        }
    }
}
