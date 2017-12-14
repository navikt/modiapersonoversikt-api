package no.nav.sbl.dialogarena.sak.mock;

import no.nav.sbl.dialogarena.saksoversikt.service.service.DokumentMetadataService;
import no.nav.tjeneste.virksomhet.journal.v2.informasjon.*;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class JoarkMock {

    public static final String PERSON_NAVN = "TestPerson";
    public static final String PERSON_FNR = "12345678901";

    public static final String TREDJEPERSON_NAVN = "Doktor Proktor";
    public static final String FALLBACK_NAVN = "Fallbacknavn";
    public static final String TREDJEPERSON_FNR = "09876543212";

    public static final String BEDRIFT_NAVN = "Testbedrift";
    public static final String BEDRIFT_ORGNR = "098743212";

    public static WSJournalpost navMottattDokumentFraBruker() throws DatatypeConfigurationException {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("1");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(brukerenSelv());
        journalpost.setGjelderSak(lagJoarkSak("1"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning("I"));
        journalpost.setMottatt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(15).toGregorianCalendar()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4562", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoved.tittel", "123Hoved", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4563", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg1.tittel", "234VED", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4564", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg2.tittel", "235VED", new ArrayList<>()));

        return journalpost;
    }

    public static WSJournalpost navMottattDokumentFraBedrift() throws DatatypeConfigurationException {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("1");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(tredjePartsOrganisasjon());
        journalpost.setGjelderSak(lagJoarkSak("1"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning("I"));
        journalpost.setMottatt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(15).toGregorianCalendar()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4562", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoved.tittel", "123Hoved", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4563", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg1.tittel", "234VED", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4564", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg2.tittel", "235VED", new ArrayList<>()));

        return journalpost;
    }

    public static WSJournalpost navMottattDokumentFraUkjent() throws DatatypeConfigurationException {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("1");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(ingen());
        journalpost.setEksternPartNavn("");
        journalpost.setGjelderSak(lagJoarkSak("1"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning("I"));
        journalpost.setMottatt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(15).toGregorianCalendar()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4562", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoved.tittel", "123Hoved", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4563", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg1.tittel", "234VED", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4564", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg2.tittel", "235VED", new ArrayList<>()));
        return journalpost;
    }



    public static WSJournalpost brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg() throws DatatypeConfigurationException {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("2");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(tredjePartsPerson());
        journalpost.setGjelderSak(lagJoarkSak("2"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning("U"));
        journalpost.setSendt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(15).toGregorianCalendar()));
        journalpost.setFerdigstilt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(14).toGregorianCalendar()));
        journalpost.getDokumentinfoRelasjonListe()
                .add(lagDokumentinfoRelasjons("awd31", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoveddokument.tittel", "5632HOVED", asList(
                        lagSkannetInnhold("23", "info"),
                        lagSkannetInnhold("34", "annenInfo")
                )));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("awd32", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg1.tittel", "231VED", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("awd33", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg2.tittel", "453VED", new ArrayList<>()));


        return journalpost;
    }

    public static WSJournalpost internDokumentinfoRelasjonListe() throws DatatypeConfigurationException {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("1");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(tredjePartsOrganisasjon());
        journalpost.setGjelderSak(lagJoarkSak("1"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning("N"));
        journalpost.setFerdigstilt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(15).toGregorianCalendar()));
        journalpost.getDokumentinfoRelasjonListe()
                .add(lagDokumentinfoRelasjons("awd31", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoveddokument.tittel", "5632HOVED", asList(
                        lagSkannetInnhold("23", "info"),
                        lagSkannetInnhold("34", "annenInfo")
                )));
        return journalpost;
    }

    public static WSJournalpost eksternDokumentinfoRelasjonListe(String saksId) throws DatatypeConfigurationException {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("1");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(tredjePartsOrganisasjon());
        journalpost.setGjelderSak(lagJoarkSak(saksId));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning("I"));
        journalpost.setMottatt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(15).toGregorianCalendar()));
        journalpost.getDokumentinfoRelasjonListe()
                .add(lagDokumentinfoRelasjons("awd31", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoveddokument.tittel", "5632HOVED", asList(
                        lagSkannetInnhold("23", "info"),
                        lagSkannetInnhold("34", "annenInfo")
                )));
        return journalpost;
    }

    private static WSSkannetInnhold lagSkannetInnhold(String id, String vedleggInnhold) {
        WSSkannetInnhold skannetInnhold = new WSSkannetInnhold();
        skannetInnhold.setSkannetInnholdId(id);
        skannetInnhold.setVedleggInnhold(vedleggInnhold);
        return skannetInnhold;

    }

    private static WSAktoer tredjePartsPerson() {
        return new WSPerson().withIdent(TREDJEPERSON_FNR).withNavn(TREDJEPERSON_NAVN);
    }

    private static WSAktoer tredjePartsOrganisasjon() {
        return new WSOrganisasjon().withOrgnummer(BEDRIFT_ORGNR).withNavn(BEDRIFT_NAVN);
    }

    private static WSAktoer brukerenSelv() {

        return new WSPerson().withIdent(PERSON_FNR).withNavn(PERSON_NAVN);
    }

    private static WSAktoer ingen() {
        return null;
    }

    public static WSJournalpost navSendtDokumentTilEksternPart() {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("3");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(tredjePartsPerson());
        journalpost.setGjelderSak(lagJoarkSak("2"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning(DokumentMetadataService.JOURNALPOST_UTGAAENDE));
        try {
            journalpost.setSendt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(14).toGregorianCalendar()));
            journalpost.setFerdigstilt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(14).toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        journalpost.getDokumentinfoRelasjonListe()
                .add(lagDokumentinfoRelasjons("awd31", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoveddokument.tittel", "5632HOVED", new ArrayList<>()));
        return journalpost;
    }

    public static WSJournalpost dokumentUtenEksternPartMedFallbackNavn() {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("3");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(null);
        journalpost.setEksternPartNavn(FALLBACK_NAVN);
        journalpost.setGjelderSak(lagJoarkSak("2"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning(DokumentMetadataService.JOURNALPOST_UTGAAENDE));
        try {
            journalpost.setSendt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(14).toGregorianCalendar()));
            journalpost.setFerdigstilt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(14).toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        journalpost.getDokumentinfoRelasjonListe()
                .add(lagDokumentinfoRelasjons("awd31", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoveddokument.tittel", "5632HOVED", new ArrayList<>()));
        return journalpost;
    }

    public static WSJournalpost dokumentUkjentAvsender() {
        WSJournalpost journalpost = new WSJournalpost();
        journalpost.setJournalpostId("3");
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart(null);
        journalpost.setEksternPartNavn("");
        journalpost.setGjelderSak(lagJoarkSak("2"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning(DokumentMetadataService.JOURNALPOST_UTGAAENDE));
        try {
            journalpost.setSendt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(14).toGregorianCalendar()));
            journalpost.setFerdigstilt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(14).toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        journalpost.getDokumentinfoRelasjonListe()
                .add(lagDokumentinfoRelasjons("awd31", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoveddokument.tittel", "5632HOVED", new ArrayList<>()));
        return journalpost;
    }

    private static WSKommunikasjonsretninger lagKommunikasjonsrettning(String retning) {
        WSKommunikasjonsretninger kommunikasjonsretninger = new WSKommunikasjonsretninger();
        kommunikasjonsretninger.setValue(retning);
        return kommunikasjonsretninger;
    }

    private static WSDokumentinfoRelasjon lagDokumentinfoRelasjons(String relasjonsId, String dokumentType, String tittel, String dokumentReferanseId, List<WSSkannetInnhold> skannetInnhold) {
        WSDokumentinfoRelasjon dokumentinfoRelasjon = new WSDokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(lagJournalfortDokumentInfo(tittel, dokumentReferanseId, skannetInnhold));
        dokumentinfoRelasjon.setDokumentinfoRelasjonId(relasjonsId);
        dokumentinfoRelasjon.setDokumentTilknyttetJournalpost(lagDokument(dokumentType));
        dokumentinfoRelasjon.getJournalfoertDokument().setKategori(new WSKatagorier().withValue("INTERN_NOTAT"));
        return dokumentinfoRelasjon;
    }

    private static WSTilknyttetJournalpostSom lagDokument(String dokumentType) {
        WSTilknyttetJournalpostSom dokument = new WSTilknyttetJournalpostSom();
        dokument.setValue(dokumentType);

        return dokument;
    }

    private static WSJournalfoertDokumentInfo lagJournalfortDokumentInfo(String tittel, String id, List<WSSkannetInnhold> skannetInnhold) {
        WSJournalfoertDokumentInfo journalfoertDokumentInfo = new WSJournalfoertDokumentInfo();
        journalfoertDokumentInfo.setTittel(tittel);
        journalfoertDokumentInfo.setDokumentId(id);
        journalfoertDokumentInfo.getSkannetInnholdListe().addAll(skannetInnhold);
        journalfoertDokumentInfo.setKategori(new WSKatagorier().withValue("kategori"));

        return journalfoertDokumentInfo;
    }


    private static WSDokumentInnhold lagDokumentInnhold(String filtype, String variantFormat) {
        WSDokumentInnhold dokumentInnhold = new WSDokumentInnhold();
        dokumentInnhold.setVariantformat(lagVariantFormater(variantFormat));
        dokumentInnhold.setFiltype(lagArkivfiltyper(filtype));

        return dokumentInnhold;
    }

    private static WSArkivfiltyper lagArkivfiltyper(String filtype) {
        WSArkivfiltyper arkivfiltyper = new WSArkivfiltyper();
        arkivfiltyper.setValue(filtype);

        return arkivfiltyper;
    }

    private static WSVariantformater lagVariantFormater(String variant) {
        WSVariantformater variantformater = new WSVariantformater();
        variantformater.setValue(variant);

        return variantformater;
    }

    private static WSRegistertSak lagJoarkSak(String saksId) {
        WSRegistertSak sak = new WSRegistertSak();
        sak.setSakId(saksId);
        return sak;
    }

    private static WSArkivtemaer lagArkivtema(String temakode) {
        WSArkivtemaer arkivtemaer = new WSArkivtemaer();
        arkivtemaer.setValue(temakode);
        return arkivtemaer;
    }
}
