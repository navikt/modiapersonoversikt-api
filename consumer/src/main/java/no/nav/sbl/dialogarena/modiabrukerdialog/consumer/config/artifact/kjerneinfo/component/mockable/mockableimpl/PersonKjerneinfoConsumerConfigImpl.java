package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.artifact.kjerneinfo.component.mockable.mockableimpl;

import no.nav.kjerneinfo.consumer.fim.mapping.KjerneinfoMapper;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.support.DefaultPersonKjerneinfoService;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.tjeneste.virksomhet.person.v1.PersonPortType;

public class PersonKjerneinfoConsumerConfigImpl {

    private PersonPortType personPortType;
    private PersonPortType selfTestPersonPortType;
    private KjerneinfoMapper kjerneinfoMapperBean;
    private EnforcementPoint kjerneinfoPep;

    public PersonKjerneinfoConsumerConfigImpl(PersonPortType personPortType, PersonPortType selfTestPersonPortType, KjerneinfoMapper kjerneinfoMapperBean, EnforcementPoint kjerneinfoPep) {
        this.personPortType = personPortType;
        this.selfTestPersonPortType = selfTestPersonPortType;
        this.kjerneinfoMapperBean = kjerneinfoMapperBean;
        this.kjerneinfoPep = kjerneinfoPep;
    }

    public PersonKjerneinfoServiceBi personKjerneinfoServiceBi() {
        return new DefaultPersonKjerneinfoService(personPortType, selfTestPersonPortType, kjerneinfoMapperBean, kjerneinfoPep);
    }

}
