package no.nav.sbl.dialogarena.sporsmalogsvar.context;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingServiceImpl;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SporsmalOgSvarContext {

    @Bean
    public HenvendelseBehandlingService henvendelseBehandlingService(
            HenvendelsePortType henvendelsePortType,
            BehandleHenvendelsePortType behandleHenvendelsePortType,
            PersonKjerneinfoServiceBi kjerneinfo,
            Tilgangskontroll tilgangskontroll,
            StandardKodeverk standardKodeverk,
            ContentRetriever propertyResolver,
            LDAPService ldapService,
            ArbeidsfordelingV1Service arbeidsfordelingService
    ) {
        return new HenvendelseBehandlingServiceImpl(
                henvendelsePortType,
                behandleHenvendelsePortType,
                kjerneinfo,
                tilgangskontroll,
                standardKodeverk,
                propertyResolver,
                ldapService,
                arbeidsfordelingService
        );
    }
}
