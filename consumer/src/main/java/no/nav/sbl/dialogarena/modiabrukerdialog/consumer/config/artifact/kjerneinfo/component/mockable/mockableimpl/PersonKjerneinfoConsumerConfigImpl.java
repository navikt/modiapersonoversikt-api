package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;

public class PersonKjerneinfoConsumerConfigImpl {

    private final SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    private PersonV3 personPortType;
    private KjerneinfoMapper kjerneinfoMapperBean;
    private EnforcementPoint kjerneinfoPep;
    private OrganisasjonEnhetV2Service organisasjonEnhetV2Service;

    public PersonKjerneinfoConsumerConfigImpl(PersonV3 personPortType,
                                              KjerneinfoMapper kjerneinfoMapperBean, EnforcementPoint kjerneinfoPep,
                                              final OrganisasjonEnhetV2Service organisasjonEnhetV2Service,
                                              SaksbehandlerInnstillingerService saksbehandlerInnstillingerService) {
        this.personPortType = personPortType;
        this.kjerneinfoMapperBean = kjerneinfoMapperBean;
        this.kjerneinfoPep = kjerneinfoPep;
        this.organisasjonEnhetV2Service = organisasjonEnhetV2Service;
        this.saksbehandlerInnstillingerService = saksbehandlerInnstillingerService;
    }

    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return new DefaultPersonKjerneinfoService(personPortType, kjerneinfoMapperBean,
                kjerneinfoPep, organisasjonEnhetV2Service, saksbehandlerInnstillingerService);
    }

}
