package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforinghelpers;

import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.*;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

public abstract class Journalforing {

    public static final String HOVEDDOKUMENT = "HOVEDDOKUMENT";
    public static final String BREVKODE_SPORSMAL_OG_SVAR = "SP_OG_SVAR";
    public static final String KATEGORI_ELEKTRONISK_DIALOG = "ED";

    protected static Person lagPerson(String fnr) {
        Person bruker = new Person();
        NorskIdent norskIdent = new NorskIdent();
        Personidenter personidenter = new Personidenter();
        personidenter.setKodeRef(personidenter.getKodeverksRef());
        //TODO Her må man skille mellom fnr og dnr når man setter verdien til kodeverket, foreløpig hardkodet til FNR
        personidenter.setValue("FNR");
        norskIdent.setIdent(fnr);
        norskIdent.setType(personidenter);
        bruker.setIdent(norskIdent);
        return bruker;
    }

    protected static EksternPart lagEksternPart(Person bruker) {
        EksternPart eksternPart = new EksternPart();
        eksternPart.setEksternAktoer(bruker);
        return eksternPart;
    }

    protected static Kryssreferanse lagKryssreferanse(String journalfortPostIdForTilhorendeSporsmal) {
        Kryssreferanse kryssreferanse = new Kryssreferanse();
        kryssreferanse.setReferanseId(journalfortPostIdForTilhorendeSporsmal);
        kryssreferanse.setReferansekode("DIALOG_REKKE");
        return kryssreferanse;
    }

    protected static Kommunikasjonskanaler lagKommunikasjonskanaler() {
        Kommunikasjonskanaler kommunikasjonskanaler = new Kommunikasjonskanaler();
        kommunikasjonskanaler.setValue("NAV_NO");
        return kommunikasjonskanaler;
    }

    protected static Arkivtemaer lagArkivtema(Sak sak) {
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(sak.tema);
        // TODO arkivtema har to felter knyttet til koderef, den ene Kodeverkref settes automatisk(i get-metoden), mens det andre er foreløpig satt til det samme
        arkivtemaer.setKodeRef(arkivtemaer.getKodeverksRef());
        return arkivtemaer;
    }

    protected static Signatur lagSignatur() {
        Signatur signatur = new Signatur();
        signatur.setSignert(true);
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
            Arkivfiltyper arkivFilTyper = new Arkivfiltyper();
            arkivFilTyper.setValue("PDF");
            dokumentInnhold.setFiltype(arkivFilTyper);
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
