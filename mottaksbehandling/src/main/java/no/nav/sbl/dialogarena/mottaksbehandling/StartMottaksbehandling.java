package no.nav.sbl.dialogarena.mottaksbehandling;

import no.nav.melding.virksomhet.hendelse.behandling.kommando.v1.StartBehandling;
import no.nav.melding.virksomhet.henvendelsebehandling.behandlingsresultat.v1.XMLHenvendelse;
import no.nav.sbl.dialogarena.common.integrasjonsutils.JSON;
import no.nav.sbl.dialogarena.mottaksbehandling.MottaksbehandlingKontekst;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;

import java.util.Map;

public class StartMottaksbehandling {
	
    public static Record<SporsmalOgSvar> start(MottaksbehandlingKontekst context, StartBehandling sb) {
        
    	XMLHenvendelse xml = (XMLHenvendelse) sb.getBehandlingsinformasjon();
    	Map<String, Object> behandlingsresultat = JSON.unmarshal(xml.getBehandlingsresultat());
    	Tema tema = Tema.valueOf(xml.getTema());
        
    	// TODO: Oppgave bør ikke ta behandlingsid?
        String oppgaveId = context.oppgavesystem.lagOppgave(null, (String) behandlingsresultat.get("fodselsnummer"), tema);

		Record<SporsmalOgSvar> henvendelse = new Record<SporsmalOgSvar>()
				.with(SporsmalOgSvar.aktor, xml.getAktor())
                .with(SporsmalOgSvar.sporsmaletsBehandlingsId, xml.getBehandlingsId())
				.with(SporsmalOgSvar.oppgaveid, oppgaveId)
				.with(SporsmalOgSvar.sporsmal, (String) behandlingsresultat.get("fritekst"))
                .with(SporsmalOgSvar.traad, xml.getTraad())
				.with(SporsmalOgSvar.tema, tema)
                .with(SporsmalOgSvar.sensitiv, xml.isSensitiv())
                .with(SporsmalOgSvar.opprettet, xml.getOpprettetDato());

        return context.repo.opprett(henvendelse);
    }
    
}
