package no.nav.sbl.dialogarena.mottaksbehandling;

import no.nav.melding.virksomhet.hendelse.behandling.status.v1.BehandlingAvsluttet;
import no.nav.melding.virksomhet.hendelse.behandling.v1.Behandlingstyper;
import no.nav.melding.virksomhet.hendelse.v1.ApplikasjonIDer;
import no.nav.melding.virksomhet.hendelse.v1.Kodeverdi;
import no.nav.melding.virksomhet.henvendelsebehandling.behandlingsresultat.v1.XMLHenvendelse;
import no.nav.sbl.dialogarena.common.integrasjonsutils.JSON;
import no.nav.sbl.dialogarena.mottaksbehandling.context.MottaksbehandlingKontekst;
import no.nav.sbl.dialogarena.mottaksbehandling.ko.HendelseKo;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Oppgavesystem;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Key;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;

public class BesvarSporsmal {

	public static void besvar(MottaksbehandlingKontekst context, Record<ISvar> svar) {
		Record<SporsmalOgSvar> henvendelse = context.repo.hentMedBehandlingsId(svar.get(ISvar.behandlingsId))
				.getOrThrow(new RuntimeException("Fant ingen henvendelser med behandlingsid " + svar.get(ISvar.behandlingsId)));
		ferdigstillOppgave(context.oppgavesystem, henvendelse.get(SporsmalOgSvar.oppgaveid));
		avsluttBehandling(context.hendelseKo, henvendelse, svar);
	}

	private static void ferdigstillOppgave(Oppgavesystem oppgavesystem, String oppgaveId) {
        oppgavesystem.ferdigstill(oppgaveId);
	}

    private static void avsluttBehandling(HendelseKo hendelseKo, Record<SporsmalOgSvar> henvendelse, Record<ISvar> svar) {
        hendelseKo.put(avsluttmelding(henvendelse, svar));
    }

	private static BehandlingAvsluttet avsluttmelding(Record<SporsmalOgSvar> eksisterende, Record<ISvar> svar) {
		Record<SporsmalOgSvar> henvendelse = eksisterende.with(SporsmalOgSvar.svar, svar.get(ISvar.fritekst)).with(SporsmalOgSvar.sensitiv, svar.get(ISvar.sensitiv));

		Record<Behandlingsresultat> behandlingsresultat = new Record<Behandlingsresultat>()
				.with(Behandlingsresultat.fritekst, henvendelse.get(SporsmalOgSvar.svar));

        XMLHenvendelse xml = new XMLHenvendelse();
        xml.setAktor(henvendelse.get(SporsmalOgSvar.aktor));
        xml.setBehandlingsresultat(JSON.marshal(behandlingsresultat));
        xml.setBehandlingsId(henvendelse.get(SporsmalOgSvar.behandlingsid));
        xml.setHenvendelseType("SVAR");
        xml.setOpprettetDato(henvendelse.get(SporsmalOgSvar.opprettet));
        xml.setSensitiv(henvendelse.get(SporsmalOgSvar.sensitiv));
        xml.setTema(henvendelse.get(SporsmalOgSvar.tema).name());
        xml.setTraad(henvendelse.get(SporsmalOgSvar.traad));

        BehandlingAvsluttet avslutt = new BehandlingAvsluttet();
        avslutt.setHendelsesId("" + System.nanoTime());
        avslutt.setHendelsesprodusentREF(kodeverdi(new ApplikasjonIDer(), "VERDIKJEDE-MOCK"));
        avslutt.setBehandlingstype(kodeverdi(new Behandlingstyper(), "ae0009"));
        avslutt.getAktoerREF().add(xml.getAktor());
        avslutt.setBehandlingsinformasjon(xml);
		return avslutt;
	}

	// CHECKSTYLE:OFF
    interface Behandlingsresultat {
    	Key<String> fritekst = new Key<>("fritekst");
    }
	// CHECKSTYLE:ON

    private static <T extends Kodeverdi> T kodeverdi(T kode, String verdi) {
    	kode.setValue(verdi);
    	return kode;
    }

}
