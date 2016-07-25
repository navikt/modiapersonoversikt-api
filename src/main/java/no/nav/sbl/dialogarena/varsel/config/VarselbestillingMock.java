package no.nav.sbl.dialogarena.varsel.config;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.AktoerId;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Person;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varsel;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.informasjon.Varselbestilling;
import org.joda.time.DateTime;

import java.util.List;

public class VarselbestillingMock extends Varselbestilling {

    public VarselbestillingMock(List<Varsel> varselliste) {
        DateTime now = DateTime.now();
        AktoerId nyAktoerId = new AktoerId();
        nyAktoerId.setAktoerId("321654987");

        Person nyPerson = new Person();
        nyPerson.setIdent("***REMOVED***");

        varseltypeId = "MOTE";
        aktoerId = nyAktoerId;
        person = nyPerson;
        bestilt = new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar());
        varselListe = varselliste;
        sisteVarselutsendelse = new XMLGregorianCalendarImpl(now.minusMinutes(60).toGregorianCalendar());

    }

}
