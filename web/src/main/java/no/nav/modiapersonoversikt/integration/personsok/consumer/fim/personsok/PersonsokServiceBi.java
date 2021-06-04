package no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok;

import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.modiapersonoversikt.integration.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonFault1;

/**
 * Interface for tjenesten for personsøk.
 */
public interface PersonsokServiceBi {

	FinnPersonResponse finnPerson(FinnPersonRequest finnPersonRequest) throws FinnPersonFault, FinnPersonFault1;

}
