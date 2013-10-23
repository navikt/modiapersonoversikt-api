package no.nav.sbl.dialogarena.sporsmalogsvar.besvare.web;

import no.nav.sbl.dialogarena.mottaksbehandling.Mottaksbehandling;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.HenvendelseRepo;
import no.nav.sbl.dialogarena.mottaksbehandling.lagring.SporsmalOgSvar;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Oppgavesystem;
import no.nav.sbl.dialogarena.mottaksbehandling.oppgave.Tema;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.records.Record;
import no.nav.sbl.dialogarena.sporsmalogsvar.besvare.TraadPanel;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.HenvendelsePortType;
import org.apache.wicket.markup.html.WebPage;

import javax.inject.Inject;

public class SporsmalOgSvarPage extends WebPage {

    @Inject
    HenvendelsePortType henvendelsePortType;

    @Inject
    Mottaksbehandling mottaksbehandling;

    @Inject
    HenvendelseRepo repo;

    @Inject
    Oppgavesystem oppgavesystem;

    public SporsmalOgSvarPage() {
        super();
        TraadPanel traadPanel = new TraadPanel("besvar", "10108000398", mottaksbehandling, henvendelsePortType);


        String s = oppgavesystem.lagOppgave("123-foo", "123456789", Tema.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT);
        repo.opprett(new Record<SporsmalOgSvar>().with(SporsmalOgSvar.oppgaveid, s).with(SporsmalOgSvar.behandlingsid, "123-foo").with(SporsmalOgSvar.tema, Tema.ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT).with(SporsmalOgSvar.traad, "1"));
        traadPanel.besvar(s);
        add(traadPanel);
    }
}
