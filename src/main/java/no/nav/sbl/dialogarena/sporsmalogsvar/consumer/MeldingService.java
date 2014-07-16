package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.PdfUtils;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Arkivfiltyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Arkivtemaer;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.DokumentInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Dokumenttyper;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.EksternPart;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Kommunikasjonskanaler;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Kryssreferanse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.NorskIdent;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Person;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Signatur;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.UstrukturertInnhold;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Variantformater;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.Journalpost;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerInngaaendeHenvendelseRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerInngaaendeHenvendelseResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerNotatRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerNotatResponse;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerUtgaaendeHenvendelseRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.JournalfoerUtgaaendeHenvendelseResponse;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;

public class MeldingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;

    @Inject
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;

    @Inject
    private no.nav.virksomhet.tjenester.sak.v1.Sak sakWs;

    @Inject
    BehandleJournalV2 behandleJournalV2;

    private static final String MODIA_SYSTEM_ID = "BD06";
    private static final String HOVEDDOKUMENT = "hovedDokument";
    private static final String SPORSMAL_OG_SVAR = "SPORSMAL_OG_SVAR";
    private static final String DOKUTYPE_MELDING = "melding";
    private static final String DOKUTYPE_UTGAENDE = "utgående brev";
    private static final String KATEGORI_KODE_ES = "ES";

    public List<Melding> hentMeldinger(String fnr) {
        List<String> typer = Arrays.asList(SPORSMAL.name(), SVAR.name(), REFERAT.name());
        return on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(typer)).getAny()).map(TIL_MELDING).collect();
    }

    public List<Sak> hentSakerForBruker(String fnr) {
        WSFinnGenerellSakListeResponse response = sakWs.finnGenerellSakListe(new WSFinnGenerellSakListeRequest().withBrukerId(fnr));
        return on(response.getSakListe()).map(tilSak).collect();
    }

    public void journalforTraad(TraadVM valgtTraad, Sak sak, String fnr) {
        Melding eldsteMelding = valgtTraad.getEldsteMelding().melding;
        String journalpostIdEldsteMelding;
        if (eldsteMelding.journalfortDato == null) {
            journalpostIdEldsteMelding = behandleJournalforing(eldsteMelding, sak, fnr, null);
            oppdaterJournalfortInformasjonIHenvendelse(sak, journalpostIdEldsteMelding, eldsteMelding);
        } else {
            journalpostIdEldsteMelding = eldsteMelding.journalfortSaksId;
        }
        for (MeldingVM meldingVM : valgtTraad.getMeldinger().subList(0, valgtTraad.getMeldinger().size() - 1)) {
            Melding melding = meldingVM.melding;
            if (melding.journalfortDato == null) {
                String journalpostId = behandleJournalforing(melding, sak, fnr, journalpostIdEldsteMelding);
                oppdaterJournalfortInformasjonIHenvendelse(sak, journalpostId, melding);
            }
        }
    }

    private void oppdaterJournalfortInformasjonIHenvendelse(Sak sak, String journalpostId, Melding melding) {
        behandleHenvendelsePortType.oppdaterJournalfortInformasjon(melding.id,
                new XMLJournalfortInformasjon()
                        .withJournalfortTema(sak.tema)
                        .withJournalfortDato(DateTime.now())
                        .withJournalpostId(journalpostId)
                        .withJournalfortSaksId(sak.saksId)
        );
    }

    private String behandleJournalforing(Melding melding, Sak sak, String fnr, String journalpostIdEldsteMelding) {
        if (melding.meldingstype.equals(Meldingstype.SPORSMAL)) {
            return behandleJournalSporsmal(melding, sak, fnr);

        } else if (melding.meldingstype.equals(Meldingstype.SVAR)) {
            return (behandleJournalSvar(melding, sak, fnr, journalpostIdEldsteMelding));

        } else {
            return (behandleJournalSamtalereferat(melding, sak, fnr, optional(journalpostIdEldsteMelding)));
        }

    }

    private String behandleJournalSporsmal(Melding melding, Sak sak, String fnr) {
        JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest = new JournalfoerInngaaendeHenvendelseRequest();

        // TODO Få tak i etternavn og fornavn, foreløpig har vi bare navident
        journalfoerInngaaendeHenvendelseRequest.setPersonEtternavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setPersonFornavn(SubjectHandler.getSubjectHandler().getUid());

        journalfoerInngaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);

        Journalpost journalpost = new Journalpost();

        Kommunikasjonskanaler kommunikasjonskanaler = createAndSetKommunikasjonskanaler();
        journalpost.setKanal(kommunikasjonskanaler);

        journalpost.setMottattDato(konverterDateTimeObjektTilGregXML(melding.opprettetDato));

        Signatur signatur = createAndSetSignatur();
        journalpost.setSignatur(signatur);

        Arkivtemaer arkivtemaer = createAndSetArkivtemaer(sak);
        journalpost.setArkivtema(arkivtemaer);

        Person bruker = createAndSetPerson(fnr);
        journalpost.getForBruker().add(bruker);

        journalpost.setOpprettetAvNavn(SubjectHandler.getSubjectHandler().getUid());

        journalpost.setInnhold("Elektronisk kommunikasjon med NAV ");

        EksternPart eksternPart = createAndSetEksternPart(bruker);
        journalpost.setEksternPart(eksternPart);

        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        byte[] pdfInnhold = PdfUtils.genererPdf(melding);
        dokumentinfoRelasjon.setJournalfoertDokument(createAndSetJournalfoertDokumentInfoForInngaaende(pdfInnhold));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);

        journalpost.setDokumentDato(konverterDateTimeObjektTilGregXML(DateTime.now()));

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalSak = createJournalSak(sak);
        journalpost.setGjelderSak(journalSak);

        journalfoerInngaaendeHenvendelseRequest.setJournalpost(journalpost);

        JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelseResponse = behandleJournalV2.journalfoerInngaaendeHenvendelse(journalfoerInngaaendeHenvendelseRequest);
        return journalfoerInngaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalSvar(Melding melding, Sak sak, String fnr, String journalfortPostIdForTilhorendeSporsmal) {
        JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest = new JournalfoerUtgaaendeHenvendelseRequest();

        // TODO Få tak i etternavn og fornavn, foreløpig har vi bare nav ident
        journalfoerUtgaaendeHenvendelseRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setPersonFornavn(getSubjectHandler().getUid());

        journalfoerUtgaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.Journalpost journalpost = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.Journalpost();

        Kommunikasjonskanaler kommunikasjonskanaler = createAndSetKommunikasjonskanaler();
        journalpost.setKanal(kommunikasjonskanaler);

        Signatur signatur = createAndSetSignatur();
        journalpost.setSignatur(signatur);

        Arkivtemaer arkivtemaer = createAndSetArkivtemaer(sak);
        journalpost.setArkivtema(arkivtemaer);

        Person bruker = createAndSetPerson(fnr);
        journalpost.getForBruker().add(bruker);

        journalpost.setInnhold("Elektronisk kommunikasjon med NAV ");

        EksternPart eksternPart = createAndSetEksternPart(bruker);
        journalpost.setEksternPart(eksternPart);

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalSak = createJournalSak(sak);
        journalpost.setGjelderSak(journalSak);

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.DokumentinfoRelasjon dokumentinfoRelasjon = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.DokumentinfoRelasjon();
        byte[] pdfInnhold = PdfUtils.genererPdf(melding);
        dokumentinfoRelasjon.setJournalfoertDokument(createAndSetJournalfoertDokumentInfoForUtgaaende(pdfInnhold));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);

        journalpost.setDokumentDato(konverterDateTimeObjektTilGregXML(DateTime.now()));

        Kryssreferanse kryssreferanse = createAndSetKryssreferanse(journalfortPostIdForTilhorendeSporsmal);
        journalpost.getKryssreferanseListe().add(kryssreferanse);

        journalfoerUtgaaendeHenvendelseRequest.setJournalpost(journalpost);

        JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponse = behandleJournalV2.journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequest);
        return journalfoerUtgaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalSamtalereferat(Melding melding, Sak sak, String fnr, Optional<String> journalfortPostIdForTilhorendeSporsmal) {
        JournalfoerNotatRequest journalfoerNotatRequest = new JournalfoerNotatRequest();

        // TODO Få tak i etternavn og fornavn, foreløpig har vi bare nav ident
        journalfoerNotatRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setPersonFornavn(getSubjectHandler().getUid());

        journalfoerNotatRequest.setApplikasjonsID(MODIA_SYSTEM_ID);

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.Journalpost journalpost = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.Journalpost();

        Kommunikasjonskanaler kommunikasjonskanaler = createAndSetKommunikasjonskanaler();
        journalpost.setKanal(kommunikasjonskanaler);

        Signatur signatur = createAndSetSignatur();
        journalpost.setSignatur(signatur);

        Arkivtemaer arkivtemaer = createAndSetArkivtemaer(sak);
        journalpost.setArkivtema(arkivtemaer);

        Person bruker = createAndSetPerson(fnr);
        journalpost.getForBruker().add(bruker);

        journalpost.setInnhold("Elektronisk kommunikasjon med NAV ");
        journalpost.setDokumentDato(konverterDateTimeObjektTilGregXML(DateTime.now()));

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalSak = createJournalSak(sak);
        journalpost.setGjelderSak(journalSak);

        if (journalfortPostIdForTilhorendeSporsmal.isSome()) {
            Kryssreferanse kryssreferanse = createAndSetKryssreferanse(journalfortPostIdForTilhorendeSporsmal.get());
            journalpost.getKryssreferanseListe().add(kryssreferanse);
        }

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.DokumentinfoRelasjon dokumentinfoRelasjon = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.DokumentinfoRelasjon();
        byte[] pdfInnhold = PdfUtils.genererPdf(melding);
        dokumentinfoRelasjon.setJournalfoertDokument(createAndSetJournalfoertDokumentInfoForNotat(pdfInnhold));
        dokumentinfoRelasjon.setTillknyttetJournalpostSomKode(HOVEDDOKUMENT);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);

        journalfoerNotatRequest.setJournalpost(journalpost);

        JournalfoerNotatResponse journalfoerNotatResponse = behandleJournalV2.journalfoerNotat(journalfoerNotatRequest);
        return journalfoerNotatResponse.getJournalpostId();
    }

    private EksternPart createAndSetEksternPart(Person bruker) {
        EksternPart eksternPart = new EksternPart();
        eksternPart.setEksternAktoer(bruker);
        return eksternPart;
    }

    private XMLGregorianCalendar konverterDateTimeObjektTilGregXML(DateTime dateTime) {
        GregorianCalendar dokumentDato = new GregorianCalendar();
        dokumentDato.setTime(dateTime.toDate());
        try {
            return (DatatypeFactory.newInstance().newXMLGregorianCalendar(dokumentDato));
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Noe gikk galt ved instansiering av XMLGregorianCalendar", e);
        }
    }

    private Signatur createAndSetSignatur() {
        Signatur signatur = new Signatur();
        signatur.setSignert(false);
        return signatur;
    }

    private no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak createJournalSak(Sak sak) {
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalSak = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak();
        journalSak.setSaksId(sak.saksId);
        journalSak.setFagsystemkode(sak.fagsystem);
        return journalSak;
    }

    private Person createAndSetPerson(String fnr) {
        // TODO Få tak i kodeverk og sett det inn i denne metoden, norskident har flere felter
        Person bruker = new Person();
        NorskIdent norskIdent = new NorskIdent();
        norskIdent.setIdent(fnr);
        bruker.setIdent(norskIdent);
        return bruker;
    }

    private Kryssreferanse createAndSetKryssreferanse(String journalfortPostIdForTilhorendeSporsmal) {
        Kryssreferanse kryssreferanse = new Kryssreferanse();
        kryssreferanse.setReferanseId(journalfortPostIdForTilhorendeSporsmal);
        kryssreferanse.setReferansekode("SPOERSMAAL");
        return kryssreferanse;
    }

    private Kommunikasjonskanaler createAndSetKommunikasjonskanaler() {
        Kommunikasjonskanaler kommunikasjonskanaler = new Kommunikasjonskanaler();
        kommunikasjonskanaler.setValue("Elektronisk");
        kommunikasjonskanaler.setKodeverksRef("http://nav.no/kodeverk/Kodeverk/Kommunikasjonskanaler");
        return kommunikasjonskanaler;
    }

    private Arkivtemaer createAndSetArkivtemaer(Sak sak) {
        // TODO Få tak i kodeverk og sett det inn i denne metoden
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(sak.tema);
        arkivtemaer.setKodeverksRef("");
        arkivtemaer.setKodeRef("");
        return arkivtemaer;
    }

    private no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.JournalfoertDokumentInfo createAndSetJournalfoertDokumentInfoForUtgaaende(byte[] pdf) {
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.JournalfoertDokumentInfo journalfoertDokumentInfo = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.JournalfoertDokumentInfo();

        // TODO hent inn kodeverk for feletene setKodevrksRef() og setKodeRef() som tilhører dokumenttyper-objektet
        Dokumenttyper dokumenttyper = new Dokumenttyper();
        dokumenttyper.setValue(DOKUTYPE_UTGAENDE);

        journalfoertDokumentInfo.setDokumentType(dokumenttyper);
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setBrevkode(SPORSMAL_OG_SVAR);
        journalfoertDokumentInfo.setKategorikode(KATEGORI_KODE_ES);
        journalfoertDokumentInfo.setSensitivitet(false);

        List<DokumentInnhold> beskriverInnhold = journalfoertDokumentInfo.getBeskriverInnhold();
        beskriverInnhold.add(generateUstrukturertInnhold(pdf));

        // TODO få inn den egentlige tittelen her
        journalfoertDokumentInfo.setTittel("Dokumenttittel");

        return journalfoertDokumentInfo;
    }

    private JournalfoertDokumentInfo createAndSetJournalfoertDokumentInfoForInngaaende(byte[] pdf) {
        JournalfoertDokumentInfo journalfoertDokumentInfo = new JournalfoertDokumentInfo();

        // TODO hent inn kodeverk for feletene setKodevrksRef() og setKodeRef() som tilhører dokumenttyper-objektet
        Dokumenttyper dokumenttyper = new Dokumenttyper();
        dokumenttyper.setValue(DOKUTYPE_MELDING);

        journalfoertDokumentInfo.setDokumentType(dokumenttyper);
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setBrevkode(SPORSMAL_OG_SVAR);
        journalfoertDokumentInfo.setKategorikode(KATEGORI_KODE_ES);
        journalfoertDokumentInfo.setSensitivitet(false);

        List<DokumentInnhold> beskriverInnhold = journalfoertDokumentInfo.getBeskriverInnhold();
        beskriverInnhold.add(generateUstrukturertInnhold(pdf));

        // TODO få inn den egentlige tittelen her
        journalfoertDokumentInfo.setTittel("Dokumenttittel");

        return journalfoertDokumentInfo;
    }

    private no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.JournalfoertDokumentInfo createAndSetJournalfoertDokumentInfoForNotat(byte[] pdf) {
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.JournalfoertDokumentInfo journalfoertDokumentInfo = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.JournalfoertDokumentInfo();

        // TODO hent inn kodeverk for feletene setKodevrksRef() og setKodeRef() som tilhører dokumenttyper-objektet
        Dokumenttyper dokumenttyper = new Dokumenttyper();
        dokumenttyper.setValue(DOKUTYPE_UTGAENDE);

        journalfoertDokumentInfo.setDokumentType(dokumenttyper);
        journalfoertDokumentInfo.setBegrensetPartsInnsyn(false);
        journalfoertDokumentInfo.setBrevkode(SPORSMAL_OG_SVAR);
        journalfoertDokumentInfo.setErOrganinternt(false);
        journalfoertDokumentInfo.setKategorikode(KATEGORI_KODE_ES);
        journalfoertDokumentInfo.setSensitivitet(false);

        List<DokumentInnhold> beskriverInnhold = journalfoertDokumentInfo.getBeskriverInnhold();
        beskriverInnhold.add(generateUstrukturertInnhold(pdf));

        // TODO få inn den egentlige tittelen her
        journalfoertDokumentInfo.setTittel("Dokumenttittel");

        return journalfoertDokumentInfo;
    }

    private UstrukturertInnhold generateUstrukturertInnhold(byte[] pdf) {
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

    private static Transformer<WSGenerellSak, Sak> tilSak = new Transformer<WSGenerellSak, Sak>() {
        @Override
        public Sak transform(WSGenerellSak wsGenerellSak) {
            Sak sak = new Sak();
            sak.opprettetDato = wsGenerellSak.getEndringsinfo().getOpprettetDato();
            sak.saksId = wsGenerellSak.getSakId();
            sak.tema = wsGenerellSak.getFagomradeKode();
            sak.sakstype = wsGenerellSak.getSakstypeKode();
            sak.fagsystem = wsGenerellSak.getFagsystemKode();
            return sak;
        }
    };

}
