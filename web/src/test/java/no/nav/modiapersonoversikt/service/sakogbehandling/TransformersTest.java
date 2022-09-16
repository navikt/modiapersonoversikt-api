package no.nav.modiapersonoversikt.service.sakogbehandling;

import no.nav.modiapersonoversikt.service.sakstema.domain.Behandling;
import no.nav.modiapersonoversikt.service.sakstema.domain.BehandlingsStatus;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransformersTest {
    @Test
    public void mapperBehandlingskjedeTilBehandling() {
        List<Behandling> collect = Stream.of(
                BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                .build()
        )
                .map(Transformers::tilBehandling)
                .collect(Collectors.toList());

        assertThat(collect.size(), equalTo(1));
        assertThat(collect.get(0).behandlingsId, equalTo("hovedskjemakodeverkref"));
        assertThat(collect.get(0).behandlingsStatus, equalTo(BehandlingsStatus.FERDIG_BEHANDLET));
    }

    @Test
    public void mapperAvbruttBehandlingskjedeTilBehandling() {
        List<Behandling> collect = Stream.of(
                BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus(FilterUtils.AVBRUTT)
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                        .build())
                .map(Transformers::tilBehandling)
                .collect(Collectors.toList());

        assertThat(collect.size(), equalTo(1));
        assertThat(collect.get(0).behandlingsStatus, equalTo(BehandlingsStatus.AVBRUTT));
    }

    @Test
    public void mapperOpprettetBehandlingskjedeTilBehandling() {
        List<Behandling> collect = Stream.of(
                BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus(FilterUtils.OPPRETTET)
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                        .build())
                .map(Transformers::tilBehandling)
                .collect(Collectors.toList());

        assertThat(collect.size(), equalTo(1));
        assertThat(collect.get(0).behandlingsStatus, equalTo(BehandlingsStatus.UNDER_BEHANDLING));
        assertThat(collect.get(0).behandlingsId, equalTo("hovedskjemakodeverkref"));
    }

    @Test(expected = RuntimeException.class)
    public void ugyldigBehandlingsstatusKasterException() {
        Transformers.tilBehandling(
                BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus("IKKE_EN_STATUS")
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                        .build()
        );
    }



}
