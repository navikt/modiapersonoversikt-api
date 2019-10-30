package no.nav.modig.jaxws.handlers;

import no.nav.modig.common.MDCOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.ProtocolException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.HashSet;
import java.util.Set;

/**
 * This handler generates the callId and puts it in the SOAPHeader
 *
 */
public class MDCOutHandler implements SOAPHandler<SOAPMessageContext> {
    protected static final Logger log = LoggerFactory.getLogger(MDCOutHandler.class.getName());

    // QName for the callId header
    private static final QName CALLID_QNAME = new QName("uri:no.nav.applikasjonsrammeverk", MDCOperations.MDC_CALL_ID);

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        // OUTBOUND processing
        if (outbound) {
            String callId = MDCOperations.getFromMDC(MDCOperations.MDC_CALL_ID);
            if (callId == null) {
                throw new RuntimeException("CallId skal være tilgjengelig i MDC på dette tidspunkt. Om du er en webapp, må du legge til et MDCFilter i web.xml " +
                        "(oppskrift på dette: http://confluence.adeo.no/display/Modernisering/MDCFilter). " +
                        "Om du er noe annet må du generere callId selv og legge på MDC. Hjelpemetoder finnes i no.nav.modig.common.MDCOperations.");
            }
            log.debug("Add the callId to the SOAP message: " + callId);
            try {
                SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.getHeader();

                SOAPElement callIdElement = header.addChildElement(CALLID_QNAME);
                callIdElement.setValue(callId);
            } catch (SOAPException e) {
                log.error(e.getMessage());
                throw new ProtocolException(e);
            }
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return new HashSet<QName>() {
            {
                add(CALLID_QNAME);
            }
        };
    }

}
