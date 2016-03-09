package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.FeilendeBaksystemException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;


import javax.inject.Inject;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Wrapper for lokalt lokaltKodeverk som returnerer standardverdier for ukjent kodeverksID
 */
@SuppressWarnings("squid:S1166")
public class BulletproofKodeverkService {

    private static final Logger LOG = getLogger(BulletproofKodeverkService.class);
    public static final String BEHANDLINGSTEMA = "Behandlingstema";
    public static final String ARKIVTEMA = "Arkivtemaer";

    @Inject
    private Kodeverk lokaltKodeverk;

    @Inject
    private KodeverkClient kodeverkClient;

    public String getSkjematittelForSkjemanummer(String vedleggsIdOrSkjemaId) {
        return getSkjematittelForSkjemanummer(vedleggsIdOrSkjemaId, "");
    }

    public String getSkjematittelForSkjemanummer(String vedleggsIdOrSkjemaId, String sprak) {
        try {
            String tittel;
            String engelskTittel = lokaltKodeverk.getKode(vedleggsIdOrSkjemaId, Kodeverk.Nokkel.TITTEL_EN);
            boolean sprakErEngelsk = !StringUtils.isEmpty(sprak) && "en".equals(sprak);


            if(sprakErEngelsk && !isEmpty(engelskTittel)) {
                tittel = engelskTittel;
            } else {
                tittel = lokaltKodeverk.getKode(vedleggsIdOrSkjemaId, Kodeverk.Nokkel.TITTEL);
            }

            if (tittel == null) {
                throw new ApplicationException("Tittel er null!");
            }

            return tittel;
        } catch (Exception e) {
            LOG.warn("Fant ikke kodeverkid '"+vedleggsIdOrSkjemaId+"'. Bruker generisk tittel.", e);
            return hentUkjentKodeverkverdi(vedleggsIdOrSkjemaId);
        }
    }

    public boolean finnesTemaKodeIKodeverk(String temakode, String kodeverknavn) {
        try {
            return !isEmpty(kodeverkClient.hentFoersteTermnavnForKode(temakode, kodeverknavn));
        } catch(ApplicationException e) {
            LOG.warn("Fant ikke temakode i kodeverk");
            return false;
        }
    }

    public String getTemanavnForTemakode(String temakode, String kodeverknavn) {
        try {
            return kodeverkClient.hentFoersteTermnavnForKode(temakode, kodeverknavn);
        } catch (RuntimeException e) {
            LOG.error("Ukjent feil mot kall mot kodeverk. Fallback til temakode", e);
            throw new FeilendeBaksystemException(Baksystem.KODEVERK);
        } catch(Exception e) {
            LOG.warn("Fant ikke kodeverkid '" + temakode + "'. Bruker generisk tittel.", e);
            LOG.warn("Fant ikke temanavn '" + kodeverknavn + "'. Bruker generisk tittel.", e);
            return hentUkjentKodeverkverdi(temakode);
        }
    }

    private String hentUkjentKodeverkverdi(String kode) {
        return String.format("[Fant ikke \"%s\" i kodeverk]", kode);
    }
}
