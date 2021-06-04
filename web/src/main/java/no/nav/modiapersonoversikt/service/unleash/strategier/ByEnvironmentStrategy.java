package no.nav.modiapersonoversikt.service.unleash.strategier;

import no.finn.unleash.strategy.Strategy;
import no.nav.common.utils.EnvironmentUtils;

import java.util.Arrays;
import java.util.Map;

import static java.util.Optional.ofNullable;

public class ByEnvironmentStrategy implements Strategy {

    public static final String ENVIRONMENT_PROPERTY = "APP_ENVIRONMENT_NAME";

    @Override
    public String getName() {
        return "byEnvironment";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return ofNullable(parameters)
                .map(par -> par.get("miljø"))
                .filter(s -> !s.isEmpty())
                .map(miljoer -> miljoer.split(","))
                .map(Arrays::stream)
                .map(miljoer -> miljoer.anyMatch(this::isCurrentEnvironment))
                .orElse(false);
    }

    private boolean isCurrentEnvironment(String env) {
        return EnvironmentUtils.getOptionalProperty(ENVIRONMENT_PROPERTY ).orElse("local").equals(env);
    }

}
