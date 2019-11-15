package no.nav.kjerneinfo.consumer.fim.person.mock;

import no.nav.tjeneste.virksomhet.person.v3.*;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSNorskIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSPeriode;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSPersonIdent;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.WSVerge;
import no.nav.tjeneste.virksomhet.person.v3.meldinger.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

public class PersonV3Mock implements PersonV3 {

    @Override
    public WSHentPersonResponse hentPerson(WSHentPersonRequest wsHentPersonRequest) throws HentPersonSikkerhetsbegrensning, HentPersonPersonIkkeFunnet {
        boolean genererKomplettBruker = true;
        return new WSHentPersonResponse().withPerson(new PersonKjerneinfoMockFactory().getBruker("10108000398", genererKomplettBruker));
    }

    @Override
    public WSHentGeografiskTilknytningResponse hentGeografiskTilknytning(WSHentGeografiskTilknytningRequest wsHentGeografiskTilknytningRequest) throws
            HentGeografiskTilknytningSikkerhetsbegrensing, HentGeografiskTilknytningPersonIkkeFunnet {
        return null;
    }

    @Override
    public WSHentSikkerhetstiltakResponse hentSikkerhetstiltak(WSHentSikkerhetstiltakRequest wsHentSikkerhetstiltakRequest) throws HentSikkerhetstiltakPersonIkkeFunnet {
        return null;
    }

    @Override
    public void ping() {

    }

    @Override
    public WSHentPersonnavnBolkResponse hentPersonnavnBolk(WSHentPersonnavnBolkRequest wsHentPersonnavnBolkRequest) {
        return null;
    }

    @Override
    public WSHentVergeResponse hentVerge(WSHentVergeRequest request) {
        WSVerge verge = new WSVerge().withVerge(new WSPersonIdent().withIdent(new WSNorskIdent().withIdent("10108000398")))
                .withVirkningsperiode(new WSPeriode()
                        .withFom(getXMLGregorianCalendarNow())
                        .withTom(getXMLGregorianCalendarNow()));
        return new WSHentVergeResponse()

                .withVergeListe(verge);
    }

    private XMLGregorianCalendar getXMLGregorianCalendarNow() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory;
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw(new RuntimeException(e));
        }
        XMLGregorianCalendar now = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        return now;
    }

}
