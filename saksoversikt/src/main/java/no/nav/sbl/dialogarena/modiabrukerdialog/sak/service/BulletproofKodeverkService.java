package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.kodeverk.KodeverkClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
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

    public String getKode(String vedleggsIdOrSkjemaId, Kodeverk.Nokkel nokkel) {
        try {
            String kodeverdi = lokaltKodeverk.getKoder(vedleggsIdOrSkjemaId).get(nokkel);
            return kodeverdi == null ? vedleggsIdOrSkjemaId : kodeverdi;
        } catch(Exception e) {
            LOG.warn("Fant ikke kodeverkid '" + vedleggsIdOrSkjemaId, e);
            return vedleggsIdOrSkjemaId;
        }
    }

}
