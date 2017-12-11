package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.rest.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

public class Oppfolgingsinfo {

    public final boolean erUnderOppfolging;
    public final Saksbehandler saksbehandler;
    public final AnsattEnhet saksbehandlerenhet;

    public Oppfolgingsinfo(boolean erUnderOppfolging, Saksbehandler saksbehandler) {
        this.erUnderOppfolging = erUnderOppfolging;
        this.saksbehandler = saksbehandler;
        this.saksbehandlerenhet = new AnsattEnhet("0118", "NAV Aremark");
    }
}
