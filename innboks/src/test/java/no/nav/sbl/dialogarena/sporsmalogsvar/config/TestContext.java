package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.SporsmalOgSvarPortType;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSMeldingstype;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmal;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSporsmalOgSvar;
import no.nav.tjeneste.domene.brukerdialog.sporsmalogsvar.v1.informasjon.WSSvar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        return new MeldingService.Default(henvendelsePortType(), sporsmalOgSvarPortType());
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
                liste.add(new no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMelding().withType(no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSMeldingstype.SPORSMAL));
                return liste;
            }

            @Override
            public void merkMeldingSomLest(@WebParam(name = "behandlingsId", targetNamespace = "") String behandlingsId) {
            }
        };
    }

    @Bean
    public SporsmalOgSvarPortType sporsmalOgSvarPortType() {
        return new SporsmalOgSvarPortType() {
            @Override
            public WSSporsmalOgSvar plukkMeldingForBesvaring(@WebParam(name = "aktorId", targetNamespace = "") String s) {
                return new WSSporsmalOgSvar();
            }

            @Override
            public String opprettSporsmal(@WebParam(name = "sporsmal", targetNamespace = "") WSSporsmal wsSporsmal, @WebParam(name = "aktorId", targetNamespace = "") String s) {
                return "String";
            }

            @Override
            public void besvarSporsmal(@WebParam(name = "svar", targetNamespace = "") WSSvar wsSvar) {
            }

            @Override
            public boolean ping() {
                return true;
            }

            @Override
            public WSMelding hentMelding(@WebParam(name = "behandlingsId", targetNamespace = "") String s) {
                return new WSMelding();
            }

            @Override
            public List<WSMelding> hentMeldingListe(@WebParam(name = "aktorId", targetNamespace = "") String s) {
                return Arrays.asList(new WSMelding().withType(WSMeldingstype.SPORSMAL), new WSMelding().withType(WSMeldingstype.SVAR));
            }
        };
    }
}

