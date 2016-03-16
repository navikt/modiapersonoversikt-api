package no.nav.sbl.dialogarena.sak.service;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.domain.widget.Tema;
import no.nav.sbl.dialogarena.sak.service.interfaces.SaksoversiktService;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.sbl.dialogarena.saksoversikt.service.service.SakOgBehandlingService;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.getProperty;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.sak.transformers.TemaTransformer.tilTema;

public class SaksoversiktServiceImpl implements SaksoversiktService {

    @Inject
    private AktoerPortType fodselnummerAktorService;
    @Inject
    private SakOgBehandlingService sakOgBehandlingService;
    @Inject
    private Filter filter;
    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;


    public List<Tema> hentTemaer(String fnr) {
        List<WSSak> saker = sakOgBehandlingService.hentSakerForAktor(hentAktorId(fnr));
        return filter.filtrerSaker(saker).stream()
                .map(wsSak -> tilTema(wsSak, bulletproofKodeverkService, filter))
                .sorted((o1, o2) -> o2.behandlingsdato.compareTo(o1.behandlingsdato))
                .collect(toList());
    }

    private String hentAktorId(String fnr) {
        try {
            return fodselnummerAktorService.hentAktoerIdForIdent(lagAktorRequest(fnr)).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet e) {
            throw new SystemException("Klarte ikke hente aktørId", e);
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
