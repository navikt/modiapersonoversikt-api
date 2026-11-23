package no.nav.modiapersonoversikt.infrastructure;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AsyncAppenderBase;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.status.Status;
import no.nav.common.log.MaskingAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogbackConfigurationTest {
    private LoggerContext context;

    @AfterEach
    void stopContext() {
        if (context != null) {
            context.stop();
        }
    }

    @Test
    void productionMarkerFilterAndLoggerHierarchySeparateStdoutFromTeamLogs() throws Exception {
        configureProductionLogback();

        Logger regularLogger = context.getLogger("no.nav.modiapersonoversikt.infrastructure.RegularLogger");
        // Prebuilt events keep the isolated LoggerContext independent of SLF4J's global MDC adapter.
        log(regularLogger, Level.INFO, null, "regularCanary");
        log(regularLogger, Level.INFO, new BasicMarkerFactory().getMarker("TEAM_LOGS"), "markerCanary");
        log(regularLogger, Level.INFO, null, "afterMarkerCanary");

        log(context.getLogger("TeamLog"), Level.INFO, null, "teamCanary");

        ListAppender<ILoggingEvent> stdoutSink = listAppender("consoleAppender");
        ListAppender<ILoggingEvent> teamSink = listAppender("team-logs");
        List<String> teamMessages = messages(teamSink);

        assertTrue(teamMessages.contains("teamCanary"));
        assertFalse(teamMessages.contains("regularCanary"));
        assertFalse(teamMessages.contains("markerCanary"));

        assertTrue(stdoutAppender().getCopyOfAttachedFiltersList().stream().allMatch(Filter::isStarted));
        assertFalse(context.getStatusManager().getCopyOfStatusList().stream()
                .anyMatch(status -> status.getLevel() == Status.ERROR));

        stdoutAppender().stop();
        List<String> stdoutMessages = messages(stdoutSink);
        assertTrue(stdoutMessages.contains("regularCanary"));
        assertTrue(stdoutMessages.contains("afterMarkerCanary"));
        assertFalse(stdoutMessages.contains("markerCanary"));
        assertFalse(stdoutMessages.contains("teamCanary"));
    }

    private void configureProductionLogback() throws Exception {
        String entryPoint = Files.readString(Path.of("src/main/resources/logback.xml"));
        assertTrue(entryPoint.contains("<include resource=\"logback-stdout-fixed.xml\"/>"));

        String productionConfig;
        try (InputStream input =
                getClass().getClassLoader().getResourceAsStream("logback-stdout-fixed.xml")) {
            assertNotNull(input);
            productionConfig = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        // Remove only the production status listener to keep isolated test configuration quiet on stdout.
        productionConfig = replaceOnce(
                productionConfig,
                "<statusListener class=\"ch.qos.logback.core.status.OnConsoleStatusListener\"/>",
                "");
        productionConfig = replaceSink(productionConfig, "consoleAppender");
        productionConfig = replaceSink(productionConfig, "team-logs");
        productionConfig = replaceOnce(productionConfig, "<included>", "<configuration>");
        productionConfig = replaceOnce(productionConfig, "</included>", "</configuration>");

        context = new LoggerContext();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        context.reset();
        try (InputStream input = new ByteArrayInputStream(productionConfig.getBytes(StandardCharsets.UTF_8))) {
            configurator.doConfigure(input);
        }

        assertInstanceOf(ListAppender.class, listAppender("consoleAppender"));
        assertInstanceOf(ListAppender.class, listAppender("team-logs"));
        assertInstanceOf(MaskingAppender.class, stdoutAppender());
        assertTrue(stdoutAppender().isStarted());
        assertTrue(listAppender("consoleAppender").isStarted());
        assertTrue(listAppender("team-logs").isStarted());
    }

    private AsyncAppenderBase<ILoggingEvent> stdoutAppender() {
        return (AsyncAppenderBase<ILoggingEvent>) context.getLogger(Logger.ROOT_LOGGER_NAME)
                .getAppender("stdout_json_masked");
    }

    @SuppressWarnings("unchecked")
    private ListAppender<ILoggingEvent> listAppender(String name) {
        Appender<ILoggingEvent> appender =
                name.equals("consoleAppender")
                        ? ((AppenderAttachable<ILoggingEvent>) stdoutAppender()).getAppender(name)
                        : context.getLogger("TeamLog").getAppender(name);
        return (ListAppender<ILoggingEvent>) appender;
    }

    private List<String> messages(ListAppender<ILoggingEvent> appender) {
        return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    private void log(Logger logger, Level level, Marker marker, String message) {
        assertTrue(logger.isEnabledFor(level));
        LoggingEvent event = new LoggingEvent();
        event.setLoggerName(logger.getName());
        event.setLevel(level);
        event.setMessage(message);
        event.setMDCPropertyMap(Map.of());
        event.setLoggerContext(context);
        if (marker != null) {
            event.addMarker(marker);
        }
        logger.callAppenders(event);
    }

    private static String replaceSink(String xml, String name) {
        Pattern pattern = Pattern.compile("(?s)<appender name=\"" + Pattern.quote(name) + "\"[^>]*>.*?</appender>");
        Matcher matcher = pattern.matcher(xml);
        assertEquals(
                1, matcher.results().count(), "Expected exactly one production sink appender named " + name);
        return matcher.replaceFirst(Matcher.quoteReplacement(
                "<appender name=\"" + name + "\" class=\"ch.qos.logback.core.read.ListAppender\"/>"));
    }

    private static String replaceOnce(String text, String oldValue, String newValue) {
        assertEquals(
                1,
                text.split(Pattern.quote(oldValue), -1).length - 1,
                "Expected exactly one XML fragment: " + oldValue);
        return text.replace(oldValue, newValue);
    }
}
