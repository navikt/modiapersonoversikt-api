package no.nav.sbl.dialogarena.modiabrukerdialog.mock.config.endpoints;

import no.nav.virksomhet.tjenester.sak.v1.Sak;

@javax.jws.WebService(targetNamespace = "http://nav.no/virksomhet/tjenester/sak/v1", name = "Sak")
@javax.xml.bind.annotation.XmlSeeAlso({no.nav.virksomhet.tjenester.sak.v1.ObjectFactory.class, no.nav.virksomhet.gjennomforing.sak.v1.ObjectFactory.class, no.nav.virksomhet.tjenester.sak.meldinger.v1.ObjectFactory.class})
public interface SakBugfix extends Sak{

   @javax.jws.WebResult(name = "response", targetNamespace = "")
    @javax.xml.ws.RequestWrapper(localName = "finnGenerellSakListe", targetNamespace = "http://nav.no/virksomhet/tjenester/sak/v1", className = "no.nav.virksomhet.tjenester.sak.v1.FinnGenerellSakListe")
    @javax.jws.WebMethod
    @javax.xml.ws.ResponseWrapper(localName = "finnGenerellSakListeResponse", targetNamespace = "http://nav.no/virksomhet/tjenester/sak/v1", className = "no.nav.virksomhet.tjenester.sak.v1.FinnGenerellSakListeResponse")
    no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse finnGenerellSakListe(@javax.jws.WebParam(name = "request", targetNamespace = "http://nav.no/virksomhet/tjenester/sak/v1") no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest wsFinnGenerellSakListeRequest);
}