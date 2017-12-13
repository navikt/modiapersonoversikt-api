package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.rest.oppfolgingsinfo;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saksbehandler;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.AnsattEnhet;

import java.util.Optional;

import static java.util.Optional.ofNullable;

public class Oppfolgingsinfo {

    public final boolean erUnderOppfolging;
    private Saksbehandler veileder;
    private AnsattEnhet oppfolgingsenhet;

    public Oppfolgingsinfo(Boolean erUnderOppfolging) {
        this.erUnderOppfolging = erUnderOppfolging;
    }

    public boolean erUnderOppfolging() {
        return erUnderOppfolging;
    }

    public Optional<Saksbehandler> getVeileder() {
        return ofNullable(veileder);
    }

    public Optional<AnsattEnhet> getOppfolgingsenhet() {
        return ofNullable(oppfolgingsenhet);
    }

    public Oppfolgingsinfo withVeileder(Saksbehandler veileder) {
        this.veileder = veileder;
        return this;
    }

    public Oppfolgingsinfo withOppfolgingsenhet(AnsattEnhet oppfolgingsenhet) {
        this.oppfolgingsenhet = oppfolgingsenhet;
        return this;
    }

}
