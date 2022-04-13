package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.resultatwrappere.ResultatWrapper;

import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem.HENVENDELSE;

@SuppressWarnings("squid:S1166") // Either log or rethrow
public class SakstemaService {

    private static final Logger LOG = LoggerFactory.getLogger(SakstemaService.class);

    @Autowired
    private DokumentMetadataService dokumentMetadataService;

    @Autowired
    private SakOgBehandlingService sakOgBehandlingService;

    @Autowired
    private EnhetligKodeverk.Service kodeverk;

    public ResultatWrapper<List<Sakstema>> hentSakstema(List<Sak> saker, String fnr) {
        ResultatWrapper<List<DokumentMetadata>> wrapper = dokumentMetadataService.hentDokumentMetadata(fnr);

        try {
            Map<String, List<Behandlingskjede>> behandlingskjeder = sakOgBehandlingService.hentBehandlingskjederGruppertPaaTema(fnr);
            Set<String> temakoder = SakstemaGrupperer.hentAlleTema(saker, wrapper.resultat, behandlingskjeder);

            return opprettSakstemaresultat(saker, wrapper, temakoder, behandlingskjeder);
        } catch (FeilendeBaksystemException e) {
            Set<String> temakoder = SakstemaGrupperer.hentAlleTema(saker, wrapper.resultat, emptyMap());
            wrapper.feilendeSystemer.add(e.getBaksystem());
            return opprettSakstemaresultat(saker, wrapper, temakoder, emptyMap());
        }
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

        return new ResultatWrapper<>(sakstema, feilendeBaksystemer);
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
        return dm -> tilhorendeSaker.stream().map(Sak::getSaksId).collect(toList()).contains(dm.getTilhorendeSakid());
    }

    private static Predicate<DokumentMetadata> tilhorendeFraHenvendelse(String temakode) {
        return dm -> dm.getBaksystem().contains(HENVENDELSE)
                && (dm.getTemakode().equals(temakode));
    }
}
