package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.OppgavebehandlingPortType;
import no.nav.tjeneste.domene.brukerdialog.oppgavebehandling.v1.informasjon.WSPlukkOppgaveResultat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OppgavebehandlingPortTypeMock {

    public static final String FODESELSNR = "10108000398";
    public static final String OPPGAVEID = "1";

    @Bean
    public OppgavebehandlingPortType oppgavebehandlingPortType() {
        return new OppgavebehandlingPortType() {
            @Override
            public boolean ping() {
                return true;
            }

            @Override
            public WSPlukkOppgaveResultat plukkOppgave(String tema) {
                return new WSPlukkOppgaveResultat().withFodselsnummer(FODESELSNR).withOppgaveId(OPPGAVEID);
            }

            @Override
            public void leggTilbakeOppgave(String oppgaveId, String aarsak) {
                //No action required
            }

        };
    }

}
