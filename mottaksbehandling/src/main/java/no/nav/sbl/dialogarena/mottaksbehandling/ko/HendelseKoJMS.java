package no.nav.sbl.dialogarena.mottaksbehandling.ko;

import no.nav.melding.virksomhet.hendelse.v1.Hendelse;
import no.nav.sbl.dialogarena.common.integrasjonsutils.JMS;
import no.nav.sbl.dialogarena.mottaksbehandling.verktoy.JAXBMarshalling;
import no.nav.sbl.dialogarena.types.Pingable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.QueueConnectionFactory;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class HendelseKoJMS implements HendelseKo {
	
	private final Destination inn, ut;
	private final QueueConnectionFactory connFactory;
	
	public HendelseKoJMS(QueueConnectionFactory connFactory, Destination inn, Destination ut) {
		this.connFactory = connFactory;
		this.inn = inn;
		this.ut = ut;
	}

	@Override
	public Hendelse plukk() {
	    String message = JMS.getTextMessage(connFactory, inn, 0);
        return JAXBMarshalling.unmarshal(message, Hendelse.class);
	}

	@Override
	public void put(Hendelse hendelse) {
		QName namespace = new QName("http://nav.no/melding/virksomhet/hendelse/behandling/status/v1", hendelse.getClass().getSimpleName());
		JAXBElement<Hendelse> jaxbelement = new JAXBElement<>(namespace, Hendelse.class, hendelse);
		String melding = JAXBMarshalling.marshall(jaxbelement);
		JMS.sendTextMessage(connFactory, ut, melding);
	}

    @Override
    public Pingable.Ping ping() {
        try {
            connFactory.createQueueConnection().close();
            return Pingable.Ping.lyktes("MOTTAKSBEHANDLING_JMS_OK");
        } catch (JMSException e) {
            return Pingable.Ping.feilet("MOTTAKSBEHANDLING_JMS_ERROR", e);
        }
    }
}
