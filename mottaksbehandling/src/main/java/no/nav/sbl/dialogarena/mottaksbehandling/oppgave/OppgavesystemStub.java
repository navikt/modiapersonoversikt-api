package no.nav.sbl.dialogarena.mottaksbehandling.oppgave;

import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.types.Pingable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;

/**
 * Stub av metoder for gsakOppgaveIntegration
 */
public class OppgavesystemStub implements Oppgavesystem {

    private static final AtomicLong NEXTID = new AtomicLong();
    private static final Map<String, Record<Oppgave>> OPPGAVER = new HashMap<>();

    @Override
    public String lagOppgave(String behandlingsId, String fodselsnummer, Tema tema) {
        Record<Oppgave> oppg = new Record<Oppgave>()
                .with(Oppgave.id, NEXTID.incrementAndGet() + "")
                .with(Oppgave.behandlingsid, behandlingsId)
                .with(Oppgave.fodselsnummer, fodselsnummer)
                .with(Oppgave.tema, tema)
                .with(Oppgave.saksbehandlerid, Optional.<String>none())
                .with(Oppgave.ferdigstilt, false);

        OPPGAVER.put(oppg.get(Oppgave.id), oppg);
        return oppg.get(Oppgave.id);
    }

    @Override
    public Record<Oppgave> hentOppgave(String oppgaveId) {
        return OPPGAVER.get(oppgaveId);
    }

    @Override
    public void fristill(String oppgaveId, String begrunnelse) {
        Record<Oppgave> oppgave = OPPGAVER.get(oppgaveId);
        OPPGAVER.put(oppgaveId, oppgave.with(Oppgave.saksbehandlerid, Optional.<String>none()));
    }

    public void ferdigstill(String oppgaveId) {
        Record<Oppgave> oppgave = OPPGAVER.get(oppgaveId);
        OPPGAVER.put(oppgaveId, oppgave.with(Oppgave.ferdigstilt, true));
    }

    @Override
    public Optional<Record<Oppgave>> plukkOppgave(Tema tema) {
        Optional<Record<Oppgave>> plukket =
            on(OPPGAVER.values())
                .filter(Oppgave.ferdigstilt.is(false))
                .filter(Oppgave.tema.is(tema))
                .head();

        for (Record<Oppgave> oppg : plukket) {
            Record<Oppgave> oppgaveMedSaksbehandler = oppg.with(Oppgave.saksbehandlerid, optional(saksbehandlerid()));
            OPPGAVER.put(oppg.get(Oppgave.id), oppgaveMedSaksbehandler);
            return optional(oppgaveMedSaksbehandler);
        }
        return Optional.none();
    }

    private String saksbehandlerid() {
        SubjectHandler subjectHandler = SubjectHandler.getSubjectHandler();
        return subjectHandler.getUid();
    }

    @Override
    public Pingable.Ping ping() {
        return Pingable.Ping.lyktes("GSAK_OK");
    }
}
