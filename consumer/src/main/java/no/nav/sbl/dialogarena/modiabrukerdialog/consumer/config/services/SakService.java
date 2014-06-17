package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.Oppgavebehandling;
import org.joda.time.DateTime;

import javax.inject.Inject;

public class SakService {

    @Inject
    private Oppgavebehandling oppgavebehandling;

    @Inject
    protected SendHenvendelsePortType ws;

    public Melding getSakFromHenvendelse(String sporsmalsId) {
        return getMelding();
    }

    public void plukkSakIGsak(String meldingsId) {
    }

    public void ferdigstillSakIGsak(Melding melding) {
    }

    private Melding getMelding() {
        Melding melding = new Melding("id", Meldingstype.SPORSMAL, DateTime.now());
        melding.fritekst = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat. Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto";
        melding.tema = "HJELPEMIDLER";
        return melding;
    }
}
