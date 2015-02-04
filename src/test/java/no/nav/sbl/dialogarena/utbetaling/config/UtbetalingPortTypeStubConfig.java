package no.nav.sbl.dialogarena.utbetaling.config;

import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.getWsUtbetalinger;

@Configuration
public class UtbetalingPortTypeStubConfig {

    private static final String FNR = "12345612345";


    @Bean
    public UtbetalingV1 utbetalingPortType() {
        return new UtbetalingV1() {
            @Override
            public void ping() {

            }

            @Override
            public WSHentUtbetalingsinformasjonResponse hentUtbetalingsinformasjon(WSHentUtbetalingsinformasjonRequest request) throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {
                String ident = request.getId().getIdent();
                if(ident == null) {
                    ident = FNR;
                }
                return new WSHentUtbetalingsinformasjonResponse().withUtbetalingListe(getWsUtbetalinger(ident, request.getPeriode().getFom(), request.getPeriode().getTom()));
            }
        };
    }

}
