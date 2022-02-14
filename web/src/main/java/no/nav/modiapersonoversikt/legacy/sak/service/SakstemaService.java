package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.modiapersonoversikt.legacy.sak.service.BulletproofKodeverkService.ARKIVTEMA;
import static no.nav.modiapersonoversikt.legacy.sak.service.SakstemaGrupperer.OPPFOLGING;

@SuppressWarnings("squid:S1166") // Either log or rethrow
public class SakstemaService {

    private static final String RESTERENDE_TEMA = "RESTERENDE_TEMA";

    @Autowired
    private DokumentMetadataService dokumentMetadataService;

    @Autowired
    private SakOgBehandlingService sakOgBehandlingService;

    @Autowired
    private BulletproofKodeverkService bulletproofKodeverkService;

    @Autowired
    private SakstemaGrupperer sakstemaGrupperer;

    private Map<String, Set<String>> grupperAlleSakstemaSomResterende(Map<String, Set<String>> grupperteSakstema) {
        Set<String> sakstema = grupperteSakstema.values().stream()
                .flatMap(Set::stream).collect(toSet());
        Map<String, Set<String>> map = new HashMap<>();
        map.put(RESTERENDE_TEMA, sakstema);
        return map;
    }

    public ResultatWrapper<List<Sakstema>> hentSakstema(List<Sak> saker, String fnr, boolean skalGruppere) {
        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(fnr);

        try {
            Map<String, List<Behandlingskjede>> behandlingskjeder = sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(fnr);
            Map<String, Set<String>> grupperteSakstema = sakstemaGrupperer.grupperSakstema(saker, wrapper.resultat, behandlingskjeder);

            if (!skalGruppere) {
                grupperteSakstema = grupperAlleSakstemaSomResterende(grupperteSakstema);
            }
            return opprettSakstemaresultat(saker, wrapper, grupperteSakstema, behandlingskjeder, skalGruppere);
        } catch (FeilendeBaksystemException e) {
            Map<String, Set<String>> grupperteSakstema = sakstemaGrupperer.grupperSakstema(saker, wrapper.resultat, emptyMap());
            wrapper.feilendeSystemer.add(e.getBaksystem());
            return opprettSakstemaresultat(saker, wrapper, grupperteSakstema, emptyMap(), skalGruppere);
        }
    }

    private ResultatWrapper<List<Sakstema>> opprettSakstemaresultat(
            List<Sak> saker, ResultatWrapper<List<DokumentMetadata>> wrapper,
            Map<String, Set<String>> grupperteSakstema, Map<String, List<Behandlingskjede>> behandlingskjeder, boolean skalGruppere) {

        return grupperteSakstema.entrySet()
                .stream()
                .map(entry -> opprettSakstemaForEnTemagruppe(entry, saker, wrapper.resultat, behandlingskjeder, skalGruppere))
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
            Map<String, List<Behandlingskjede>> behandlingskjeder, boolean skalGruppere) {

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
                            .withBehandlingskjeder(Optional.ofNullable(behandlingskjeder.get(temakode)).orElse(emptyList()))
                            .withTilhorendeSaker(tilhorendeSaker)
                            .withTemanavn(temanavn.resultat)
                            .withDokumentMetadata(tilhorendeDokumentMetadata)
                            .withErGruppert(erGruppert);
                })
                .collect(toList());

        List<Sakstema> ekstraGruppering = skalGruppere ? grupperSykepengerOgSykemelding(sakstema) : sakstema;

        return new ResultatWrapper<>(ekstraGruppering, feilendeBaksystemer);
    }

    private List<Sakstema> grupperSykepengerOgSykemelding(List<Sakstema> sakstema) {
        List<Sakstema> listeKopi = new ArrayList<>(sakstema);

        Optional<Sakstema> maybeSykepenger = listeKopi.stream().filter(st -> st.temakode.equals("SYK")).findFirst();
        Optional<Sakstema> maybeSykemelding = listeKopi.stream().filter(st -> st.temakode.equals("SYM")).findFirst();
        if (maybeSykepenger.isPresent() && maybeSykemelding.isPresent()) {
            Sakstema sykepenger = maybeSykepenger.get();
            Sakstema sykemelding = maybeSykemelding.get();
            if (sykepenger.erGruppert && sykemelding.erGruppert) {
                return flettSykepengerOgSykemelding(listeKopi, sykepenger, sykemelding, true);
            } else if (sykepenger.erIkkeTomtTema() && sykemelding.erIkkeTomtTema()) {
                return flettSykepengerOgSykemelding(listeKopi, sykepenger, sykemelding, false);
            }
        }
        return sakstema;
    }

    private List<Sakstema> flettSykepengerOgSykemelding(List<Sakstema> sakstema, Sakstema sykepenger, Sakstema sykemelding, boolean harOppfolging) {
        sakstema.remove(sykepenger);
        sakstema.remove(sykemelding);

        List<Behandlingskjede> behandlingskjeder = concat(sykemelding.behandlingskjeder.stream(), sykepenger.behandlingskjeder.stream()).collect(toList());
        List<DokumentMetadata> dokumentMetadata = concat(sykemelding.dokumentMetadata.stream(), sykepenger.dokumentMetadata.stream()).collect(toList());
        List<Sak> tilhorendeSaker = concat(sykemelding.tilhorendeSaker.stream(), sykepenger.tilhorendeSaker.stream()).collect(toList());
        List<Integer> feilkoder = concat(sykemelding.feilkoder.stream(), sykepenger.feilkoder.stream()).collect(toList());

        String temanavn = harOppfolging ? "Sykmelding, sykepenger og oppfølging" : "Sykmelding og sykepenger";

        sakstema.add(
                new Sakstema()
                        .withBehandlingskjeder(behandlingskjeder)
                        .withDokumentMetadata(dokumentMetadata)
                        .withErGruppert(harOppfolging)
                        .withTemakode("SYK_SYM")
                        .withTemanavn(temanavn)
                        .withTilhorendeSaker(tilhorendeSaker)
                        .withFeilkoder(feilkoder)
        );

        return sakstema;
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

    private static Predicate<DokumentMetadata> tilhorendeFraJoark(List<Sak> tilhorendeSaker) {
        return dm -> tilhorendeSaker.stream().map(Sak::getSaksId).collect(toList()).contains(dm.getTilhorendeSakid());
    }

    private static Predicate<DokumentMetadata> tilhorendeFraHenvendelse(Map.Entry<String, Set<String>> temagruppe, String temakode) {
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
