package no.nav.sbl.dialogarena.saksoversikt.service.service;

import no.nav.sbl.dialogarena.common.kodeverk.Kodeverk;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import no.nav.tjeneste.virksomhet.innsynjournal.v1.informasjon.*;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.BulletproofKodeverkService.ARKIVTEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.optional;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Konstanter.DAGPENGER;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType.SOKNADSINNSENDING;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.HOVEDSKJEMA;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.KODEVERK_REF;
import static org.hamcrest.core.Is.is;
import static org.joda.time.DateTime.now;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DokumentMetadataServiceTest {

    @Mock
    InnsynJournalService innsynJournalService;

    @Mock
    HenvendelseService henvendelseService;

    @Mock
    BulletproofKodeverkService bulletproofKodeverkService;

    @Mock
    Kodeverk kodeverk;

    @InjectMocks
    DokumentMetadataService dokumentMetadataService;

    @Test
    public void oversetterJournalposterTilDokumentMetadata() throws DatatypeConfigurationException {
        mockJoark(navMottattDokumentFraBruker());

        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(new ArrayList<>());

        List<DokumentMetadata> dokumentMetadata = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), anyString());

        assertThat(dokumentMetadata.size(), is(1));
    }

    @Test
    public void oversettJournalpostMedLogiskeOgVanligeVedlegg() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");

        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(new ArrayList<>());

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), anyString());

        assertThat(dokumentMetadatas.size(), is(1));
        assertThat(dokumentMetadatas.get(0).getVedlegg().size(), is(4));
    }

    @Test
    public void hvisViFaarSammeJournalpostFraHenvendelseOgJoarkSkalViBrukeInformasjonenFraJoark() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(singletonList(lagHenvendelse("2")));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), anyString());

        assertThat(dokumentMetadatas.size(), is(1));
    }

    @Test
    public void hvisViFaarJournalpostFraHenvendelseSomIkkeFinnesIJoarkSkalDenneBrukesVidere() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(singletonList(lagHenvendelse("En annen journalpost")));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), anyString());

        assertThat(dokumentMetadatas.size(), is(2));
    }

    @Test
    public void hvisViFaarSammeJournalpostFraHenvendelseOgJoarkSkalViBrukeInformasjonenFraJoarkMenTaMedInformasjonOmEttersendelseFraHenvendelse() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        String SAMME_JOURNALPOST = "2";
        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(singletonList(lagHenvendelse(SAMME_JOURNALPOST)));

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), anyString());

        assertThat(dokumentMetadatas.size(), is(1));
        assertTrue(dokumentMetadatas.get(0).isEttersending());
    }

    @Test
    public void hvisViBareFaarJournalpostFraJoarkSkalIkkeDokumentetSettesTilFiktivtDokument() throws DatatypeConfigurationException {
        mockJoark(brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg());

        when(bulletproofKodeverkService.getTemanavnForTemakode(DAGPENGER, ARKIVTEMA)).thenReturn("Dagpenger");
        when(kodeverk.getTittel("NAV 14-05.00")).thenReturn("Soknad om foreldrepenger");

        when(henvendelseService.hentHenvendelsessoknaderMedStatus(any(), anyString())).thenReturn(emptyList());

        List<DokumentMetadata> dokumentMetadatas = dokumentMetadataService.hentDokumentMetadata(new ArrayList<>(), anyString());

        assertFalse(dokumentMetadatas.get(0).isEttersending());
    }

    private void mockJoark(Journalpost... jp){
        when(innsynJournalService.joarkSakhentTilgjengeligeJournalposter(any()))
                .thenReturn(optional(asList(jp).stream()));
    }

    private Record<Soknad> lagHenvendelse(String journalpostId) {
        return new Record<Soknad>()
                .with(Soknad.JOURNALPOST_ID, journalpostId)
                .with(Soknad.BEHANDLINGS_ID, "12345")
                .with(Soknad.BEHANDLINGSKJEDE_ID, "98765")
                .with(Soknad.STATUS, Soknad.HenvendelseStatus.FERDIG)
                .with(Soknad.OPPRETTET_DATO, now())
                .with(Soknad.INNSENDT_DATO, now())
                .with(Soknad.SISTENDRET_DATO, now())
                .with(Soknad.SKJEMANUMMER_REF, "NAV---")
                .with(Soknad.ETTERSENDING, true)
                .with(Soknad.TYPE, SOKNADSINNSENDING)
                .with(Soknad.DOKUMENTER, singletonList(new Record<Dokument>()
                        .with(HOVEDSKJEMA, true)
                        .with(KODEVERK_REF, "NAV 14-05.00")
                ));
    }

    @Test
    public void finnerTittelIVedlegg() throws DatatypeConfigurationException {
        Optional<String> s = dokumentMetadataService.finnTittelForDokumentReferanseIJournalpost(navMottattDokumentFraBruker(), "234VED");

        assertEquals(s.get(), "Vedlegg1.tittel");
    }

    @Test
    public void finnerTittelIHovedDokument() throws DatatypeConfigurationException {
        Optional<String> s = dokumentMetadataService.finnTittelForDokumentReferanseIJournalpost(navMottattDokumentFraBruker(), "123Hoved");

        assertEquals(s.get(), "Hoved.tittel");
    }

    @Test
    public void returnererTomOptionalOmDokumentReferanseIdIkkeFinnes() throws DatatypeConfigurationException {
        Optional<String> s = dokumentMetadataService.finnTittelForDokumentReferanseIJournalpost(navMottattDokumentFraBruker(), "NOE_SOM_IKKE_FINNES");

        assertFalse(s.isPresent());
    }



    private static Journalpost navMottattDokumentFraBruker() throws DatatypeConfigurationException {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId("1");
        journalpost.setBrukerErAvsenderMottaker(AvsenderMottaker.JA);
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart("Stig");
        journalpost.setGjelderSak(lagJoarkSak("1"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning("I"));
        journalpost.setMottatt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(15).toGregorianCalendar()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4562", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoved.tittel", "123Hoved", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4563", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg1.tittel", "234VED", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("4564", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg2.tittel", "235VED", new ArrayList<>()));

        return journalpost;
    }

    private static Journalpost brukerMottattDokumentFraNavMedLogiskeOgVanligeVedlegg() throws DatatypeConfigurationException {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId("2");
        journalpost.setBrukerErAvsenderMottaker(AvsenderMottaker.JA);
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart("");
        journalpost.setGjelderSak(lagJoarkSak("2"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning("U"));
        journalpost.setSendt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(15).toGregorianCalendar()));
        journalpost.getDokumentinfoRelasjonListe()
                .add(lagDokumentinfoRelasjons("awd31", DokumentMetadataService.DOKTYPE_HOVEDDOKUMENT, "Hoveddokument.tittel", "5632HOVED", asList(
                        lagSkannetInnhold("23", "info"),
                        lagSkannetInnhold("34", "annenInfo")
                )));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("awd32", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg1.tittel", "231VED", new ArrayList<>()));
        journalpost.getDokumentinfoRelasjonListe().add(lagDokumentinfoRelasjons("awd33", DokumentMetadataService.DOKTYPE_VEDLEGG, "Vedlegg2.tittel", "453VED", new ArrayList<>()));


        return journalpost;
    }

    private static SkannetInnhold lagSkannetInnhold(String id, String vedleggInnhold) {
        SkannetInnhold skannetInnhold = new SkannetInnhold();
        skannetInnhold.setSkannetInnholdId(id);
        skannetInnhold.setVedleggInnhold(vedleggInnhold);
        return skannetInnhold;

    }

    private static Journalpost navSendtDokumentTilEksternPart() {
        Journalpost journalpost = new Journalpost();
        journalpost.setJournalpostId("3");
        journalpost.setBrukerErAvsenderMottaker(AvsenderMottaker.NEI);
        journalpost.setArkivtema(lagArkivtema("DAG"));
        journalpost.setEksternPart("Dr. Mikkelsen");
        journalpost.setGjelderSak(lagJoarkSak("2"));
        journalpost.setKommunikasjonsretning(lagKommunikasjonsrettning(DokumentMetadataService.JOURNALPOST_UTGAAENDE));
        try {
            journalpost.setSendt(DatatypeFactory.newInstance().newXMLGregorianCalendar(new DateTime().minusDays(14).toGregorianCalendar()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return journalpost;
    }

    private static Kommunikasjonsretninger lagKommunikasjonsrettning(String retning) {
        Kommunikasjonsretninger kommunikasjonsretninger = new Kommunikasjonsretninger();
        kommunikasjonsretninger.setValue(retning);
        return kommunikasjonsretninger;
    }

    private static DokumentinfoRelasjon lagDokumentinfoRelasjons(String relasjonsId, String dokumentType, String tittel, String dokumentReferanseId, List<SkannetInnhold> skannetInnhold) {
        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        dokumentinfoRelasjon.setJournalfoertDokument(lagJournalfortDokumentInfo(tittel, dokumentReferanseId, skannetInnhold));
        dokumentinfoRelasjon.setDokumentinfoRelasjonId(relasjonsId);
        dokumentinfoRelasjon.setDokumentTilknyttetJournalpost(lagDokument(dokumentType));

        return dokumentinfoRelasjon;
    }

    private static TilknyttetJournalpostSom lagDokument(String dokumentType) {
        TilknyttetJournalpostSom dokument = new TilknyttetJournalpostSom();
        dokument.setValue(dokumentType);

        return dokument;
    }

    private static JournalfoertDokumentInfo lagJournalfortDokumentInfo(String tittel, String id, List<SkannetInnhold> skannetInnhold) {
        JournalfoertDokumentInfo journalfoertDokumentInfo = new JournalfoertDokumentInfo();
        journalfoertDokumentInfo.setTittel(tittel);
        journalfoertDokumentInfo.setDokumentId(id);
        journalfoertDokumentInfo.setBeskriverInnhold(lagDokumentInnhold("filtype", "variantFormat"));
        journalfoertDokumentInfo.setInnsynDokument(InnsynDokument.JA);
        journalfoertDokumentInfo.getSkannetInnholdListe().addAll(skannetInnhold);

        return journalfoertDokumentInfo;
    }


    private static DokumentInnhold lagDokumentInnhold(String filtype, String variantFormat) {
        DokumentInnhold dokumentInnhold = new DokumentInnhold();
        dokumentInnhold.setVariantformat(lagVariantFormater(variantFormat));
        dokumentInnhold.setFiltype(lagArkivfiltyper(filtype));

        return dokumentInnhold;
    }

    private static Arkivfiltyper lagArkivfiltyper(String filtype) {
        Arkivfiltyper arkivfiltyper = new Arkivfiltyper();
        arkivfiltyper.setValue(filtype);

        return arkivfiltyper;
    }

    private static Variantformater lagVariantFormater(String variant) {
        Variantformater variantformater = new Variantformater();
        variantformater.setValue(variant);

        return variantformater;
    }

    private static Sak lagJoarkSak(String saksId) {
        Sak sak = new Sak();
        sak.setSakId(saksId);
        return sak;
    }

    private static Arkivtemaer lagArkivtema(String temakode) {
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(temakode);
        return arkivtemaer;
    }
}
