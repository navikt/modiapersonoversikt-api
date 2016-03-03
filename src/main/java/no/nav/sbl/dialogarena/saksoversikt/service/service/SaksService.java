package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandlingskjede;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.utils.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.*;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.OPPFOLGING;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.concat;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.optional;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem.HENVENDELSE;

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

        return new SakstemaResultatWrapper(
                grupperteSakstema.entrySet()
                        .stream()
                        .map(entry -> opprettSakstemaForEnTemagruppe(entry, saker, wrapper.dokumentMetadata, fnr))
                        .flatMap(Collection::stream).collect(Collectors.toList()),
                wrapper.feiledeSystemer);
    }

    protected List<Sakstema> opprettSakstemaForEnTemagruppe(Map.Entry<String, Set<String>> temagruppe, List<Sak> alleSaker, List<DokumentMetadata> alleDokumentMetadata, String fnr) {

        Predicate<String> finnesTemaKodeIKodeverk = temaKode -> bulletproofKodeverkService.finnesTemaKodeIKodeverk(temaKode, BulletproofKodeverkService.ARKIVTEMA);
        Predicate<String> ikkeGruppertOppfolingssak = temakode -> (RESTERENDE_TEMA.equals(temagruppe.getKey()) || !OPPFOLGING.equals(temakode));

        Map<String, List<Behandlingskjede>> behandlingskjederGruppertPaaTema = sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(fnr);

        return temagruppe.getValue().stream()
                .filter(finnesTemaKodeIKodeverk)
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

                    return new Sakstema()
                            .withTemakode(temakode)
                            .withBehandlingskjeder(optional(behandlingskjederGruppertPaaTema.get(temakode)).orElse(emptyList()))
                            .withTilhorendeSaker(tilhorendeSaker)
                            .withTemanavn(temanavn(temagruppe, temakode))
                            .withDokumentMetadata(tilhorendeDokumentMetadata)
                            .withErGruppert(erGruppert);
                })
                .collect(toList());
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
