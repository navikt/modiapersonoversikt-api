package no.nav.modiapersonoversikt.legacy.sak.service;

import kotlin.Pair;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Baksystem;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandlingskjede;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.DokumentMetadata;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Sak;
import no.nav.modiapersonoversikt.legacy.sak.utils.Konstanter;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.core.IsEqual.equalTo;

public class SakstemaGruppererTest {

    @Test
    public void gruppererTemaFraSakerDokumentMetadataOgBehandlingskjeder() {
        Set<String> temakoder = SakstemaGrupperer.hentAlleTema(
                lagSaker(Konstanter.DAGPENGER, Konstanter.KONTROLL),
                lagDokument(List.of(Konstanter.OPPFOLGING, Konstanter.ARBEIDSAVKLARINGSPENGER), List.of("KNA", "IND")),
                lagBehandlingskjeder(Konstanter.FORELDREPENGER, Konstanter.DAGPENGER, Konstanter.OPPFOLGING)
        );

        assertThat(temakoder, hasItems(
                Konstanter.DAGPENGER,
                Konstanter.KONTROLL,
                Konstanter.OPPFOLGING,
                Konstanter.ARBEIDSAVKLARINGSPENGER,
                Konstanter.FORELDREPENGER
        ));

        assertThat(temakoder, not(hasItems(
                "KNA",
                "IND"
        )));

        assertThat(temakoder.size(), is(5));
    }

    private static List<Sak> lagSaker(String ...temakoder) {
        return Stream.of(temakoder)
                .map(temakode -> new Sak().withTemakode(temakode))
                .collect(Collectors.toList());
    }

    private static List<DokumentMetadata> lagDokument(List<String> henvendelseTemakoder, List<String> joarkTemakoder) {
        Stream<DokumentMetadata> henvendelseDokument = henvendelseTemakoder
                .stream()
                .map(temakode -> new DokumentMetadata()
                        .withTemakode(temakode)
                        .withBaksystem(Baksystem.HENVENDELSE)
                );
        Stream<DokumentMetadata> joarkDokument = joarkTemakoder
                .stream()
                .map(temakode -> new DokumentMetadata()
                        .withTemakode(temakode)
                        .withBaksystem(Baksystem.JOARK)
                );

        return Stream.concat(henvendelseDokument, joarkDokument).collect(Collectors.toList());
    }

    private static Map<String, List<Behandlingskjede>> lagBehandlingskjeder(String ...temakoder) {
        return Stream.of(temakoder)
                .map(temakode -> new Pair<String, List<Behandlingskjede>>(temakode, emptyList()))
                .collect(Collectors.toMap(Pair::component1, Pair::component2));
    }
}
