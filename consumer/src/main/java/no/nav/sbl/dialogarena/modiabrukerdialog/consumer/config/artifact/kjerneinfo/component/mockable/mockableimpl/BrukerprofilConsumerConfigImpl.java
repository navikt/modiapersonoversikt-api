package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.brukerprofil.consumer.BrukerprofilServiceBi;
import no.nav.brukerprofil.consumer.support.DefaultBrukerprofilService;
import no.nav.brukerprofil.consumer.support.mapping.BrukerprofilMapper;
import no.nav.tjeneste.virksomhet.brukerprofil.v1.BrukerprofilPortType;

public class BrukerprofilConsumerConfigImpl {

    private BrukerprofilPortType brukerprofilPortType;

    public BrukerprofilConsumerConfigImpl(BrukerprofilPortType brukerprofilPortType) {
        this.brukerprofilPortType = brukerprofilPortType;
    }

    public BrukerprofilServiceBi brukerprofilServiceBi() {
        return new DefaultBrukerprofilService(brukerprofilPortType, new BrukerprofilMapper());
    }

}
