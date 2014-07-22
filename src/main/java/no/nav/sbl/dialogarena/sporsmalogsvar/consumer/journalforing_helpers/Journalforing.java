package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing_helpers;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.*;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

public abstract class Journalforing {

    public static final String HOVEDDOKUMENT = "hovedDokument";
    public static final String SPORSMAL_OG_SVAR = "SPORSMAL_OG_SVAR";
    public static final String KATEGORI_KODE_ES = "ES";
    public static final String DOKUTYPE_INNGAENDE = "melding";
    public static final String DOKUTYPE_UTGAENDE = "utgående brev";

    protected static Person lagPerson(String fnr) {
        // TODO Få tak i kodeverk og sett det inn i denne metoden, norskident har flere felter
        Person bruker = new Person();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(fnr);
        bruker.setIdent(norskIdent);
        return bruker;
    }



    protected static Kryssreferanse lagKryssreferanse(String journalfortPostIdForTilhorendeSporsmal) {
        Kryssreferanse kryssreferanse = new Kryssreferanse();
        kryssreferanse.setReferanseId(journalfortPostIdForTilhorendeSporsmal);
        kryssreferanse.setReferansekode("SPOERSMAAL");
        return kryssreferanse;
    }

    protected static Kommunikasjonskanaler lagKommunikasjonskanaler() {
        Kommunikasjonskanaler kommunikasjonskanaler = new Kommunikasjonskanaler();
        kommunikasjonskanaler.setValue("Elektronisk");
        kommunikasjonskanaler.setKodeverksRef("http://nav.no/kodeverk/Kodeverk/Kommunikasjonskanaler");
        return kommunikasjonskanaler;
    }

    protected static Arkivtemaer lagArkivtema(Sak sak) {
        // TODO Få tak i kodeverk og sett det inn i denne metoden
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(sak.tema);
        arkivtemaer.setKodeverksRef("");
        arkivtemaer.setKodeRef("");
        return arkivtemaer;
    }

    protected static Signatur lagSignatur() {
        Signatur signatur = new Signatur();
        signatur.setSignert(false);
        return signatur;
    }

    public static class SakToJournalforingSak implements Transformer<Sak, no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak> {
        public static final SakToJournalforingSak INSTANCE = new SakToJournalforingSak();

        @Override
        public no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak transform(Sak sak) {
            no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalSak = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak();
            journalSak.setSaksId(sak.saksId);
            journalSak.setFagsystemkode(sak.fagsystem);
            return journalSak;
        }
    }

    public static class PdfDokumentToUstrukturertInnholdConverter implements Transformer<byte[], UstrukturertInnhold> {
        public static final PdfDokumentToUstrukturertInnholdConverter INSTANCE = new PdfDokumentToUstrukturertInnholdConverter();

        @Override
        public UstrukturertInnhold transform(byte[] pdf) {
            UstrukturertInnhold dokumentInnhold = new UstrukturertInnhold();
            // TODO få inn den egentlige tittelen her
            dokumentInnhold.setFilnavn("Dokumenttittel");
            // TODO Få tak i kodeverk og sett det inn i denne metoden
            Arkivfiltyper arkivFilTyper = new Arkivfiltyper();
            arkivFilTyper.setValue("PDF");

            dokumentInnhold.setFiltype(arkivFilTyper);
            // TODO Få tak i kodeverk og sett det inn i denne metoden
            Variantformater variansformat = new Variantformater();
            variansformat.setValue("ARKIV");
            dokumentInnhold.setVariantformat(variansformat);

            dokumentInnhold.setInnhold(pdf);
            return dokumentInnhold;
        }
    }

    public static class DateTimeToXmlGregorianCalendarConverter implements Transformer<DateTime, XMLGregorianCalendar> {
        public static DateTimeToXmlGregorianCalendarConverter INSTANCE = new DateTimeToXmlGregorianCalendarConverter();

        @Override
        public XMLGregorianCalendar transform(DateTime dateTime) {
            GregorianCalendar dokumentDato = new GregorianCalendar();
            dokumentDato.setTime(dateTime.toDate());
            try {
                return (DatatypeFactory.newInstance().newXMLGregorianCalendar(dokumentDato));
            } catch (DatatypeConfigurationException e) {
                throw new RuntimeException("Noe gikk galt ved instansiering av XMLGregorianCalendar", e);
            }
        }
    }
}
