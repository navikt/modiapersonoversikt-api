package no.nav.sbl.dialogarena.mottaksbehandling.tjeneste;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TjenesteSikkerhet {

    public static final String  standardBrukernavn  = "Z900002",
                                standardPassord     = "***REMOVED***";

    public static void leggPaaAutentisering(JaxWsProxyFactoryBean jaxwsClient, final String brukernavn, final String passord) {
        jaxwsClient.getHandlers().add(new StelvioHeaderHandler());
        Map<String, Object> usernametokenprops = new HashMap<>();
        usernametokenprops.put("action", "UsernameToken");
        usernametokenprops.put("user", brukernavn);
        usernametokenprops.put("passwordType", "PasswordText");
        usernametokenprops.put("passwordCallbackRef", new CallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                ((WSPasswordCallback) callbacks[0]).setPassword(passord);
            }
        });
        jaxwsClient.getOutInterceptors().add(new WSS4JOutInterceptor(usernametokenprops));
    }

    static class StelvioHeaderHandler implements SOAPHandler<SOAPMessageContext> {

        @Override
        public boolean handleMessage(SOAPMessageContext context) {
            SOAPMessage msg = context.getMessage();
            Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            if (outbound) {
                try {
                    SOAPEnvelope env = msg.getSOAPPart().getEnvelope();
                    SOAPHeader header = msg.getSOAPHeader();
                    SOAPElement stelvioContext = header.addHeaderElement(env.createName("StelvioContext", "sc", "http://www.nav.no/StelvioContextPropagation"));
                    SOAPElement applicationId = stelvioContext.addChildElement(env.createName("applicationId"));
                    applicationId.setValue("BD09");
                    SOAPElement userId = stelvioContext.addChildElement(env.createName("userId"));
                    userId.setValue("Z900001");
                    SOAPElement correlationId = stelvioContext.addChildElement(env.createName("correlationId"));
                    correlationId.setValue("4LQA7VQPH2YQEU1R5TYU");
                    msg.saveChanges();
                } catch (SOAPException e) {
                    return false;
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
            return Collections.emptySet();
        }
    }
}
