package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.endpoint.unleash.strategier;

import no.finn.unleash.strategy.Strategy;

import java.util.Map;

public class IsNotProdStrategy implements Strategy {
    @Override
    public String getName() {
        return "isNotProd";
    }

    @Override
    public boolean isEnabled(Map<String, String> map) {
        return !"p".equals(System.getProperty("environment.name", "local"));
    }
}
