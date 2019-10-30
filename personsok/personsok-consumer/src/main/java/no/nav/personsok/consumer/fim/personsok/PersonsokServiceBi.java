package no.nav.personsok.consumer.fim.personsok;

import no.nav.personsok.consumer.fim.personsok.to.FinnPersonRequest;
import no.nav.personsok.consumer.fim.personsok.to.FinnPersonResponse;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonForMangeForekomster;
import no.nav.tjeneste.virksomhet.personsoek.v1.FinnPersonUgyldigInput;

/**
 * Interface for tjenesten for personsøk.
 */
public interface PersonsokServiceBi {

	FinnPersonResponse finnPerson(FinnPersonRequest finnPersonRequest) throws FinnPersonForMangeForekomster, FinnPersonUgyldigInput;

}
