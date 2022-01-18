package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig;
import org.slf4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("squid:S1166")
public class BulletproofKodeverkService {

    private static final Logger LOG = getLogger(BulletproofKodeverkService.class);
    public static final KodeverkConfig BEHANDLINGSTEMA = KodeverkConfig.BEHANDLINGSTEMA;
    public static final KodeverkConfig ARKIVTEMA = KodeverkConfig.ARKIVTEMA;

    @Autowired
    private EnhetligKodeverk.Service kodeverk;

    public ResultatWrapper<String> getTemanavnForTemakode(String temakode, KodeverkConfig kodeverknavn) {
        try {
            return new ResultatWrapper<>(kodeverk.hentKodeverk(kodeverknavn).hentBeskrivelse(temakode));
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
