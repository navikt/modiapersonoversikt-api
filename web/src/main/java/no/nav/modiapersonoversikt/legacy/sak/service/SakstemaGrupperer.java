package no.nav.modiapersonoversikt.legacy.sak.service;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandlingskjede;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sak;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SakstemaGrupperer {
    public static Set<String> hentAlleTema(
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
}
