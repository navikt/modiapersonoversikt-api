package no.nav.sbl.dialogarena.utbetaling.config;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.tjeneste.virksomhet.utbetaling.v1.HentUtbetalingsinformasjonPeriodeIkkeGyldig;
import no.nav.tjeneste.virksomhet.utbetaling.v1.UtbetalingV1;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonRequest;
import no.nav.tjeneste.virksomhet.utbetaling.v1.meldinger.WSHentUtbetalingsinformasjonResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;

import static java.lang.Boolean.valueOf;
import static java.lang.System.getProperty;
import static no.nav.sbl.dialogarena.utbetaling.domain.testdata.WSUtbetalingTestData.getWsUtbetalinger;

@Configuration
public class UtbetalingPortTypeTestConfig {

    private static final String AREMARK_FNR = "10108000398";

    @Bean
    public UtbetalingV1 utbetalingPortType() {
        if (valueOf(getProperty("utbetal.endpoint.mock", "true"))) {
            return getUtbetalingV1Stub();
        }
        return createUtbetalingPortType();
    }

    protected UtbetalingV1 getUtbetalingV1Stub() {
        return new UtbetalingV1() {
            @Override
            public void ping() {

            }

            @Override
            public WSHentUtbetalingsinformasjonResponse hentUtbetalingsinformasjon(WSHentUtbetalingsinformasjonRequest request) throws HentUtbetalingsinformasjonPeriodeIkkeGyldig {
                String ident = request.getId().getIdent();
                if (ident == null) {
                    ident = AREMARK_FNR;
                }
                return new WSHentUtbetalingsinformasjonResponse().withUtbetalingListe(getWsUtbetalinger(ident, request.getPeriode().getFom(), request.getPeriode().getTom()));
            }
        };
    }

    private UtbetalingV1 createUtbetalingPortType() {
        return new CXFClient<>(UtbetalingV1.class)
                .wsdl("classpath:utbetaling/no/nav/tjeneste/virksomhet/utbetaling/v1/Binding.wsdl")
                .serviceName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1"))
                .endpointName(new QName("http://nav.no/tjeneste/virksomhet/utbetaling/v1/Binding", "Utbetaling_v1Port"))
                .address(getProperty("utbetalingendpoint.url"))
                .build();
    }

}
