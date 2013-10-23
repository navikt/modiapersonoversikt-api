package no.nav.sbl.dialogarena.mottaksbehandling;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.mottaksbehandling.BesvarSporsmal;
import no.nav.sbl.dialogarena.mottaksbehandling.ISvar;
import no.nav.sbl.dialogarena.mottaksbehandling.MottaksbehandlingKontekst;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Oppgave;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.ISak;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static no.nav.modig.lang.option.Optional.none;

public interface Mottaksbehandling {

    void besvarSporsmal(Record<ISvar> svar);
    Optional<Record<SporsmalOgSvar>> hentSporsmalOgSvar(String oppgaveId);
    List<Record<ISak>> hentSaker(String brukerId);
    Optional<Record<Oppgave>> plukkOppgave(String tema);
    List<WSHenvendelse> tidligereDialog(String fnr, String traadId, String etterBehandlingsId);
    void leggTilbakeOppgave(String oppgaveId, String begrunnelse);

    public static class Default implements Mottaksbehandling {
        private static final Logger log = LoggerFactory.getLogger(Default.class);
        private MottaksbehandlingKontekst context;

        @Override
        public void besvarSporsmal(Record<ISvar> svar) {
            BesvarSporsmal.besvar(context, svar);
        }

        @Override
        public Optional<Record<SporsmalOgSvar>> hentSporsmalOgSvar(String oppgaveId) {
            Optional<Record<SporsmalOgSvar>> resultObject = context.repo.hentMedOppgaveId(oppgaveId);
            if (!resultObject.isSome()) {
                log.error("Oppgave fra GSAK med oppgaveId " + oppgaveId + " finnes ikke i Mottaksbehandling. Databasene kan være i usync.");
            }
            return resultObject;
        }

        @Override
        public List<Record<ISak>> hentSaker(String brukerId) {
            List<Record<ISak>> saksliste = context.saksystem.saksliste(brukerId);
            saksliste.addAll(context.pensjonSaksystem.saksliste(brukerId));
            return saksliste;
        }

        @Override
        public Optional<Record<Oppgave>> plukkOppgave(String tema) {
            Optional<Record<Oppgave>> plukket = context.oppgavesystem.plukkOppgave(Tema.valueOf(tema));

            if (plukket.isSome()) {
                String oppgid = plukket.get().get(Oppgave.id);
                Optional<Record<SporsmalOgSvar>> henvendelse = context.repo.hentMedOppgaveId(oppgid);
                if (henvendelse.isSome()) {
                    return plukket;
                } else {
                    log.warn("Oppgave fra GSAK med oppgaveId " + oppgid + " finnes ikke i Mottaksbehandling.\n" +
                            "Databasene kan være i usync. Vi ferdigstiller oppgaven og plukker en ny.");
                    context.oppgavesystem.ferdigstill(oppgid);
                    plukkOppgave(tema);
                }
            }
            return none();
        }

        @Override
        public List<WSHenvendelse> tidligereDialog(String fnr, String traadId, String etterBehandlingsId) {
            return context.henvendelser.tidligereDialog(fnr, traadId, etterBehandlingsId);
        }

        @Override
        public void leggTilbakeOppgave(String oppgaveId, String begrunnelse) {
            context.oppgavesystem.fristill(oppgaveId, begrunnelse);
        }

        public Default(MottaksbehandlingKontekst context) {
            this.context = context;
        }
    }
}
