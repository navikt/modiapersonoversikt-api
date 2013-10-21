package no.nav.sbl.dialogarena.mottaksbehandling;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.context.AppContext;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Oppgave;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.ISak;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Mottaksbehandling {
    private static final Logger log = LoggerFactory.getLogger(Mottaksbehandling.class);
    public final AppContext context;

    public void besvarSporsmal(Record<ISvar> svar) {
        BesvarSporsmal.besvar(context, svar);
    }

    public Record<SporsmalOgSvar> hentSporsmalOgSvar(String oppgaveId) {
        Optional<Record<SporsmalOgSvar>> resultObject = context.repo.hentMedOppgaveId(oppgaveId);
        if (!resultObject.isSome()) {
            log.error("Oppgave fra GSAK med oppgaveId " + oppgaveId + " finnes ikke i Mottaksbehandling. Databasene kan være i usync.");
            return null;
        }
        return resultObject.get();
    }

    public List<Record<ISak>> hentSaker(String brukerId) {
        List<Record<ISak>> saksliste = context.saksystem.saksliste(brukerId);
        saksliste.addAll(context.pensjonSaksystem.saksliste(brukerId));
        return saksliste;
    }

    public Record<Oppgave> plukkOppgave(String tema) {
        Optional<Record<Oppgave>> plukket = context.oppgavesystem.plukkOppgave(Tema.valueOf(tema));
        for (Record<Oppgave> oppgave : plukket) {
            String oppgid = oppgave.get(Oppgave.id);
            Optional<Record<SporsmalOgSvar>> optionalHenvendelse = context.repo.hentMedOppgaveId(oppgid);
            if (optionalHenvendelse.isSome()) {
                return oppgave;
            } else {
                log.warn("Oppgave fra GSAK med oppgaveId " + oppgid + " finnes ikke i Mottaksbehandling.\n" +
                        "Databasene kan være i usync. Vi ferdigstiller oppgaven og plukker en ny.");
                context.oppgavesystem.ferdigstill(oppgid);
                plukkOppgave(tema);
            }
        }
        return null;
    }

    public void leggTilbakeOppgave(String oppgaveId, String begrunnelse) {
        context.oppgavesystem.fristill(oppgaveId, begrunnelse);
    }

    public Mottaksbehandling(AppContext context) {
        this.context = context;
    }
}
