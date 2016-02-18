package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandlingskjede;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Baksystem;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Sak;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.Journalpost;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.SakstemaGrupperer.OPPFOLGING;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.TemagrupperHenter.hentTemagruppenavnForTemagruppe;

public class SaksService {

    public static final String RESTERENDE_TEMA = "RESTERENDE_TEMA";

    public static final Function<Sakstema, LocalDate> NYESTE_DATO = (st) -> st.dokumentMetadata.stream()
            .map(DokumentMetadata::getDato)
            .sorted(reverseOrder())
            .findFirst()
            .orElse(LocalDate.MIN);

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

    @Inject
    private InnsynJournalService innsynJournalService;


    public Optional<Stream<Journalpost>> hentJournalpostListe(String fnr) {
        Optional<Stream<Journalpost>> alleSaker = innsynJournalService.joarkSakhentTilgjengeligeJournalposter(hentAlleSaker(fnr));

        return alleSaker;
    }

    public List<Record<Soknad>> hentPaabegynteSoknader(String fnr) {
        return on(henvendelseService.hentHenvendelsessoknaderMedStatus(Soknad.HenvendelseStatus.UNDER_ARBEID, fnr)).collect();
    }

    public List<Sak> hentAlleSaker(String fnr) {
        Stream<Sak> fraGsak = gsakSakerService.hentSaker(fnr).orElse(Stream.empty());
        Stream<Sak> fraPesys = pensjonService.hentSakstemaFraPesys(fnr).orElse(Stream.empty());
        return concat(fraGsak, fraPesys).collect(toList());
    }

    private Map grupperAlleSakstemaSomResterende(Map<String, Set<String>> grupperteSakstema) {
        Set<String> sakstema = grupperteSakstema.entrySet().stream().map(stringSetEntry -> stringSetEntry.getValue())
                .flatMap(Set::stream).collect(Collectors.toSet());
        Map<String, Set<String>> map = new HashMap<>();
        map.put(RESTERENDE_TEMA, sakstema);
        return map;

    }


    public Stream<Sakstema> hentSakstema(List<Sak> saker, String fnr, boolean skalGruppere) {
        List<DokumentMetadata> dokumentMetadata = dokumentMetadataService.hentDokumentMetadata(saker, fnr);
        Map<String, Set<String>> grupperteSakstema = sakstemaGrupperer.grupperSakstema(saker, dokumentMetadata);

        if (!skalGruppere) {
            grupperteSakstema = grupperAlleSakstemaSomResterende(grupperteSakstema);
        }

        return grupperteSakstema.entrySet().stream()
                .map(entry -> opprettSakstemaForEnTemagruppe(entry, saker, dokumentMetadata, fnr))
                .flatMap(Collection::stream)
                .sorted(comparing(NYESTE_DATO, reverseOrder()));
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
                            .filter(sak -> tilhorerSakTemagruppe(sak, temakode))
                            .collect(toList());

                    List<DokumentMetadata> tilhorendeDokumentMetadata = alleDokumentMetadata
                            .stream()
                            .filter(tilhorendeFraJoark(tilhorendeSaker).or(tilhorendeFraHenvendelse(temagruppe, temakode)))
                            .collect(toList());

                    return new Sakstema()
                            .withTemakode(temakode)
                            .withBehandlingskjeder(optional(behandlingskjederGruppertPaaTema.get(temakode)).orElse(emptyList()))
                            .withTilhorendeSaker(tilhorendeSaker)
                            .withTemanavn(temanavn(temagruppe, temakode))
                            .withDokumentMetadata(tilhorendeDokumentMetadata);
                })
                .collect(toList());
    }

    private String temanavn(Map.Entry<String, Set<String>> temagruppe, String temakode) {
        if (temagruppe.getKey().equals(RESTERENDE_TEMA)) {
            return bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA);
        } else {
            return hentTemagruppenavnForTemagruppe(temagruppe.getKey()) + " → " + bulletproofKodeverkService.getTemanavnForTemakode(temakode, BulletproofKodeverkService.ARKIVTEMA) + " og oppfølging";
        }
    }

    private Predicate<DokumentMetadata> tilhorendeFraJoark(List<Sak> tilhorendeSaker) {
        return dokumentMetadata1 -> tilhorendeSaker.stream().map(Sak::getSaksId).collect(toList()).contains(dokumentMetadata1.getTilhorendeSakid());
    }

    private Predicate<DokumentMetadata> tilhorendeFraHenvendelse(Map.Entry<String, Set<String>> temagruppe, String temakode) {
        return dm -> dm.getBaksystem().equals(Baksystem.HENVENDELSE)
                && (dm.getTemakode().equals(temakode)
                || (!temagruppe.getKey().equals(RESTERENDE_TEMA) && dm.getTemakode().equals(OPPFOLGING)));
    }

    private boolean tilhorerSakTemagruppe(Sak sak, String temakode) {
        return temakode.equals(sak.getTemakode());

    }
}
