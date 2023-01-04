package no.nav.modiapersonoversikt.service.sakstema;

import no.nav.common.utils.EnvironmentUtils;
import no.nav.modiapersonoversikt.commondomain.sak.ResultatWrapper;
import no.nav.modiapersonoversikt.commondomain.sak.Baksystem;
import no.nav.modiapersonoversikt.commondomain.sak.FeilendeBaksystemException;
import no.nav.modiapersonoversikt.service.saf.domain.DokumentMetadata;
import no.nav.modiapersonoversikt.service.sakogbehandling.SakOgBehandlingService;
import no.nav.modiapersonoversikt.service.sakstema.domain.Sak;
import no.nav.modiapersonoversikt.service.sakstema.domain.Sakstema;
import no.nav.modiapersonoversikt.service.saf.SafService;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig;
import no.nav.modiapersonoversikt.service.sakstema.domain.Behandlingskjede;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.commondomain.sak.Baksystem.HENVENDELSE;

@SuppressWarnings("squid:S1166") // Either log or rethrow
public class SakstemaService {

    private static final Logger LOG = LoggerFactory.getLogger(SakstemaService.class);

    @Autowired
    private SafService safService;

    @Autowired
    private SakOgBehandlingService sakOgBehandlingService;

    @Autowired
    private EnhetligKodeverk.Service kodeverk;

    public ResultatWrapper<List<Sakstema>> hentSakstema(List<Sak> saker, String fnr) {
        ResultatWrapper<List<DokumentMetadata>> wrapper = safService.hentJournalposter(fnr);

        try {
            Map<String, List<Behandlingskjede>> behandlingskjeder = sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(fnr);
            Set<String> temakoder = hentAlleTema(saker, wrapper.resultat, behandlingskjeder);

            return opprettSakstemaresultat(saker, wrapper, temakoder, behandlingskjeder);
        } catch (FeilendeBaksystemException e) {
            Set<String> temakoder = hentAlleTema(saker, wrapper.resultat, emptyMap());
            wrapper.feilendeSystemer.add(e.getBaksystem());
            return opprettSakstemaresultat(saker, wrapper, temakoder, emptyMap());
        }
    }

    protected static Set<String> hentAlleTema(
            List<Sak> saker,
            List<DokumentMetadata> dokumentMetadata,
            Map<String, List<Behandlingskjede>> behandlingskjeder
    ) {
        Stream<String> sakerTema = saker.stream().map(Sak::getTemakode);

        Stream<String> dokumentTema = dokumentMetadata
                .stream()
                .filter((metadata) -> metadata.getBaksystem().contains(Baksystem.HENVENDELSE))
                .map(DokumentMetadata::getTemakode);

        Stream<String> behandlingskjedeTema = behandlingskjeder.keySet().stream();

        return Stream.of(sakerTema, dokumentTema, behandlingskjedeTema)
                .flatMap(Function.identity())
                .collect(Collectors.toSet());
    }

    private ResultatWrapper<List<Sakstema>> opprettSakstemaresultat(
            List<Sak> saker,
            ResultatWrapper<List<DokumentMetadata>> wrapper,
            Set<String> temakoder,
            Map<String, List<Behandlingskjede>> behandlingskjeder
    ) {
        return opprettSakstemaForEnTemagruppe(temakoder, saker, wrapper.resultat, behandlingskjeder)
                .withEkstraFeilendeBaksystemer(wrapper.feilendeSystemer);
    }

    protected static List<Sakstema> fjernGamleDokumenter(List<Sakstema> saker) {
        return saker
                .stream()
                .map((sak) -> {
                    var filtrerteDokument = sak.dokumentMetadata
                            .stream()
                            .filter((dokument) -> {
                                var erFraSAF = dokument.getBaksystem().size() == 1 && dokument.getBaksystem().contains(Baksystem.SAF);
                                var erFraForProdsetting = dokument.getDato().isBefore(getProdsettingsDato());
                                return !(erFraSAF && erFraForProdsetting);
                            })
                            .collect(Collectors.toList());
                    return sak.withDokumentMetadata(filtrerteDokument);
                })
                .collect(Collectors.toList());
    }

    private static LocalDateTime prodsettingsDato;
    protected static LocalDateTime getProdsettingsDato() {
        if (prodsettingsDato == null) {
            prodsettingsDato = LocalDate
                    .parse(EnvironmentUtils.getRequiredProperty("SAKSOVERSIKT_PRODSETTNINGSDATO"))
                    .atStartOfDay();
        }
        return prodsettingsDato;
    }

    protected ResultatWrapper<List<Sakstema>> opprettSakstemaForEnTemagruppe(
            Set<String> temakoder,
            List<Sak> alleSaker, List<DokumentMetadata> alleDokumentMetadata,
            Map<String, List<Behandlingskjede>> behandlingskjeder) {

        Set<Baksystem> feilendeBaksystemer = new HashSet<>();

        List<Sakstema> sakstema = temakoder.stream()
                .map(temakode -> {
                    List<Sak> tilhorendeSaker = sakerITemagruppe(alleSaker, temakode);
                    List<DokumentMetadata> tilhorendeDokumentMetadata = tilhorendeDokumentMetadata(alleDokumentMetadata, temakode, tilhorendeSaker);

                    ResultatWrapper<String> temanavn = getTemanavnForTemakode(temakode);
                    feilendeBaksystemer.addAll(temanavn.feilendeSystemer);

                    return new Sakstema()
                            .withTemakode(temakode)
                            .withBehandlingskjeder(Optional.ofNullable(behandlingskjeder.get(temakode)).orElse(emptyList()))
                            .withTilhorendeSaker(tilhorendeSaker)
                            .withTemanavn(temanavn.resultat)
                            .withDokumentMetadata(tilhorendeDokumentMetadata)
                            .withErGruppert(false);
                })
                .collect(toList());

        return new ResultatWrapper<>(fjernGamleDokumenter(sakstema), feilendeBaksystemer);
    }
    private List<DokumentMetadata> tilhorendeDokumentMetadata(List<DokumentMetadata> alleDokumentMetadata, String temakode, List<Sak> tilhorendeSaker) {
        return alleDokumentMetadata
                .stream()
                .filter(tilhorendeFraJoark(tilhorendeSaker).or(tilhorendeFraHenvendelse(temakode)))
                .collect(toList());
    }

    private List<Sak> sakerITemagruppe(List<Sak> alleSaker, String temakode) {
        return alleSaker.stream()
                .filter(sak -> temakode.equals(sak.getTemakode()))
                .collect(toList());
    }

    private ResultatWrapper<String> getTemanavnForTemakode(String temakode) {
        String temanavn = kodeverk.hentKodeverk(KodeverkConfig.ARKIVTEMA).hentVerdiEllerNull(temakode);
        if (temanavn == null) {
            LOG.warn("Fant ikke temanavn for temakode " + temakode + ". Bruker temakode som generisk tittel.");
            return new ResultatWrapper<>(temakode).withEkstraFeilendeSystem(Baksystem.KODEVERK);
        } else {
            return new ResultatWrapper<>(temanavn);
        }
    }

    private static Predicate<DokumentMetadata> tilhorendeFraJoark(List<Sak> tilhorendeSaker) {
        return dm -> tilhorendeSaker.stream().map(Sak::getSaksId).toList().contains(dm.getTilhorendeSakid());
    }

    private static Predicate<DokumentMetadata> tilhorendeFraHenvendelse(String temakode) {
        return dm -> dm.getBaksystem().contains(HENVENDELSE)
                && (dm.getTemakode().equals(temakode));
    }
}
