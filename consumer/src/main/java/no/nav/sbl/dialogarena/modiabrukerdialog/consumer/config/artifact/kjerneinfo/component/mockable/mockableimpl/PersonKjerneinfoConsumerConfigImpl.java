package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService;
import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.kjerneinfo.consumer.fim.person.support.KjerneinfoMapper;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.organisasjonsEnhetV2.OrganisasjonEnhetV2Service;
import no.nav.tjeneste.virksomhet.person.v3.PersonV3;

public class PersonKjerneinfoConsumerConfigImpl {

    private PersonV3 personPortType;
    private KjerneinfoMapper kjerneinfoMapperBean;
    private EnforcementPoint kjerneinfoPep;
    private OrganisasjonEnhetV2Service organisasjonEnhetV2Service;

    public PersonKjerneinfoConsumerConfigImpl(PersonV3 personPortType,
                                              KjerneinfoMapper kjerneinfoMapperBean, EnforcementPoint kjerneinfoPep,
                                              final OrganisasjonEnhetV2Service organisasjonEnhetV2Service) {
        this.personPortType = personPortType;
        this.kjerneinfoMapperBean = kjerneinfoMapperBean;
        this.kjerneinfoPep = kjerneinfoPep;
        this.organisasjonEnhetV2Service = organisasjonEnhetV2Service;
    }

    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return new DefaultPersonKjerneinfoService(personPortType, kjerneinfoMapperBean, kjerneinfoPep,
                organisasjonEnhetV2Service);
    }

}
