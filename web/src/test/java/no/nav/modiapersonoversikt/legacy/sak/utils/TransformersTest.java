package no.nav.modiapersonoversikt.legacy.sak.utils;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import no.nav.modiapersonoversikt.legacy.sak.BehandlingskjedeBuilder;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.FilterUtils;
import no.nav.modiapersonoversikt.legacy.sak.transformers.Transformers;
import org.hamcrest.Matchers;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.HenvendelseType.DOKUMENTINNSENDING;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class TransformersTest {

    @Test
    public void skalTransformereEnSoknadTilKvittering() {
        DokumentFraHenvendelse innsendtHovedskjema = new DokumentFraHenvendelse().withErHovedskjema(true).withInnsendingsvalg(DokumentFraHenvendelse.Innsendingsvalg.LASTET_OPP).withKodeverkRef("kodeverk1");
        DokumentFraHenvendelse innsendtVedlegg = new DokumentFraHenvendelse().withErHovedskjema(false).withInnsendingsvalg(DokumentFraHenvendelse.Innsendingsvalg.INNSENDT).withKodeverkRef("kodeverk2");
        DokumentFraHenvendelse sendSenereVedlegg = new DokumentFraHenvendelse().withErHovedskjema(false).withInnsendingsvalg(DokumentFraHenvendelse.Innsendingsvalg.SEND_SENERE).withKodeverkRef("kodeverk3");

        Soknad soknad = new Soknad()
                .withBehandlingsId("123-behandlingsid")
                .withBehandlingskjedeId("123-behandlingskjedeid")
                .withHenvendelseType(DOKUMENTINNSENDING)
                .withInnsendtDato(DateMidnight.parse("2014-01-01").toDateTime())
                .withSkjemanummerRef("kvittering-kodeverk-ref-mock")
                .withEttersending(false)
                .withDokumenter(asList(innsendtHovedskjema, innsendtVedlegg, sendSenereVedlegg));

        Behandling kvittering = Transformers.SOKNAD_TIL_KVITTERING.apply(soknad);

        assertThat(kvittering.getBehandlingsId(), is(soknad.getBehandlingsId()));
        assertThat(kvittering.getBehandlingskjedeId(), is(soknad.getBehandlingskjedeId()));
        assertThat(kvittering.getKvitteringstype(), is(soknad.getType()));
        assertThat(kvittering.getBehandlingDato(), is(soknad.getInnsendtDato()));
        assertThat(kvittering.getSkjemanummerRef(), is(soknad.getSkjemanummerRef()));
        assertThat(kvittering.getBehandlingsStatus(), Matchers.is(BehandlingsStatus.FERDIG_BEHANDLET));
        assertThat(kvittering.getBehandlingkvittering(), Matchers.is(BehandlingsType.KVITTERING));
        assertThat(kvittering.getEttersending(), is(soknad.getEttersending()));
        assertThat(kvittering.getInnsendteDokumenter(), contains(innsendtHovedskjema, innsendtVedlegg));
        assertThat(kvittering.getManglendeDokumenter(), contains(sendSenereVedlegg));
    }

    @Test
    public void skalLeggeTilHovedskjemaPaaKvitteringHvisHovedskjemaErInnsendt() {
        DokumentFraHenvendelse innsendtHovedskjema = new DokumentFraHenvendelse().withErHovedskjema(true).withInnsendingsvalg(DokumentFraHenvendelse.Innsendingsvalg.LASTET_OPP).withKodeverkRef("kodeverk1");

        Soknad soknad = new Soknad()
                .withDokumenter(asList(innsendtHovedskjema));

        Behandling kvittering = Transformers.SOKNAD_TIL_KVITTERING.apply(soknad);

        assertThat(kvittering.getInnsendteDokumenter().size(), is(1));
        assertThat(kvittering.getInnsendteDokumenter(), contains(innsendtHovedskjema));
    }

    @Test
    public void skalIkkeLeggeTilHovedskjemaPaaKvitteringHvisHovedskjemaIkkeErInnsendt() {
        DokumentFraHenvendelse innsendtHovedskjema = new DokumentFraHenvendelse().withErHovedskjema(true).withInnsendingsvalg(DokumentFraHenvendelse.Innsendingsvalg.SEND_SENERE).withKodeverkRef("kodeverk1");

        Soknad soknad = new Soknad()
                .withDokumenter(asList(innsendtHovedskjema));

        Behandling kvittering = Transformers.SOKNAD_TIL_KVITTERING.apply(soknad);

        assertThat(kvittering.getInnsendteDokumenter().size(), is(0));
        assertThat(kvittering.getManglendeDokumenter().size(), is(0));
    }

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
                .map(Transformers.TIL_BEHANDLING)
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
                .map(Transformers.TIL_BEHANDLING)
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
                .map(Transformers.TIL_BEHANDLING)
                .collect(Collectors.toList());

        assertThat(collect.size(), equalTo(1));
        assertThat(collect.get(0).behandlingsStatus, equalTo(BehandlingsStatus.UNDER_BEHANDLING));
        assertThat(collect.get(0).behandlingsId, equalTo("hovedskjemakodeverkref"));
    }

    @Test(expected = ApplicationException.class)
    public void ugyldigBehandlingsstatusKasterException() {
            Stream.of(
                    BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus("IKKE_EN_STATUS")
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                .build())
                .map(Transformers.TIL_BEHANDLING)
                .collect(Collectors.toList());

    }



}
