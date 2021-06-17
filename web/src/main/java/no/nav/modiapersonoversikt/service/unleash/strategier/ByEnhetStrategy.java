package no.nav.modiapersonoversikt.service.unleash.strategier;

import no.finn.unleash.UnleashContext;
import no.finn.unleash.strategy.Strategy;

import java.util.Arrays;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class ByEnhetStrategy implements Strategy {

    public static final String ENHETER = "enheter";

    @Override
    public String getName() {
        return "byEnhet";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return false;
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters, UnleashContext unleashContext) {

        return ofNullable(parameters)
                .map(par -> par.get("valgtEnhet"))
                .filter(s -> !s.isEmpty())
                .map(enheter -> enheter.split(","))
                .map(Arrays::stream)
                .map(enheter -> enheter.anyMatch(enhet -> ansattHarEnhet(unleashContext, enhet)))
                .orElse(false);
    }

    private boolean ansattHarEnhet(UnleashContext unleashContext, String enhet) {
        String enheter = unleashContext.getProperties().get(ENHETER) != null ? unleashContext.getProperties().get(ENHETER) : "";
        return Arrays.asList(enheter.split(",")).contains(enhet);
    }
}
