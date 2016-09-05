package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.StringUtils.isEmpty;

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

            if (sprakErEngelsk && !isEmpty(engelskTittel)) {
                tittel = engelskTittel;
            } else {
                tittel = lokaltKodeverk.getKode(vedleggsIdOrSkjemaId, Kodeverk.Nokkel.TITTEL);
            }

            if (tittel == null) {
                throw new ApplicationException("Tittel er null!");
            }

            return tittel;
        } catch (Exception e) {
            LOG.error("Fant ikke kodeverkid '" + vedleggsIdOrSkjemaId + "'. Bruker generisk tittel.", e);
            return vedleggsIdOrSkjemaId;
        }
    }

    public ResultatWrapper<String> getTemanavnForTemakode(String temakode, String kodeverknavn) {
        try {
            return new ResultatWrapper<>(kodeverkClient.hentFoersteTermnavnForKode(temakode, kodeverknavn));
        } catch (ApplicationException e) {
            LOG.warn("Fant ikke kodeverkid '" + temakode + "'. Bruker generisk tittel.", e);
            return new ResultatWrapper<>(temakode);
        } catch (RuntimeException e) {
            LOG.error("Ukjent feil mot kall mot kodeverk", e);
            Set<Baksystem> feilendeBaksystemer = new HashSet<>();
            feilendeBaksystemer.add(Baksystem.KODEVERK);
            return new ResultatWrapper<>(temakode, feilendeBaksystemer);
        }
    }

}
