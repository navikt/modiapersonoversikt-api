package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.strategier;

import no.finn.unleash.UnleashContext;
import no.finn.unleash.strategy.Strategy;

import java.util.Arrays;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class ByEnhetStrategy implements Strategy {
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
                .map(enheter -> enheter.anyMatch(enhet -> isValgtEnhet(unleashContext, enhet)))
                .orElse(false);
    }

    private boolean isValgtEnhet(UnleashContext unleashContext, String enhet) {
        return enhet.equals(unleashContext.getProperties().get("valgtEnhet"));
    }
}
