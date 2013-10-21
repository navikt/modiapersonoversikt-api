package no.nav.sbl.dialogarena.mottaksbehandling.ko;

import no.nav.melding.virksomhet.hendelse.v1.Hendelse;
import no.nav.sbl.dialogarena.types.Pingable;

public interface HendelseKo extends Pingable {
	
	Hendelse plukk();
	void put(Hendelse data);

}
