package no.nav.modiapersonoversikt.service.kodeverk;


import no.nav.modiapersonoversikt.legacy.api.service.saker.GsakKodeverk;

import java.util.Map;

public class GsakKodeverkFraFil implements GsakKodeverk {
    private final Map<String, String> fagsystemMapping;

    public GsakKodeverkFraFil() {
        this.fagsystemMapping = GsakKodeverkFagsystem.Parser.parse();
    }

    @Override
    public Map<String, String> hentFagsystemMapping() {
        return fagsystemMapping;
    }

}
