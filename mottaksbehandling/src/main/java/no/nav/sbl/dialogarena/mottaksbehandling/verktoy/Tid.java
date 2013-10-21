package no.nav.sbl.dialogarena.mottaksbehandling.verktoy;

import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Tid {

    public static XMLGregorianCalendar gregorianNow() {
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().toGregorianCalendar());
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
