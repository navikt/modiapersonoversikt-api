package no.nav.sbl.dialogarena.sporsmalogsvar.config;

import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareService;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareServiceImpl;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.BesvareSporsmalDetaljer;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Sporsmal;
import no.nav.sbl.dialogarena.sporsmalogsvar.service.Svar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static java.util.Collections.emptyList;

@Configuration
public class BesvareServiceConfig {

    @Profile({"default", "besvarehenvendelseDefault"})
    @Configuration
    public static class Default {
        @Bean
        BesvareService besvareService() {
            return new BesvareServiceImpl();
        }
    }

    @Profile({"test", "besvarehenvendelseTest"})
    @Configuration
    public static class Test {
        @Bean
        BesvareService besvareService() {
            return new BesvareService() {

                @Override
                public void besvareSporsmal(Svar svar) {
                    return;
                }

                @Override
                public BesvareSporsmalDetaljer hentDetaljer(String fnr, String oppgaveId) {
                    Svar svar = new Svar("1");
                    Sporsmal sporsmal = new Sporsmal();
                    sporsmal.fritekst = "Spørsmålstekst";
                    sporsmal.sendtDato = "28082013";

                    BesvareSporsmalDetaljer detaljer = new BesvareSporsmalDetaljer();
                    detaljer.sporsmal = sporsmal;
                    detaljer.svar = svar;
                    detaljer.tema = "Tema";
                    detaljer.tildligereDialog = emptyList();
                    return detaljer;
                }
            };
        }
    }
}
