package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.servlets.MetricsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class MetricsConfig extends MetricsServlet.ContextListener {
    public static final MetricRegistry registry = new MetricRegistry();
    private static final Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    static {
        Slf4jReporter slf4jReporter = Slf4jReporter.forRegistry(registry)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        slf4jReporter.start(1, TimeUnit.MINUTES);
    }

    //Metrics servlet configuration
    @Override
    protected TimeUnit getRateUnit() {
        return TimeUnit.SECONDS;
    }

    @Override
    protected TimeUnit getDurationUnit() {
        return TimeUnit.MILLISECONDS;
    }

    @Override
    protected MetricRegistry getMetricRegistry() {
        return registry;
    }
}
