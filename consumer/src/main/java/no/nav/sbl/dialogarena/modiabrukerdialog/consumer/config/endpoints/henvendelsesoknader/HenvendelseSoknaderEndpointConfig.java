package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoints.henvendelsesoknader;

import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints.HenvendelseSoknaderPortTypeMock;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebParam;
import java.util.List;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.MockUtil.mockErTillattOgSlaattPaaForKey;

@Configuration
public class HenvendelseSoknaderEndpointConfig {

    public static final String HENVENDELSESOKNADER_KEY = "start.henvendelsesoknader.withmock";

    @Bean
    public HenvendelseSoknaderPortType henvendelseSoknaderPortType() {
        final HenvendelseSoknaderPortType mock = new HenvendelseSoknaderPortTypeMock().getHenvendelseSoknaderPortTypeMock();
        final HenvendelseSoknaderPortType prod = createHenvendelsePortType();
        return new HenvendelseSoknaderPortType() {
            @Override
            public List<WSSoknad> hentSoknadListe(@WebParam(name = "fodselsnummer", targetNamespace = "") String fodselsnummer) {
                if (mockErTillattOgSlaattPaaForKey(HENVENDELSESOKNADER_KEY)) {
                    return mock.hentSoknadListe(fodselsnummer);
                }
                return prod.hentSoknadListe(fodselsnummer);
            }

            @Override
            public void ping() {
                if (mockErTillattOgSlaattPaaForKey(HENVENDELSESOKNADER_KEY)) {
                    mock.ping();
                } else {
                    prod.ping();
                }
            }
        };
    }

    private HenvendelseSoknaderPortType createHenvendelsePortType() {
        return new CXFClient<>(HenvendelseSoknaderPortType.class)
                .address(System.getProperty("henvendelse.soknader.url"))
                .wsdl("classpath:no/nav/tjeneste/domene/brukerdialog/henvendelsesoknader/v1/Soknader.wsdl")
                .build();
    }

}
