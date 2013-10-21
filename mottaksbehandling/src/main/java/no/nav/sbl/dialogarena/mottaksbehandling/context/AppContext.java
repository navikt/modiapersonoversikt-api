package no.nav.sbl.dialogarena.mottaksbehandling.context;

import no.nav.sbl.dialogarena.mottaksbehandling.ko.HendelseKo;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.HenvendelseRepo;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Oppgavesystem;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.SakSystemGsak;
import no.nav.sbl.dialogarena.mottaksbehandling.sak.SakSystemPensjon;
import no.nav.sbl.dialogarena.types.Pingable;

import java.util.List;

import static java.util.Arrays.asList;

public class AppContext {

    public static final String SERVLETCONTEXT_KEY = "no.nav.sbl.dialogarena.no.nav.sbl.dialogarena.mottaksbehandling.AppContext";

    public final HendelseKo hendelseKo;
    public final Oppgavesystem oppgavesystem;
    public final HenvendelseRepo repo;
    public final SakSystemGsak saksystem;
    public final SakSystemPensjon pensjonSaksystem;

    public AppContext(HendelseKo hendelseKo, Oppgavesystem oppgavesystem, HenvendelseRepo repo, SakSystemGsak saksystem, SakSystemPensjon pensjonSaksystem) {
        this.hendelseKo = hendelseKo;
        this.oppgavesystem = oppgavesystem;
        this.repo = repo;
        this.saksystem = saksystem;
        this.pensjonSaksystem = pensjonSaksystem;
    }
   
    public List<? extends Pingable> getPingables() {
        return asList(hendelseKo, oppgavesystem, repo, saksystem, pensjonSaksystem);
    }
    
}
