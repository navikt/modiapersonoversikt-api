package no.nav.sbl.dialogarena.mottaksbehandling.ko;

import no.nav.melding.virksomhet.hendelse.v1.Hendelse;
import no.nav.sbl.dialogarena.types.Pingable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class HendelseKoStub implements HendelseKo {

	private final BlockingQueue<Hendelse> queue = new ArrayBlockingQueue<>(5000);

	@Override
	public Hendelse plukk() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			throw new RuntimeException("Feil putting på kø", e);
		}
	}

	@Override
	public void put(Hendelse data) {
		try {
			queue.put(data);
		} catch (InterruptedException e) {
			throw new RuntimeException("Feil putting på kø", e);
		}
	}

    @Override
    public Pingable.Ping ping() {
        return Pingable.Ping.lyktes("BESVAREHENVENDELSE_JMS_OK");
    }
}