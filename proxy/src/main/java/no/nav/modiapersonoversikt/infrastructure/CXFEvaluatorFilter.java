package no.nav.modiapersonoversikt.infrastructure;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.boolex.MarkerList;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggerContextVO;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import ch.qos.logback.core.boolex.Matcher;
import org.slf4j.Marker;


/**
 * <p>This class was generated when migrating a logback-classic configuration
 * using JaninoEventEvaluator.</p>
 *
 * <p>JaninoEventEvaluator has been removed due to identified vulnerabilities.</p>which
 *
 * <p>Note that the generated code in the {@link #evaluate(ILoggingEvent)} method will
 * depend on the boolean expression, more specifically on the variables referenced
 * in the original boolean expression.</p>
 */
public class CXFEvaluatorFilter extends EventEvaluatorBase<ILoggingEvent> {

    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        int ERROR = Level.ERROR.toInt();
        String logger = event.getLoggerName();
        int level = event.getLevel().toInteger();
        return level != ERROR && (logger.contains("org.apache.cxf") || logger.contains("no.nav.common.cxf"));
    }
}
