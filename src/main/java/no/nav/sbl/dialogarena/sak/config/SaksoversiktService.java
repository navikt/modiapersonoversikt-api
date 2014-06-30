package no.nav.sbl.dialogarena.sak.config;

import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;

import javax.inject.Inject;



public class SaksoversiktService {

    @Inject
    private AktoerPortType fodselnummerAktorService;

    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;

    @Inject
    private HenvendelseSoknaderPortType henvendelseSoknaderPortType;

}
