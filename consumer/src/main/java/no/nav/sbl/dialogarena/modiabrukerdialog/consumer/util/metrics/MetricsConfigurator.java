package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.metrics;

import com.codahale.metrics.JmxAttributeGauge;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.servlets.MetricsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static no.nav.modig.lang.collections.IterUtils.on;

public class MetricsConfigurator extends MetricsServlet.ContextListener implements ApplicationListener<ContextRefreshedEvent> {
    public static final MetricRegistry registry = new MetricRegistry();
    private static final Logger logger = LoggerFactory.getLogger(MetricsConfigurator.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        lagJMXMetricsForCache();
        startReporters();
    }

    private void lagJMXMetricsForCache() {
        for (ObjectName objectName : getObjectNameForCaches()) {
            registry.register(name("ehcache", objectName.getKeyPropertyList().get("name"), "CacheMissPercentage"), new JmxAttributeGauge(objectName, "CacheMissPercentage"));
            registry.register(name("ehcache", objectName.getKeyPropertyList().get("name"), "CacheHitPercentage"), new JmxAttributeGauge(objectName, "CacheHitPercentage"));
        }
    }

    private void startReporters() {
        Slf4jReporter slf4jReporter = Slf4jReporter.forRegistry(registry)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        JmxReporter jmxReporter = JmxReporter.forRegistry(registry).build();


        slf4jReporter.start(1, TimeUnit.MINUTES);
        jmxReporter.start();
    }

    private static List<ObjectName> getObjectNameForCaches() {
        return on(ManagementFactory.getPlatformMBeanServer().queryNames(null, new QueryExp() {
            @Override
            public boolean apply(ObjectName name) throws BadStringOperationException, BadBinaryOpValueExpException, BadAttributeValueExpException, InvalidApplicationException {
                return name.getCanonicalName().startsWith("net.sf.ehcache") && name.getCanonicalName().contains("CacheStatistics");
            }

            @Override
            public void setMBeanServer(MBeanServer s) {

            }
        })).collect();
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
