package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Import({
        JaxWsFeatures.Mock.class
})
public class TestContext {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public MeldingService meldingService() {
        return new MeldingService.Default(henvendelsePortType());
    }

    @Bean
    public HenvendelsePortType henvendelsePortType() {
        return new HenvendelsePortType() {
            @Override
            public boolean ping() {
                return true;
            }

            @Override
            public List<WSHenvendelse> hentHenvendelseListe(@WebParam(name = "fodselsnummer", targetNamespace = "") String s) {
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

