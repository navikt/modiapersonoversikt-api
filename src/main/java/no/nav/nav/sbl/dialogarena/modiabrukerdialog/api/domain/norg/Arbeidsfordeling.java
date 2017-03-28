package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg;

import java.util.Optional;

public class Arbeidsfordeling {

    private final Optional<String> geografiskNedslagsfelt;
    private final Optional<String> arkivTema;

    public Arbeidsfordeling(String geografiskNedslagsfelt, String arkivTema) {
        this.geografiskNedslagsfelt = Optional.ofNullable(geografiskNedslagsfelt);
        this.arkivTema = Optional.ofNullable(arkivTema);
    }

    public Optional<String> getGeografiskNedslagsfelt() {
        return geografiskNedslagsfelt;
    }

    public Optional<String> getArkivTema() {
        return arkivTema;
    }

}
