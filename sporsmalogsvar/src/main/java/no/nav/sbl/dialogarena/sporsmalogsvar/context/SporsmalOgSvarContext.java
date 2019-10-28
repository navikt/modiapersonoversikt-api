package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.modig.common.SporingsLogger;
import no.nav.modig.common.SporingsLoggerFactory;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakServiceImpl;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSokImpl;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static com.google.common.base.Charsets.UTF_8;

@Configuration
public class SporsmalOgSvarContext {

    @Bean
    public GsakService gsakService() {
        return new GsakServiceImpl();
    }

    @Bean
    public HenvendelseBehandlingService henvendelseBehandlingService() {
        return new HenvendelseBehandlingServiceImpl();
    }

    @Bean
    public MeldingerSok meldingIndekserer() {
        return new MeldingerSokImpl();
    }

    @Bean
    public SporingsLogger sporingsLogger() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(SporsmalOgSvarContext.class.getResourceAsStream("sporingslogconfig.txt"), UTF_8));
        return SporingsLoggerFactory.sporingsLogger(br);
    }
}
