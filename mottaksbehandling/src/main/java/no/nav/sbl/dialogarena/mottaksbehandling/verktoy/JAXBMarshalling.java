package no.nav.sbl.dialogarena.mottaksbehandling.verktoy;

import no.nav.melding.virksomhet.hendelse.v1.Hendelse;
import no.nav.melding.virksomhet.henvendelsebehandling.behandlingsresultat.v1.XMLHenvendelse;
import no.nav.melding.virksomhet.henvendelsebehandling.behandlingsresultat.v1.XMLSuperwrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public class JAXBMarshalling {

	public static final JAXBContext CONTEXT;
	
	static {
		try {
			CONTEXT = JAXBContext.newInstance(Hendelse.class, XMLHenvendelse.class, XMLSuperwrapper.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> String marshall(Object element) {
		try {
			StringWriter writer = new StringWriter();
			Marshaller marshaller = CONTEXT.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(element, new StreamResult(writer));
			return writer.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T unmarshal(String melding, Class<T> elementClass) {
		try {
			Object value = CONTEXT.createUnmarshaller().unmarshal(new StringReader(melding));
			return value instanceof JAXBElement ? ((JAXBElement<T>) value).getValue() : (T) value;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
}