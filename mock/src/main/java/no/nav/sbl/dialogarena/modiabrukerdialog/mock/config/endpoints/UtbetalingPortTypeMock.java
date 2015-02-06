package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.WSUtbetalingTestData.getWsUtbetalinger;

@Configuration
public class UtbetalingPortTypeMock {

    @Bean
    public UtbetalingV1 utbetalingPortType() {
        return new UtbetalingV1() {
            @Override
            public void ping() {
            }

            @Override
            public WSHentUtbetalingsinformasjonResponse hentUtbetalingsinformasjon(WSHentUtbetalingsinformasjonRequest request) throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {
                return new WSHentUtbetalingsinformasjonResponse()
                        .withUtbetalingListe(getWsUtbetalinger(
                                request.getId().getIdent(),
                                request.getPeriode().getFom(),
                                request.getPeriode().getTom()));
            }
        };
    }

}
