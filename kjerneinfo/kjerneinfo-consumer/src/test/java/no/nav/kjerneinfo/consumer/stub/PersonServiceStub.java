package no.nav.kjerneinfo.consumer.stub;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.domain.person.*;
import org.joda.time.LocalDateTime;

import java.io.Serializable;

public class PersonServiceStub implements Serializable {

    private static final long serialVersionUID = -756407610503817704L;

    public Person hentPerson(Fodselsnummer fnr) {
        // Opprette stub
        Endringsinformasjon endringsinformasjonStub = new Endringsinformasjon();
        endringsinformasjonStub.setEndretAv("Endret Av");
        endringsinformasjonStub.setSistOppdatert(new LocalDateTime());

        Kodeverdi sivilstandStub = new Kodeverdi.With().kodeRef("GIFT").done();

        Personnavn navnStub = new Personnavn();
        navnStub.setSistEndret(endringsinformasjonStub);
        navnStub.setFornavn("Jens");
        navnStub.setMellomnavn("Andersen");
        navnStub.setEtternavn("");

        Personfakta faktaStub = new Personfakta();
        faktaStub.setPersonfaktaId(1);
        faktaStub.setPersonnavn(navnStub);
        faktaStub.setSivilstand(sivilstandStub);

        Person personStub = new Person();
        personStub.setPersonId(1);
        personStub.setFodselsnummer(fnr);
        personStub.setPersonfakta(faktaStub);

        return personStub;
    }
}
