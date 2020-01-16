package no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.BehandlingskjedeBuilder;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.FilterUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers.Transformers;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Behandlingsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Behandlingstyper;
import org.hamcrest.Matchers;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.HenvendelseType.DOKUMENTINNSENDING;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

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
    public void transformTilSoknadOk() {
        DateTime opprettetdato = new DateTime();
        DateTime innsendtdato = new DateTime();
        DateTime sistendretdato = new DateTime();
        WSSoknad wsSoknad = new WSSoknad()
                .withBehandlingsId("behandlingid")
                .withBehandlingsKjedeId("behandlingkjedeid")
                .withJournalpostId("journalpostid")
                .withHenvendelseStatus(Soknad.HenvendelseStatus.UNDER_ARBEID.name())
                .withOpprettetDato(opprettetdato)
                .withInnsendtDato(innsendtdato)
                .withSistEndretDato(sistendretdato)
                .withHovedskjemaKodeverkId("hovedskjemakodeverkref")
                .withEttersending(false)
                .withHenvendelseType(DOKUMENTINNSENDING.name())
                .withDokumentforventninger(new WSSoknad.Dokumentforventninger().withDokumentforventning(asList(
                        new WSDokumentforventning()
                                .withKodeverkId("dokKodeverkRef1")
                                .withTilleggsTittel("tilleggstittel1")
                                .withUuid("uuid1")
                                .withArkivreferanse("arkivreferanse1")
                                .withInnsendingsvalg(DokumentFraHenvendelse.Innsendingsvalg.INNSENDT.name()),
                        new WSDokumentforventning()
                                .withKodeverkId("hovedskjemakodeverkref")
                                .withTilleggsTittel("tilleggstittel2")
                                .withUuid("uuid2")
                                .withArkivreferanse("arkivreferanse2")
                                .withInnsendingsvalg(DokumentFraHenvendelse.Innsendingsvalg.INNSENDT.name())
                )));

        Soknad soknad = Transformers.transformTilSoknad(wsSoknad);

        assertThat(soknad.getBehandlingsId(), is("behandlingid"));
        assertThat(soknad.getBehandlingskjedeId(), is("behandlingkjedeid"));
        assertThat(soknad.getJournalpostId(), is("journalpostid"));
        assertThat(soknad.getStatus(), Matchers.is(Soknad.HenvendelseStatus.UNDER_ARBEID));
        assertThat(soknad.getOpprettetDato(), is(opprettetdato));
        assertThat(soknad.getInnsendtDato(), is(innsendtdato));
        assertThat(soknad.getSistendretDato(), is(sistendretdato));
        assertThat(soknad.getSkjemanummerRef(), is("hovedskjemakodeverkref"));
        assertThat(soknad.getEttersending(), is(false));
        assertThat(soknad.getType(), is(DOKUMENTINNSENDING));
        assertThat(soknad.getDokumenter().size(), is(2));
        assertThat(soknad.getDokumenter().get(0).getKodeverkRef(), is("dokKodeverkRef1"));
        assertThat(soknad.getDokumenter().get(0).erHovedskjema(), is(false));
        assertThat(soknad.getDokumenter().get(1).getKodeverkRef(), is("hovedskjemakodeverkref"));
        assertThat(soknad.getDokumenter().get(1).erHovedskjema(), is(true));
    }

    @Test
    public void brukerBehandlingsIdHvisBehandlingskjedeIdErNull() {
        WSSoknad wsSoknad = new WSSoknad()
                .withBehandlingsId("behandlingid")
                .withBehandlingsKjedeId(null)
                .withHenvendelseStatus(Soknad.HenvendelseStatus.UNDER_ARBEID.name())
                .withHenvendelseType(DOKUMENTINNSENDING.name());

        Soknad soknad = Transformers.transformTilSoknad(wsSoknad);

        assertThat(soknad.getBehandlingsId(), is("behandlingid"));
        assertThat(soknad.getBehandlingskjedeId(), is("behandlingid"));
    }

    @Test
    public void mapperBehandlingskjedeTilBehandling() {
        List<Behandling> collect = asList(
                BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus(FilterUtils.AVSLUTTET)
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                .build()
        )
                .stream()
                .map(Transformers.TIL_BEHANDLING)
                .collect(Collectors.toList());

        assertThat(collect.size(), equalTo(1));
        assertThat(collect.get(0).behandlingsId, equalTo("hovedskjemakodeverkref"));
        assertThat(collect.get(0).behandlingsStatus, equalTo(BehandlingsStatus.FERDIG_BEHANDLET));
    }

    @Test
    public void mapperAvbruttBehandlingskjedeTilBehandling() {
        List<Behandling> collect = asList(
                BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus(FilterUtils.AVBRUTT)
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                        .build())
                .stream()
                .map(Transformers.TIL_BEHANDLING)
                .collect(Collectors.toList());

        assertThat(collect.size(), equalTo(1));
        assertThat(collect.get(0).behandlingsStatus, equalTo(BehandlingsStatus.AVBRUTT));
    }

    @Test
    public void mapperOpprettetBehandlingskjedeTilBehandling() {
        List<Behandling> collect = asList(
                BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus(FilterUtils.OPPRETTET)
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                        .build())
                .stream()
                .map(Transformers.TIL_BEHANDLING)
                .collect(Collectors.toList());

        assertThat(collect.size(), equalTo(1));
        assertThat(collect.get(0).behandlingsStatus, equalTo(BehandlingsStatus.UNDER_BEHANDLING));
        assertThat(collect.get(0).behandlingsId, equalTo("hovedskjemakodeverkref"));
    }

    @Test(expected = ApplicationException.class)
    public void ugyldigBehandlingsstatusKasterException() {
            asList(
                    BehandlingskjedeBuilder.create()
                        .withSisteBehandlingstype("type")
                        .withSisteBehandlingsstatus("IKKE_EN_STATUS")
                        .withSlutt(new DateTime())
                        .withStart(new DateTime().minusDays(1))
                        .withSisteBehandlingREF("hovedskjemakodeverkref")
                .build())
                .stream()
                .map(Transformers.TIL_BEHANDLING)
                .collect(Collectors.toList());

    }



}
