package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppfolgingsinfo.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.tjeneste.virksomhet.oppfolgingsinfo.v1.meldinger.WSOppfolgingsdata;

public class Oppfolgingsinfo {

    public final boolean erUnderOppfolging;
    private final Saksbehandler saksbehandler;

    public Oppfolgingsinfo(WSOppfolgingsdata wsOppfolgingsdata, Saksbehandler saksbehandler) {
        this.erUnderOppfolging = wsOppfolgingsdata.isErUnderOppfolging();
        this.saksbehandler = saksbehandler;
    }
}
