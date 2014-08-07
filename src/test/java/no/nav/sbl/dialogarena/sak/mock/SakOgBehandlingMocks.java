package no.nav.sbl.dialogarena.sak.mock;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingskjedetyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingsstegtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstid;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstidtyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSBehandlingstyper;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.WSSakstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;

import java.math.BigInteger;

import static org.joda.time.DateTime.now;

public class SakOgBehandlingMocks {

    public static final String TEMA = "DAG";

    public static WSSak createWSSak() {
        return new WSSak()
                .withSaksId("saksId-mock")
                .withSakstema(new WSSakstemaer().withValue(TEMA).withKodeverksRef("kodeverk-ref-mock"))
                .withBehandlingskjede(createWSBehandlingskjede())
                .withOpprettet(now());
    }

    public static WSBehandlingskjede createWSBehandlingskjede() {
        return new WSBehandlingskjede()
                .withBehandlingskjedeId("behandlingskjedeid-mock")
                .withBehandlingskjedetype(new WSBehandlingskjedetyper().withKodeverksRef("kodeverk-ref-mock"))
                .withStart(now())
                .withNormertBehandlingstid(
                        new WSBehandlingstid()
                                .withTid(new BigInteger("1"))
                                .withType(new WSBehandlingstidtyper().withValue("dager"))
                )
                .withKjedensNAVfrist(now().plusDays(10))
                .withSisteBehandlingREF("siste-behandling-ref-mock")
                .withSisteBehandlingstype(new WSBehandlingstyper().withKodeverksRef("behandlingstype-ref-mock"))
                .withSisteBehandlingsstegREF("siste-behandling-steg-ref-mock")
                .withSisteBehandlingsstegtype(new WSBehandlingsstegtyper().withKodeverksRef("behandlingssteg-ref-mock"));
    }

}
