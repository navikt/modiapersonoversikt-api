package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Wrapper for lokalt lokaltKodeverk som returnerer standardverdier for ukjent kodeverksID
 */
public class BulletproofKodeverkService {

    private static final Logger LOG = LoggerFactory.getLogger(BulletproofKodeverkService.class);
    public static final String BEHANDLINGSTEMA = "Behandlingstema";
    public static final String ARKIVTEMA = "Arkivtemaer";

    @Inject
    private Kodeverk lokaltKodeverk;

    @Inject
    private KodeverkClient kodeverkClient;

    public String getSkjematittelForSkjemanummer(String vedleggsIdOrSkjemaId) {
        try {
            String tittel = lokaltKodeverk.getTittel(vedleggsIdOrSkjemaId);
            if (tittel == null) {
                throw new RuntimeException();
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
            return false;
        }
    }

    public String getTemanavnForTemakode(String temakode, String kodeverknavn) {
        try {
            return kodeverkClient.hentFoersteTermnavnForKode(temakode, kodeverknavn);
        } catch(Exception e) {
            LOG.warn("Fant ikke kodeverkid '" + temakode + "'. Bruker generisk tittel.", e);
            return hentUkjentKodeverkverdi(temakode);
        }
    }

    private String hentUkjentKodeverkverdi(String kode) {
        return String.format("[Fant ikke \"%s\" i kodeverk]", kode);
    }

}
