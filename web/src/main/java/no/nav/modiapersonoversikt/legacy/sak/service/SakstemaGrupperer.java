package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandlingskjede;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sak;
import no.nav.modiapersonoversikt.legacy.sak.utils.Java8Utils;
import no.nav.modiapersonoversikt.legacy.sak.utils.TemagrupperHenter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.*;

public class SakstemaGrupperer {

    public static final String TEMAGRUPPE_RESTERENDE_TEMA = "RESTERENDE_TEMA";
    public static final String OPPFOLGING = "OPP";

    @Autowired
    private TemagrupperHenter temagrupperHenter;

    private Predicate<Map.Entry<String, Set<String>>> harMinstEtTema = entrySet -> entrySet.getValue().size() > 1;
    private Predicate<Map.Entry<String, Set<String>>> inneholderOppfolgingstema = entrySet -> entrySet.getValue().contains(OPPFOLGING);
    private Predicate<Map.Entry<String, Set<String>>> erTemaMedOppfolging = harMinstEtTema.and(inneholderOppfolgingstema);

    public Map<String, Set<String>> grupperSakstema(List<Sak> saker, List<DokumentMetadata> dokumentMetadata, Map<String, List<Behandlingskjede>> behandlingskjeder) {

        Map<String, List<Pair<String, String>>> grupperteSaker = grupperSakstemaITemagrupper(saker, dokumentMetadata, behandlingskjeder);
        Map<String, Set<String>> grupperteSakstema = grupperTemagruppePar(grupperteSaker);

        Map<String, Set<String>> temaMedOppfolging = grupperteSakstema.entrySet().stream()
                .filter(erTemaMedOppfolging)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        Set<String> ugrupperteTema = Java8Utils.concat(
                saker.stream().map(Sak::getTemakode),
                dokumentMetadata.stream()
                        .filter(dm -> dm.getBaksystem().contains(Baksystem.HENVENDELSE))
                        .map(DokumentMetadata::getTemakode)
                ,
                behandlingskjeder.keySet().stream()
        )
                .filter(tema -> temaFinnesIkkeITemagruppe(tema, temaMedOppfolging))
                .collect(toSet());

        return new HashMap<String, Set<String>>() {{
            putAll(temaMedOppfolging);
            put(TEMAGRUPPE_RESTERENDE_TEMA, ugrupperteTema);
        }};
    }

    private Map<String, List<Pair<String, String>>> grupperSakstemaITemagrupper(List<Sak> saker, List<DokumentMetadata> dokumentMetadata, Map<String, List<Behandlingskjede>> behandlingskjeder) {

        Map<String, List<String>> temagrupperMedTema = temagrupperHenter.genererTemagrupperMedTema();

        Map<String, List<Pair<String, String>>> parFraSaker = saker.stream()
                .filter(harDokumentMetadata(dokumentMetadata))
                .map(sak -> finnTemagruppeForSak(temagrupperMedTema, sak))
                .flatMap(List::stream)
                .collect(groupingBy(Pair::getKey));

        Map<String, List<Pair<String, String>>> parFraDokumentMetadata = dokumentMetadata.stream()
                .filter(dm -> dm.getBaksystem().contains(Baksystem.HENVENDELSE))
                .map(dm -> finnTemagruppeForDokumentMetadata(temagrupperMedTema, dm))
                .flatMap(List::stream)
                .collect(groupingBy(Pair::getKey));

        Map<String, List<Pair<String, String>>> parFraBehandlingskjeder = behandlingskjeder.entrySet().stream()
                .map(entry -> finnTemagruppeForBehandlingskjede(temagrupperMedTema, entry.getKey()))
                .flatMap(List::stream)
                .collect(groupingBy(Pair::getKey));

        Java8Utils.concat(
                parFraDokumentMetadata.entrySet().stream(),
                parFraBehandlingskjeder.entrySet().stream()
        ).forEach(entry -> {
                    parFraSaker.computeIfPresent(entry.getKey(), (k, v) -> {
                        v.addAll(entry.getValue());
                        return v;
                    });
                    parFraSaker.computeIfAbsent(entry.getKey(), k -> entry.getValue());
                }
        );

        return parFraSaker;
    }

    private Map<String, Set<String>> grupperTemagruppePar
            (Map<String, List<Pair<String, String>>> grupperteSaker) {
        return grupperteSaker.entrySet()
                .stream().collect(toMap(Map.Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .map(pair -> pair.getValue())
                                .collect(toSet())));
    }

    private static boolean temaFinnesIkkeITemagruppe(String tema, Map<String, Set<String>> temagrupper) {
        return temagrupper.entrySet().stream()
                .noneMatch(map -> map.getValue().contains(tema));
    }

    private static List<Pair<String, String>> finnTemagruppeForSak(Map<String, List<String>> temagrupperMedTemaer, Sak sak) {
        return temagrupperMedTemaer.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(sak.getTemakode()))
                .map(entry -> new ImmutablePair<>(entry.getKey(), sak.getTemakode()))
                .collect(toList());
    }

    private static List<Pair<String, String>> finnTemagruppeForBehandlingskjede(Map<String, List<String>> temagrupperMedTemaer, String temakode) {
        return temagrupperMedTemaer.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(temakode))
                .map(entry -> new ImmutablePair<>(entry.getKey(), temakode))
                .collect(toList());
    }

    private static List<Pair<String, String>> finnTemagruppeForDokumentMetadata(Map<String, List<String>> temagruppeMedTemaer, DokumentMetadata dm) {
        return temagruppeMedTemaer.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(dm.getTemakode()))
                .map(entry -> new ImmutablePair<>(entry.getKey(), dm.getTemakode()))
                .collect(toList());
    }

    private Predicate<Sak> harDokumentMetadata(List<DokumentMetadata> dokumentMetadata) {
        return harInnholdIHenvendelse(dokumentMetadata).or(harInnholdIJoark(dokumentMetadata));
    }

    private Predicate<Sak> harInnholdIJoark(List<DokumentMetadata> dokumentMetadata) {
        return sak -> dokumentMetadata.stream().anyMatch(dm -> sak.getSaksId().equals(dm.getTilhorendeSakid()));
    }

    private Predicate<Sak> harInnholdIHenvendelse(List<DokumentMetadata> dokumentMetadata) {
        return sak -> dokumentMetadata.stream().anyMatch(dm -> dm.getBaksystem().contains(Baksystem.HENVENDELSE) && dm.getTemakode().equals(sak.getTemakode()));
    }
}
