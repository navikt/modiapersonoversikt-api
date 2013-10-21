package no.nav.sbl.dialogarena.mottaksbehandling.lagring;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.types.Pingable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;

public class HenvendelseRepoStub implements HenvendelseRepo {

	private final Map<String, Record<SporsmalOgSvar>> data = new HashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);

	@Override
	public Optional<Record<SporsmalOgSvar>> hentMedBehandlingsId(String behandlingsId) {
		return optional(data.get(behandlingsId));
	}

	@Override
	public Optional<Record<SporsmalOgSvar>> hentMedOppgaveId(String oppgaveId) {
		return on(data.values()).filter(SporsmalOgSvar.oppgaveid.is(oppgaveId)).head();
	}

	@Override
	public Record<SporsmalOgSvar> opprett(Record<SporsmalOgSvar> henvendelse) {
		String behandlingsId = IdGenerator.lagBehandlingsId(nextId.get());
		Record<SporsmalOgSvar> hvMedId = henvendelse.with(SporsmalOgSvar.behandlingsid, behandlingsId);
		data.put(behandlingsId, hvMedId);
		return hvMedId;
	}

	@Override
	public void oppdater(Record<SporsmalOgSvar> henvendelse) {
		data.put(henvendelse.get(SporsmalOgSvar.behandlingsid), henvendelse);
	}

    @Override
    public Pingable.Ping ping() {
        return Pingable.Ping.lyktes("MOTTAKSBEHANDLING_DATABASE_OK");
    }

}