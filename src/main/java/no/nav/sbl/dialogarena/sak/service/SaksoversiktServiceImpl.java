package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.core.exception.SystemException;
import no.nav.modig.lang.collections.iter.PreparedIterable;
import no.nav.sbl.dialogarena.sak.comparators.SistOppdaterteBehandlingComparator;
import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.sak.transformers.FilterImpl;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.Tema;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SakOgBehandlingService;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.getProperty;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sak.transformers.SakOgBehandlingTransformers.temaVMTransformer;
import static org.slf4j.LoggerFactory.getLogger;

public class SaksoversiktServiceImpl implements SaksoversiktService {

    private static final Logger LOG = getLogger(SaksoversiktServiceImpl.class);

    @Inject
    private AktoerPortType fodselnummerAktorService;
    @Inject
    private SakOgBehandlingService sakOgBehandlingService;
    @Inject
    private FilterImpl filter;
    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;

    @Override
    @SuppressWarnings("PMD")
    public List<Tema> hentTemaer(String fnr) {
        LOG.info("Henter tema fra Sak og Behandling til Modiasaksoversikt. Fnr: " + fnr);
        List<WSSak> saker = on(sakOgBehandlingService.hentSakerForAktor(hentAktorId(fnr))).collect();
        PreparedIterable<Tema> temaer = on(filter.filtrerSaker(saker)).map(temaVMTransformer(filter, bulletproofKodeverkService));
        try {
            return temaer.collect(new SistOppdaterteBehandlingComparator());
        } catch (NullPointerException npe) {
            throw new ApplicationException("Nullpointer i service, antar comparator", npe);
        }
    }

    private String hentAktorId(String fnr) {
        try {
            return fodselnummerAktorService.hentAktoerIdForIdent(lagAktorRequest(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            throw new SystemException("Klarte ikke hente aktørId", hentAktoerIdForIdentPersonIkkeFunnet);
        }
    }

    private HentAktoerIdForIdentRequest lagAktorRequest(String fnr) {
        HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
        request.setIdent(fnr);
        return request;
    }

    @Override
    public void fjernGamleDokumenter(List<Sakstema> resultat) {

        List<String> journalpostIdAaFjerne = getJournalpostIdAaFjerne(resultat);

        resultat.stream()
                .forEach(sakstema -> sakstema.withDokumentMetadata(sakstema.dokumentMetadata.stream()
                        .filter(dokumentMetadata -> !journalpostIdAaFjerne
                                .contains(dokumentMetadata.getJournalpostId())).collect(Collectors.toList())));
    }


    private List<String> getJournalpostIdAaFjerne(List<Sakstema> resultat) {
        Map<String, DokumentMetadata> muligeJournalpostIdAaFjerne = new HashMap<>();

        final LocalDateTime PROD_SETTNING_DATO = formatDate(getProperty("saksoversikt.prodsettningsdato"));


        getMuligeJournalpostIdAaFjerne(resultat, muligeJournalpostIdAaFjerne);

        return muligeJournalpostIdAaFjerne
                .keySet()
                .stream()
                .filter(key -> muligeJournalpostIdAaFjerne
                        .get(key).getBaksystem()
                        .equals(Baksystem.JOARK))
                .filter(joarkKey -> muligeJournalpostIdAaFjerne
                        .get(joarkKey)
                        .getDato()
                        .isBefore(PROD_SETTNING_DATO))
                .collect(Collectors.toList());
    }

    private void getMuligeJournalpostIdAaFjerne(List<Sakstema> resultat, Map<String, DokumentMetadata> muligeJournalpostIdAaFjerne) {
        for (Sakstema sakstema : resultat) {
            for (DokumentMetadata dokumentMetadata : sakstema.dokumentMetadata) {
                String journalpostId = dokumentMetadata.getJournalpostId();
                if (muligeJournalpostIdAaFjerne.containsKey(journalpostId)) {
                    muligeJournalpostIdAaFjerne.remove(journalpostId);
                } else {
                    muligeJournalpostIdAaFjerne.put(journalpostId, dokumentMetadata);
                }
            }
        }
    }

    private static LocalDateTime formatDate(String prodDate) {
        LocalDate date = LocalDate.parse(prodDate);
        LocalTime time = LocalTime.of(0, 0);
        return LocalDateTime.of(date, time);
    }


}
