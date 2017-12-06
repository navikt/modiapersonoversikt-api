package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.rest.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;

public class Oppfolgingsinfo {

    public final boolean erUnderOppfolging;
    public final Saksbehandler saksbehandler;

    public Oppfolgingsinfo(boolean erUnderOppfolging, Saksbehandler saksbehandler) {
        this.erUnderOppfolging = erUnderOppfolging;
        this.saksbehandler = saksbehandler;
    }
}
