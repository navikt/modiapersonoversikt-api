package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Pdf;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Arkivfiltyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Arkivtemaer;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.DokumentInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Dokumenttyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.EksternPart;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Kommunikasjonskanaler;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Kryssreferanse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.NorskIdent;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Person;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Personidenter;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Signatur;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.UstrukturertInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Variantformater;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.List;

public abstract class Journalforing {

    public static final String HOVEDDOKUMENT = "HOVEDDOKUMENT";
    public static final String KATEGORI_ELEKTRONISK_DIALOG = "ELEKTRONISK_DIALOG";

    public static final String BREVKODE_SPORSMAL = "900021";
    public static final String BREVKODE_SVAR = "GEN_SVAR_001";
    public static final String BREVKODE_NOTAT = "GEN_NOT_003";

    public static final String KRYSSREFERANSE_KODE = "SPOERSMAAL";
    public static final String ARKIV_FILTYPE = "PDF";
    public static final String VARIANSFORMAT = "ARKIV";
    public static final String KOMMUNIKASJONSKANAL = "NAV_NO";
    public static final String INNHOLD_BESKRIVELSE = "Elektronisk kommunikasjon med NAV ";

    public static final String GSAK_FAGSYSTEMKODE = "FS22";

    protected static Person lagPerson(String fnr) {
        Person bruker = new Person();
        Personidenter personidenter = new Personidenter();
        personidenter.setKodeRef(personidenter.getKodeverksRef());
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(fnr);
        norskIdent.setType(personidenter);

        bruker.setIdent(norskIdent);
        return bruker;
    }

    protected static EksternPart lagEksternPart(Person bruker) {
        EksternPart eksternPart = new EksternPart();
        eksternPart.setEksternAktoer(bruker);
        eksternPart.setNavn(bruker.getIdent().getIdent());
        return eksternPart;
    }

    protected static Kryssreferanse lagKryssreferanse(String journalfortPostIdForTilhorendeSporsmal) {
        Kryssreferanse kryssreferanse = new Kryssreferanse();
        kryssreferanse.setReferanseId(journalfortPostIdForTilhorendeSporsmal);
        kryssreferanse.setReferansekode(KRYSSREFERANSE_KODE);
        return kryssreferanse;
    }

    protected static Kommunikasjonskanaler lagKommunikasjonskanaler() {
        Kommunikasjonskanaler kommunikasjonskanaler = new Kommunikasjonskanaler();
        kommunikasjonskanaler.setValue(KOMMUNIKASJONSKANAL);
        return kommunikasjonskanaler;
    }

    protected static Arkivtemaer lagArkivtema(String tema) {
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(tema);
        arkivtemaer.setKodeRef(arkivtemaer.getKodeverksRef());
        return arkivtemaer;
    }

    protected static Signatur lagSignatur() {
        Signatur signatur = new Signatur();
        signatur.setSignert(true);
        return signatur;
    }

    protected static byte[] lagPdfInnhold(Melding melding) {
        return PdfUtils.genererPdf(melding);
    }

    protected static Dokumenttyper lagDokumenttype(String type) {
        Dokumenttyper dokumenttyper = new Dokumenttyper();
        dokumenttyper.setValue(type);
        if (type.equals(BREVKODE_NOTAT)) {
            dokumenttyper.setKodeRef(dokumenttyper.getKodeverksRef());
        }
        return dokumenttyper;
    }

    protected static void leggBeskriverInnholdTilJournalfortDokumentInfo(List<DokumentInnhold> beskriverInnhold, Pdf pdf) {
        beskriverInnhold.add(PdfDokumentToUstrukturertInnholdConverter.INSTANCE.transform(pdf));
    }

    public static class PdfDokumentToUstrukturertInnholdConverter implements Transformer<Pdf, UstrukturertInnhold> {
        public static final PdfDokumentToUstrukturertInnholdConverter INSTANCE = new PdfDokumentToUstrukturertInnholdConverter();

        @Override
        public UstrukturertInnhold transform(Pdf pdf) {
            UstrukturertInnhold dokumentInnhold = new UstrukturertInnhold();
            dokumentInnhold.setFilnavn(pdf.getDokumenttittel());
            Arkivfiltyper arkivFilTyper = new Arkivfiltyper();
            arkivFilTyper.setValue(ARKIV_FILTYPE);
            dokumentInnhold.setFiltype(arkivFilTyper);
            Variantformater variansformat = new Variantformater();
            variansformat.setValue(VARIANSFORMAT);
            dokumentInnhold.setVariantformat(variansformat);
            dokumentInnhold.setInnhold(pdf.getPdfBytes());
            return dokumentInnhold;
        }
    }

    public static class SakToJournalforingSak implements Transformer<Sak, no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak> {

        public static final SakToJournalforingSak INSTANCE = new SakToJournalforingSak();

        @Override
        public no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak transform(Sak sak) {
            no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalSak = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak();
            journalSak.setSaksId(sak.saksId);
            journalSak.setFagsystemkode(GSAK_FAGSYSTEMKODE);
            return journalSak;
        }

    }

    public static class DateTimeToXmlGregorianCalendarConverter implements Transformer<DateTime, XMLGregorianCalendar> {
        public static final DateTimeToXmlGregorianCalendarConverter INSTANCE = new DateTimeToXmlGregorianCalendarConverter();

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
