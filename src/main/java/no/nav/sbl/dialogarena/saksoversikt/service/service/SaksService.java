package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.HENVENDELSE;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.OPPFOLGING;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.concat;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.optional;

public class SaksService {

    public static final String RESTERENDE_TEMA = "RESTERENDE_TEMA";

    @Inject
    private DokumentMetadataService dokumentMetadataService;

    @Inject
    private SakOgBehandlingService sakOgBehandlingService;

    @Inject
    private GsakSakerService gsakSakerService;

    @Inject
    private PesysService pensjonService;

    @Inject
    private HenvendelseService henvendelseService;

    @Inject
    private BulletproofKodeverkService bulletproofKodeverkService;

    @Inject
    private SakstemaGrupperer sakstemaGrupperer;

    public List<Soknad> hentPaabegynteSoknader(String fnr) {
        return henvendelseService.hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus.UNDER_ARBEID, fnr);
    }

    public ResultatWrapper<List<Sak>> hentAlleSaker(String fnr) {
        Set<Baksystem> feilendeBaksystemer = new HashSet<>();

        Optional<Stream<Sak>> maybeFraGSak;
        Optional<Stream<Sak>> maybeFraPesys;

        try {
            maybeFraGSak = gsakSakerService.hentSaker(fnr);
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystemer.add(e.getBaksystem());
            maybeFraGSak = Optional.empty();
        }

        try {
            maybeFraPesys = pensjonService.hentSakstemaFraPesys(fnr);
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystemer.add(e.getBaksystem());
            maybeFraPesys = Optional.empty();
        }

        Stream<Sak> fraGSak = maybeFraGSak.orElse(Stream.empty());
        Stream<Sak> fraPesys = maybeFraPesys.orElse(Stream.empty());

        return new ResultatWrapper<>(concat(fraGSak, fraPesys).collect(toList()), feilendeBaksystemer);
    }

    private Map grupperAlleSakstemaSomResterende(Map<String, Set<String>> grupperteSakstema) {
        Set<String> sakstema = grupperteSakstema.entrySet().stream().map(stringSetEntry -> stringSetEntry.getValue())
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

    private ResultatWrapper opprettSakstemaresultat(List<Sak> saker, ResultatWrapper<List<DokumentMetadata>> wrapper,
                                                    Map<String,Set<String>> grupperteSakstema, Map<String, List<Behandlingskjede>> behandlingskjeder) {

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

    protected ResultatWrapper<List<Sakstema>> opprettSakstemaForEnTemagruppe(Map.Entry<String, Set<String>> temagruppe, List<Sak> alleSaker, List<DokumentMetadata> alleDokumentMetadata, Map<String, List<Behandlingskjede>> behandlingskjeder) {
        Predicate<String> ikkeGruppertOppfolingssak = temakode -> (RESTERENDE_TEMA.equals(temagruppe.getKey()) || !OPPFOLGING.equals(temakode));
        Set<Baksystem> feilendeBaksystemer = new HashSet();

        List<Sakstema> sakstema = temagruppe.getValue().stream()
                .filter(ikkeGruppertOppfolingssak)
                .map(temakode -> {
                    List<Sak> tilhorendeSaker = alleSaker.stream()
                            .filter(sak -> tilhorerSakTemagruppe(sak, temakode, temagruppe))
                            .collect(toList());

                    List<DokumentMetadata> tilhorendeDokumentMetadata = alleDokumentMetadata
                            .stream()
                            .filter(tilhorendeFraJoark(tilhorendeSaker).or(tilhorendeFraHenvendelse(temagruppe, temakode)))
                            .collect(toList());

                    boolean erGruppert = RESTERENDE_TEMA.equals(temagruppe.getKey()) ? false : true;

                    String temanavn = temakode;
                    try {
                        temanavn = temanavn(temagruppe, temakode);
                    } catch (FeilendeBaksystemException e) {
                        if (!temagruppe.equals(RESTERENDE_TEMA)) {
                            temanavn += " og oppfølging";
                        }
                        feilendeBaksystemer.add(e.getBaksystem());
                    }

                    return new Sakstema()
                            .withTemakode(temakode)
                            .withBehandlingskjeder(optional(behandlingskjeder.get(temakode)).orElse(emptyList()))
                            .withTilhorendeSaker(tilhorendeSaker)
                            .withTemanavn(temanavn)
                            .withDokumentMetadata(tilhorendeDokumentMetadata)
                            .withErGruppert(erGruppert);
                })
                .collect(toList());
        return new ResultatWrapper<>(sakstema, feilendeBaksystemer);
    }

    private String temanavn(Map.Entry<String, Set<String>> temagruppe, String temakode) {
        if (temagruppe.getKey().equals(RESTERENDE_TEMA)) {
            return bulletproofKodeverkService.getTemanavnForTemakode(temakode, ARKIVTEMA);
        } else {
            return bulletproofKodeverkService.getTemanavnForTemakode(temakode, ARKIVTEMA) + " og oppfølging";
        }
    }

    private Predicate<DokumentMetadata> tilhorendeFraJoark(List<Sak> tilhorendeSaker) {
        return dm -> tilhorendeSaker.stream().map(Sak::getSaksId).collect(toList()).contains(dm.getTilhorendeSakid());
    }

    private Predicate<DokumentMetadata> tilhorendeFraHenvendelse(Map.Entry<String, Set<String>> temagruppe, String temakode) {
        return dm -> dm.getBaksystem().equals(HENVENDELSE)
                && (dm.getTemakode().equals(temakode)
                || (!temagruppe.getKey().equals(RESTERENDE_TEMA) && dm.getTemakode().equals(OPPFOLGING)));
    }

    private boolean tilhorerSakTemagruppe(Sak sak, String temakode, Map.Entry<String, Set<String>> temagruppe) {
        if (!RESTERENDE_TEMA.equals(temagruppe.getKey())) {
            return temakode.equals(sak.getTemakode()) || sak.temakode().equals(OPPFOLGING);
        } else {
            return temakode.equals(sak.getTemakode());
        }
    }
}
