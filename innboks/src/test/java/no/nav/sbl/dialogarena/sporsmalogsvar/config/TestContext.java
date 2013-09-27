package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.HenvendelseService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;


public class TestContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public HenvendelseService meldingService() {
        return new HenvendelseService.Mock();
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return new HenvendelsePortType() {
            @Override
            public boolean ping() {
                return true;
            }

            @Override
            public List<WSHenvendelse> hentHenvendelseListe(@WebParam(name = "fodselsnummer", targetNamespace = "") String s, @WebParam(name = "henvendelseType", targetNamespace = "") List<String> strings) {
                List<WSHenvendelse> liste = new ArrayList<>();
                liste.add(new WSHenvendelse().withHenvendelseType("SPORSMAL"));
                return liste;
            }

            @Override
            public void merkMeldingSomLest(@WebParam(name = "behandlingsId", targetNamespace = "") String behandlingsId) {
            }
        };
    }

}

