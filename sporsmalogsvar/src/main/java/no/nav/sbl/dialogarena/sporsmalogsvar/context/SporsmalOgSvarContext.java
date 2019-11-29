package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakServiceImpl;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSok;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingerSokImpl;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingServiceImpl;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
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
    public HenvendelseBehandlingService henvendelseBehandlingService(
            HenvendelsePortType henvendelsePortType,
            BehandleHenvendelsePortType behandleHenvendelsePortType,
            PersonKjerneinfoServiceBi kjerneinfo,
            Tilgangskontroll tilgangskontroll,
            StandardKodeverk standardKodeverk,
            @Named("propertyResolver") ContentRetriever propertyResolver,
            LDAPService ldapService
    ) {
        return new HenvendelseBehandlingServiceImpl(
                henvendelsePortType,
                behandleHenvendelsePortType,
                kjerneinfo,
                tilgangskontroll,
                standardKodeverk,
                propertyResolver,
                ldapService
        );
    }

    @Bean
    public MeldingerSok meldingIndekserer() {
        return new MeldingerSokImpl();
    }
}
