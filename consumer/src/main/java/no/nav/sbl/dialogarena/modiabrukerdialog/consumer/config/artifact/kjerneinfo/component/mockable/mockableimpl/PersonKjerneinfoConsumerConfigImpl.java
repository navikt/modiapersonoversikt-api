package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.tjeneste.virksomhet.person.v2.PersonV2;

public class PersonKjerneinfoConsumerConfigImpl {

    private PersonV2 personPortType;
    private PersonV2 selfTestPersonPortType;
    private KjerneinfoMapper kjerneinfoMapperBean;
    private EnforcementPoint kjerneinfoPep;

    public PersonKjerneinfoConsumerConfigImpl(PersonV2 personPortType, PersonV2 selfTestPersonPortType, KjerneinfoMapper kjerneinfoMapperBean, EnforcementPoint kjerneinfoPep) {
        this.personPortType = personPortType;
        this.selfTestPersonPortType = selfTestPersonPortType;
        this.kjerneinfoMapperBean = kjerneinfoMapperBean;
        this.kjerneinfoPep = kjerneinfoPep;
    }

    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return new DefaultPersonKjerneinfoService(personPortType, selfTestPersonPortType, kjerneinfoMapperBean, kjerneinfoPep);
    }

}
