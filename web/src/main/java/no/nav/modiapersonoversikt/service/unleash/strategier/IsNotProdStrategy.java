package no.nav.modiapersonoversikt.service.unleash.strategier;

import no.finn.unleash.strategy.Strategy;
import no.nav.common.utils.EnvironmentUtils;

import java.util.Map;

public class IsNotProdStrategy implements Strategy {
    @Override
    public String getName() {
        return "isNotProd";
    }

    @Override
    public boolean isEnabled(Map<String, String> map) {
        return !"p".equals(EnvironmentUtils.getOptionalProperty("APP_ENVIRONMENT_NAME").orElse("local"));
    }
}
