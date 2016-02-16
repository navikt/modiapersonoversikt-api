package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering;
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
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling.BehandlingsType.KVITTERING;
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
        List<Record<Kvittering>> kvitteringer = asList(new Record<Kvittering>()
                        .with(BEHANDLING_DATO, henvendelseTid)
                        .with(BEHANDLING_STATUS, GenerellBehandling.BehandlingsStatus.AVSLUTTET)
                        .with(GenerellBehandling.BEHANDLINGS_ID, henvendelsesId)
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

        Record<? extends GenerellBehandling> kvittering = fletter.flettDataFraBaksystemer(sak, kvitteringer).get(0);

        assertThat(kvittering.get(BEHANDLING_DATO), equalTo(henvendelseTid));
        assertThat(kvittering.get(GenerellBehandling.BEHANDLING_STATUS), equalTo(AVSLUTTET));
    }

    @Test
    public void behandlingsStatus_skalHentesFraHenvendelse_naarSakOgBehandlingHarIkkeSynkroniserteData() {
        DateTime henvendelseTid = new DateTime();
        DateTime sakOgBehandlingTid = new DateTime().minusDays(1);
        String henvendelsesId = "henvendelsesId";
        List<Record<Kvittering>> kvitteringer = asList(new Record<Kvittering>()
                        .with(BEHANDLING_DATO, henvendelseTid)
                        .with(BEHANDLING_STATUS, GenerellBehandling.BehandlingsStatus.AVSLUTTET)
                        .with(GenerellBehandling.BEHANDLINGS_ID, henvendelsesId)
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

        Record<? extends GenerellBehandling> kvittering = fletter.flettDataFraBaksystemer(sak, kvitteringer).get(0);

        assertThat(kvittering.get(BEHANDLING_DATO), equalTo(henvendelseTid));
        assertThat(kvittering.get(GenerellBehandling.BEHANDLING_STATUS), equalTo(AVSLUTTET));
    }

    @Test
    public void fireBehandlingsKjederMedToKvitteringskoblinger_skalFlette_toKvitteringerOgToBehandlinger() {
        WSSak sak = opprettSak();
        List<Record<Kvittering>> kvitteringer = opprettKvitteringer();

        List<Record<? extends GenerellBehandling>> behandlinger = fletter.flettDataFraBaksystemer(sak, kvitteringer);

        assertThat(finnAntallKvitteringer(behandlinger), equalTo(2));
        assertThat(behandlinger.size(), equalTo(4));
    }

    private List<Record<Kvittering>> opprettKvitteringer() {
        return asList(
                new Record<Kvittering>()
                        .with(GenerellBehandling.BEHANDLINGS_ID, KVITTERINGSID_1)
                        .with(BEHANDLINGKVITTERING, KVITTERING)
                        .with(BEHANDLING_STATUS, AVSLUTTET),
                new Record<Kvittering>()
                        .with(GenerellBehandling.BEHANDLINGS_ID, KVITTERINGSID_2)
                        .with(BEHANDLINGKVITTERING, KVITTERING)
                        .with(BEHANDLING_STATUS, AVSLUTTET),
                new Record<Kvittering>()
                        .with(GenerellBehandling.BEHANDLINGS_ID, "ikke-matchende-id-henvendelse")
                        .with(BEHANDLINGKVITTERING, KVITTERING)
                        .with(BEHANDLING_STATUS, AVSLUTTET)
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

    private int finnAntallKvitteringer(List<Record<? extends GenerellBehandling>> behandlinger) {
        int i = 0;
        for (Record<? extends GenerellBehandling> behandling : behandlinger) {
            if (behandling.get(BEHANDLINGKVITTERING).equals(KVITTERING)) {
                i++;
            }
        }
        return i;
    }

}
