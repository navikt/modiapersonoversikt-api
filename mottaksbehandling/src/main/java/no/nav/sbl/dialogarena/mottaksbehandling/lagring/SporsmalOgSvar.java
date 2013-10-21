package no.nav.sbl.dialogarena.mottaksbehandling.lagring;

import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Key;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Key.key;

// CHECKSTYLE:OFF
public interface SporsmalOgSvar {

	Key<String> aktor 			            = key("aktor"),
				behandlingsid 	            = key("behandlingsid"),
				sporsmaletsBehandlingsId    = key("sporsmaletsBehandlingsId"),
				oppgaveid 		            = key("oppgaveid"),
				sporsmal 		            = key("sporsmal"),
				svar 			            = key("svar"),
				traad 			            = key("traad");
	Key<Tema> tema = key("tema");
	Key<Boolean> sensitiv = key("sensitiv");
	Key<DateTime> opprettet = key("opprettet");
}

