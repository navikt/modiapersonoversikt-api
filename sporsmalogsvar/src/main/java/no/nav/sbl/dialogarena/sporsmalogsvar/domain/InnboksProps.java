package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.modig.lang.option.Optional;

import java.io.Serializable;

public class InnboksProps implements Serializable {
    public final Optional<String> henvendelseId, oppgaveId, besvarModus;
    public final Optional<Boolean> fortsettModus;

    public InnboksProps(Optional<String> henvendelseId, Optional<String> oppgaveId, Optional<String> besvarModus, Optional<Boolean> fortsettModus) {
        this.henvendelseId = henvendelseId;
        this.oppgaveId = oppgaveId;
        this.besvarModus = besvarModus;
        this.fortsettModus = fortsettModus;
    }
}
