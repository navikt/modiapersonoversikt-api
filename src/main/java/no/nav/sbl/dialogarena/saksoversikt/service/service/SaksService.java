package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.*;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.AlleSakerResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.DokumentMetadataResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.resultatwrappere.SakstemaResultatWrapper;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.OPPFOLGING;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.concat;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.optional;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Baksystem.*;

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

    public List<Record<Soknad>> hentPaabegynteSoknader(String fnr) {
        return on(henvendelseService.hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus.UNDER_ARBEID, fnr)).collect();
    }

    public AlleSakerResultatWrapper hentAlleSaker(String fnr) {

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

        return new AlleSakerResultatWrapper(concat(fraGSak, fraPesys).collect(toList()), feilendeBaksystemer);
    }

    private Map grupperAlleSakstemaSomResterende(Map<String, Set<String>> grupperteSakstema) {
        Set<String> sakstema = grupperteSakstema.entrySet().stream().map(stringSetEntry -> stringSetEntry.getValue())
                .flatMap(Set::stream).collect(Collectors.toSet());
        Map<String, Set<String>> map = new HashMap<>();
        map.put(RESTERENDE_TEMA, sakstema);
        return map;

    }

    public SakstemaResultatWrapper hentSakstema(List<Sak> saker, String fnr, boolean skalGruppere) {
        DokumentMetadataResultatWrapper wrapper = dokumentMetadataService.hentDokumentMetadata(saker, fnr);
        Map<String, Set<String>> grupperteSakstema = sakstemaGrupperer.grupperSakstema(saker, wrapper.dokumentMetadata);

        if (!skalGruppere) {
            grupperteSakstema = grupperAlleSakstemaSomResterende(grupperteSakstema);
        }

        try {
            Map<String, List<Behandlingskjede>> behandlingskjeder = sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(fnr);
            return OpprettSakstemaresultat(saker, wrapper, grupperteSakstema, behandlingskjeder);
        } catch (FeilendeBaksystemException e) {
            wrapper.feiledeSystemer.add(e.getBaksystem());
            return OpprettSakstemaresultat(saker, wrapper, grupperteSakstema, emptyMap());
        }
    }

    private SakstemaResultatWrapper OpprettSakstemaresultat(List<Sak> saker, DokumentMetadataResultatWrapper wrapper, Map<String,
            Set<String>> grupperteSakstema, Map<String, List<Behandlingskjede>> behandlingskjeder) {

        List<SakstemaResultatWrapper> sakstemaResultatWrapperListe = grupperteSakstema.entrySet()
                .stream()
                .map(entry -> opprettSakstemaForEnTemagruppe(entry, saker, wrapper.dokumentMetadata, behandlingskjeder)).collect(toList());

        return new SakstemaResultatWrapper(
                sakstemaResultatWrapperListe.stream().map(entry -> entry.sakstema).flatMap(Collection::stream).collect(toList()),
                concat(
                        sakstemaResultatWrapperListe.stream()
                                .map(entry -> entry.feilendeSystemer)
                                .flatMap(Collection::stream),
                        wrapper.feiledeSystemer.stream()
                ).collect(Collectors.toSet()));
    }

    protected SakstemaResultatWrapper opprettSakstemaForEnTemagruppe(Map.Entry<String, Set<String>> temagruppe, List<Sak> alleSaker, List<DokumentMetadata> alleDokumentMetadata, Map<String, List<Behandlingskjede>> behandlingskjeder) {

        Predicate<String> ikkeGruppertOppfolingssak = temakode -> (RESTERENDE_TEMA.equals(temagruppe.getKey()) || !OPPFOLGING.equals(temakode));

        Set<Baksystem> feilendeBaksystemer = new HashSet();

        List<Sakstema> collect = temagruppe.getValue().stream()
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

        return new SakstemaResultatWrapper(collect, feilendeBaksystemer);
    }

    private String temanavn(Map.Entry<String, Set<String>> temagruppe, String temakode) {
        if (temagruppe.getKey().equals(RESTERENDE_TEMA)) {
            return bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA);
        } else {
            return bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA) + " og oppfølging";
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
