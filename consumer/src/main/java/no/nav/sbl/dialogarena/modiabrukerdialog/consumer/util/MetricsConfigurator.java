package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

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
import static no.nav.modig.modia.metrics.MetricsFactory.registry;

public class MetricsConfigurator extends MetricsServlet.ContextListener implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(MetricsConfigurator.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        lagJMXMetricsForCache();
        lagJVMMetrics();
        startReporters();
    }

    private void lagJMXMetricsForCache() {
        for (ObjectName objectName : getObjectNameForCaches()) {
            final String name = objectName.getKeyPropertyList().get("name");
            if(name != null && !name.isEmpty()) {
                registry.register(name("ehcache", name, "CacheMisses"), new JmxAttributeGauge(objectName, "CacheMisses"));
                registry.register(name("ehcache", name, "CacheHits"), new JmxAttributeGauge(objectName, "CacheHits"));
            }
        }
    }

    private void lagJVMMetrics() {
        leggTilJMXGauges("java.lang", "ClassLoading", "LoadedClassCount", "TotalLoadedClassCount", "UnloadedClassCount");
        leggTilJMXGauges("java.lang", "OperatingSystem", "FreePhysicalMemorySize", "ProcessCpuLoad", "SystemCpuLoad");
        leggTilJMXGauges("java.lang", "Threading", "DaemonThreadCount", "PeakThreadCount", "ThreadCount", "TotalStartedThreadCount");
    }

    private void leggTilJMXGauges(String domain, String type, String... attributeNames) {
        try {
            ObjectName objectName = new ObjectName(domain, "type", type);
            for (String attributeName : attributeNames) {
                registry.register(name(domain, type, attributeName), new JmxAttributeGauge(objectName, attributeName));
            }
        } catch (MalformedObjectNameException e) {
                logger.error("Feil i leggTilJMXGauges", e);
        }
    }

    private void startReporters() {
        Slf4jReporter slf4jReporter = Slf4jReporter.forRegistry(registry)
                .outputTo(logger)
                .withLoggingLevel(Slf4jReporter.LoggingLevel.DEBUG) //Slik at det ikke spammer console loggen
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
