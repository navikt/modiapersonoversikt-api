package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSAvslutningsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingsstatuser;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSSakstemaer;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.sbl.dialogarena.saksoversikt.service.mock.MockCreationUtil.createWSBehandlingskjede;
import static no.nav.sbl.dialogarena.saksoversikt.service.mock.MockCreationUtil.createWSSak;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus.FERDIG_BEHANDLET;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsType.KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DataFletterTest {

    private static final String KVITTERINGSID_1 = "kvitteringsid1";
    private static final String KVITTERINGSID_2 = "kvitteringsid2";
    private DataFletter fletter = new DataFletter();

    @Test
    public void behandlingsStatus_skalHentesFraHenvendelse_naarSakOgBehandlingHarInkonsistentSluttData() {
        DateTime henvendelseTid = new DateTime();
        DateTime sakOgBehandlingTid = new DateTime().minusDays(1);
        String henvendelsesId = "henvendelsesId";
        List<Behandling> kvitteringer = asList(new Behandling()
                .withBehandlingsDato(henvendelseTid)
                .withBehandlingStatus(FERDIG_BEHANDLET)
                .withBehandlingsId(henvendelsesId)
        );
        WSSak sak = new WSSak().withBehandlingskjede(new WSBehandlingskjede()
                .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue(BEHANDLINGSTATUS_AVSLUTTET))
                .withSisteBehandlingstype(new WSBehandlingstyper().withValue(SEND_SOKNAD_KVITTERINGSTYPE))
                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                .withBehandlingsListeRef(henvendelsesId)
                .withSisteBehandlingREF(henvendelsesId)
                .withStart(sakOgBehandlingTid)
                .withSlutt(null)
        );

        Behandling kvittering = fletter.flettDataFraBaksystemer(sak, kvitteringer).get(0);

        assertThat(kvittering.getBehandlingDato(), equalTo(henvendelseTid));
        assertThat(kvittering.getBehandlingsStatus(), equalTo(FERDIG_BEHANDLET));
    }

    @Test
    public void behandlingsStatus_skalHentesFraHenvendelse_naarSakOgBehandlingHarIkkeSynkroniserteData() {
        DateTime henvendelseTid = new DateTime();
        DateTime sakOgBehandlingTid = new DateTime().minusDays(1);
        String henvendelsesId = "henvendelsesId";
        List<Behandling> kvitteringer = asList(new Behandling()
                        .withBehandlingsDato(henvendelseTid)
                        .withBehandlingStatus(FERDIG_BEHANDLET)
                        .withBehandlingsId(henvendelsesId)
        );
        WSSak sak = new WSSak().withBehandlingskjede(new WSBehandlingskjede()
                        .withSisteBehandlingAvslutningsstatus(new WSAvslutningsstatuser().withValue(Filter.OPPRETTET))
                        .withSisteBehandlingstype(new WSBehandlingstyper().withValue(SEND_SOKNAD_KVITTERINGSTYPE))
                        .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.OPPRETTET))
                        .withBehandlingsListeRef(henvendelsesId)
                        .withSisteBehandlingREF(henvendelsesId)
                        .withStart(sakOgBehandlingTid)
                        .withSlutt(null)
        );

        Behandling kvittering = fletter.flettDataFraBaksystemer(sak, kvitteringer).get(0);

        assertThat(kvittering.getBehandlingDato(), equalTo(henvendelseTid));
        assertThat(kvittering.getBehandlingsStatus(), equalTo(FERDIG_BEHANDLET));
    }

    @Test
    public void fireBehandlingsKjederMedToKvitteringskoblinger_skalFlette_toKvitteringerOgToBehandlinger() {
        WSSak sak = opprettSak();
        List<Behandling> kvitteringer = opprettKvitteringer();

        List<Behandling> behandlinger = fletter.flettDataFraBaksystemer(sak, kvitteringer);

        assertThat(finnAntallKvitteringer(behandlinger), equalTo(2));
        assertThat(behandlinger.size(), equalTo(4));
    }

    private List<Behandling> opprettKvitteringer() {
        return asList(
                new Behandling()
                        .withBehandlingsId(KVITTERINGSID_1)
                        .withBehandlingKvittering(KVITTERING)
                        .withBehandlingStatus(FERDIG_BEHANDLET),
                new Behandling()
                        .withBehandlingsId(KVITTERINGSID_2)
                        .withBehandlingKvittering(KVITTERING)
                        .withBehandlingStatus(FERDIG_BEHANDLET),
                new Behandling()
                        .withBehandlingsId("ikke-matchende-id-henvendelse")
                        .withBehandlingKvittering(KVITTERING)
                        .withBehandlingStatus(FERDIG_BEHANDLET)
        );
    }

    private WSSak opprettSak() {
        return createWSSak()
                .withSakstema(new WSSakstemaer().withValue("DAG"))
                .withBehandlingskjede(
                        createWSBehandlingskjede()
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue(DOKUMENTINNSENDING_KVITTERINGSTYPE))
                                .withSisteBehandlingREF(KVITTERINGSID_1),
                        createWSBehandlingskjede()
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue(SEND_SOKNAD_KVITTERINGSTYPE))
                                .withSisteBehandlingREF(KVITTERINGSID_2),
                        createWSBehandlingskjede()
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ingenkvittering")),
                        createWSBehandlingskjede()
                                .withSisteBehandlingsstatus(new WSBehandlingsstatuser().withValue(Filter.AVSLUTTET))
                                .withSisteBehandlingstype(new WSBehandlingstyper().withValue("ingenkvittering"))
                );
    }

    private int finnAntallKvitteringer(List<Behandling> behandlinger) {
        int i = 0;
        for (Behandling behandling : behandlinger) {
            if (behandling.getBehandlingkvittering().equals(KVITTERING)) {
                i++;
            }
        }
        return i;
    }

}
