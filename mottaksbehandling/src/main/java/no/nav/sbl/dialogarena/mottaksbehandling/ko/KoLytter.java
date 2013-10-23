package no.nav.sbl.dialogarena.mottaksbehandling.ko;

import no.nav.melding.virksomhet.hendelse.behandling.kommando.v1.StartBehandling;
import no.nav.melding.virksomhet.hendelse.v1.Hendelse;
import no.nav.sbl.dialogarena.mottaksbehandling.MottaksbehandlingKontekst;
import no.nav.sbl.dialogarena.mottaksbehandling.StartMottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KoLytter implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(KoLytter.class);
	private final MottaksbehandlingKontekst context;

	public KoLytter(MottaksbehandlingKontekst context) {
		this.context = context;
	}

	@SuppressWarnings({ "PMD.PreserveStackTrace" })
	public void run() {
		while (true) {
			try {
				plukkFraKo(context);
			} catch (Exception e) {
				LOG.error("Feil ved mottak av melding", e);
				try {
					// Hvis feks broker er nede ønsker vi ikke å fylle loggen med feil
					Thread.sleep(5000);
				} catch (InterruptedException e1) {
					throw new RuntimeException(e1);
				}
			}
		}
	}

	public static Record<SporsmalOgSvar> plukkFraKo(MottaksbehandlingKontekst context) {
		Hendelse innkommendeHendelse = context.hendelseKo.plukk();
		if (innkommendeHendelse instanceof StartBehandling) {
			return StartMottaksbehandling.start(context, (StartBehandling) innkommendeHendelse);
		} else {
			LOG.warn("Mottok ukjent melding: " + innkommendeHendelse);
			return null;
		}
	}

}
