package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.context;

import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.Tilgangskontroll;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingServiceImpl;
import no.nav.modiapersonoversikt.rest.persondata.PersondataService;
import no.nav.modiapersonoversikt.service.arbeidsfordeling.ArbeidsfordelingService;
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk;
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
            PersondataService persondataService,
            Tilgangskontroll tilgangskontroll,
            EnhetligKodeverk.Service kodeverk,
            ContentRetriever propertyResolver,
            LDAPService ldapService,
            ArbeidsfordelingService arbeidsfordelingService
    ) {
        return new HenvendelseBehandlingServiceImpl(
                henvendelsePortType,
                behandleHenvendelsePortType,
                persondataService,
                tilgangskontroll,
                kodeverk,
                propertyResolver,
                ldapService,
                arbeidsfordelingService
        );
    }
}
