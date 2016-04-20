package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.FeilendeBaksystemException;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.OPPFOLGING;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.optional;

@SuppressWarnings("squid:S1166") // Either log or rethrow
public class SakstemaService {

    public static final String RESTERENDE_TEMA = "RESTERENDE_TEMA";

    @Inject
    private DokumentMetadataService dokumentMetadataService;

    @Inject
    private SakOgBehandlingService sakOgBehandlingService;

    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;

    @Inject
    private SakstemaGrupperer sakstemaGrupperer;

    private Map<String, Set<String>> grupperAlleSakstemaSomResterende(Map<String, Set<String>> grupperteSakstema) {
        Set<String> sakstema = grupperteSakstema.entrySet().stream().map(Map.Entry::getValue)
                .flatMap(Set::stream).collect(toSet());
        Map<String, Set<String>> map = new HashMap<>();
        map.put(RESTERENDE_TEMA, sakstema);
        return map;
    }

    public ResultatWrapper<List<Sakstema>> hentSakstema(List<Sak> saker, String fnr, boolean skalGruppere) {
        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(saker, fnr);

        try {
            Map<String, List<Behandlingskjede>> behandlingskjeder = sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(fnr);
            Map<String, Set<String>> grupperteSakstema = sakstemaGrupperer.grupperSakstema(saker, wrapper.resultat, behandlingskjeder);

            if (!skalGruppere) {
                grupperteSakstema = grupperAlleSakstemaSomResterende(grupperteSakstema);
            }
            return opprettSakstemaresultat(saker, wrapper, grupperteSakstema, behandlingskjeder);
        } catch (FeilendeBaksystemException e) {
            Map<String, Set<String>> grupperteSakstema = sakstemaGrupperer.grupperSakstema(saker, wrapper.resultat, emptyMap());
            wrapper.feilendeSystemer.add(e.getBaksystem());
            return opprettSakstemaresultat(saker, wrapper, grupperteSakstema, emptyMap());
        }
    }

    private ResultatWrapper<List<Sakstema>> opprettSakstemaresultat(
            List<Sak> saker, ResultatWrapper<List<DokumentMetadata>> wrapper,
            Map<String, Set<String>> grupperteSakstema, Map<String, List<Behandlingskjede>> behandlingskjeder) {

        return grupperteSakstema.entrySet()
                .stream()
                .map(entry -> opprettSakstemaForEnTemagruppe(entry, saker, wrapper.resultat, behandlingskjeder))
                .reduce(new ResultatWrapper<>(new ArrayList<>()), (accumulator, resultatwrapper) -> {
                    accumulator.resultat.addAll(resultatwrapper.resultat);
                    accumulator.feilendeSystemer.addAll(resultatwrapper.feilendeSystemer);
                    return accumulator;
                })
                .withEkstraFeilendeBaksystemer(wrapper.feilendeSystemer);
    }

    protected ResultatWrapper<List<Sakstema>> opprettSakstemaForEnTemagruppe(
            Map.Entry<String, Set<String>> temagruppe,
            List<Sak> alleSaker, List<DokumentMetadata> alleDokumentMetadata,
            Map<String, List<Behandlingskjede>> behandlingskjeder) {

        Predicate<String> ikkeGruppertOppfolingssak = temakode -> (RESTERENDE_TEMA.equals(temagruppe.getKey()) || !OPPFOLGING.equals(temakode));
        Set<Baksystem> feilendeBaksystemer = new HashSet<>();

        List<Sakstema> sakstema = temagruppe.getValue().stream()
                .filter(ikkeGruppertOppfolingssak)
                .map(temakode -> {
                    List<Sak> tilhorendeSaker = sakerITemagruppe(temagruppe, alleSaker, temakode);
                    List<DokumentMetadata> tilhorendeDokumentMetadata = tilhorendeDokumentMetadata(temagruppe, alleDokumentMetadata, temakode, tilhorendeSaker);

                    boolean erGruppert = !RESTERENDE_TEMA.equals(temagruppe.getKey());

                    ResultatWrapper<String> temanavn = temanavnFraKodeverk(temagruppe, temakode);
                    feilendeBaksystemer.addAll(temanavn.feilendeSystemer);

                    return new Sakstema()
                            .withTemakode(temakode)
                            .withBehandlingskjeder(optional(behandlingskjeder.get(temakode)).orElse(emptyList()))
                            .withTilhorendeSaker(tilhorendeSaker)
                            .withTemanavn(temanavn.resultat)
                            .withDokumentMetadata(tilhorendeDokumentMetadata)
                            .withErGruppert(erGruppert);
                })
                .collect(toList());
        return new ResultatWrapper<>(sakstema, feilendeBaksystemer);
    }

    private List<DokumentMetadata> tilhorendeDokumentMetadata(Map.Entry<String, Set<String>> temagruppe, List<DokumentMetadata> alleDokumentMetadata, String temakode, List<Sak> tilhorendeSaker) {
        return alleDokumentMetadata
                .stream()
                .filter(tilhorendeFraJoark(tilhorendeSaker).or(tilhorendeFraHenvendelse(temagruppe, temakode)))
                .collect(toList());
    }

    private List<Sak> sakerITemagruppe(Map.Entry<String, Set<String>> temagruppe, List<Sak> alleSaker, String temakode) {
        return alleSaker.stream()
                .filter(sak -> tilhorerSakTemagruppe(sak, temakode, temagruppe))
                .collect(toList());
    }

    private ResultatWrapper<String> temanavnFraKodeverk(Map.Entry<String, Set<String>> temagruppe, String temakode) {
        ResultatWrapper<String> temanavnForTemakode = bulletproofKodeverkService.getTemanavnForTemakode(temakode, ARKIVTEMA);
        if (temagruppe.getKey().equals(RESTERENDE_TEMA)) {
            return temanavnForTemakode;
        } else {
            return new ResultatWrapper<>(temanavnForTemakode.resultat + " og oppfølging", temanavnForTemakode.feilendeSystemer);
        }
    }

    private static final Predicate<DokumentMetadata> tilhorendeFraJoark(List<Sak> tilhorendeSaker) {
        return dm -> tilhorendeSaker.stream().map(Sak::getSaksId).collect(toList()).contains(dm.getTilhorendeSakid());
    }

    private static final Predicate<DokumentMetadata> tilhorendeFraHenvendelse(Map.Entry<String, Set<String>> temagruppe, String temakode) {
        return dm -> dm.getBaksystem().contains(HENVENDELSE)
                && (dm.getTemakode().equals(temakode)
                || (!temagruppe.getKey().equals(RESTERENDE_TEMA) && dm.getTemakode().equals(OPPFOLGING)));
    }

    private static boolean tilhorerSakTemagruppe(Sak sak, String temakode, Map.Entry<String, Set<String>> temagruppe) {
        if (!RESTERENDE_TEMA.equals(temagruppe.getKey())) {
            return temakode.equals(sak.getTemakode()) || sak.temakode().equals(OPPFOLGING);
        } else {
            return temakode.equals(sak.getTemakode());
        }
    }
}
