package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.rest.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Oppfolgingsinfo {

    public final boolean erUnderOppfolging;
    private final Saksbehandler saksbehandler;
    private final AnsattEnhet saksbehandlerenhet;

    public Oppfolgingsinfo(Boolean erUnderOppfolging, Saksbehandler saksbehandler, AnsattEnhet saksbehandlerenhet) {
        this.erUnderOppfolging = erUnderOppfolging;
        this.saksbehandler = saksbehandler;
        this.saksbehandlerenhet = saksbehandlerenhet;
    }

    public boolean erUnderOppfolging() {
        return erUnderOppfolging;
    }

    public Optional<Saksbehandler> getSaksbehandler() {
        return ofNullable(saksbehandler);
    }

    public Optional<AnsattEnhet> getSaksbehandlerenhet() {
        return ofNullable(saksbehandlerenhet);
    }

}
