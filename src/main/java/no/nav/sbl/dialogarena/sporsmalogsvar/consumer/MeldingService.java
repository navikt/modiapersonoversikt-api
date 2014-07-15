package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.binding.BehandleJournalV2;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.*;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.DokumentinfoRelasjon;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.JournalfoertDokumentInfo;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerinngaaendehenvendelse.Journalpost;
import no.nav.tjeneste.virksomhet.behandlejournal.v2.meldinger.*;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;

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

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
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

    private final static String MODIA_SYSTEM_ID = "BD06";

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
        for (MeldingVM meldingVM : valgtTraad.getMeldinger().subList(0,valgtTraad.getMeldinger().size()-1)) {
            Melding melding = meldingVM.melding;
            if (melding.journalfortDato == null) {
                String journalpostId = behandleJournalforing(melding, sak, fnr, journalpostIdEldsteMelding);
                oppdaterJournalfortInformasjonIHenvendelse(sak, journalpostId, melding);
            }

        }
    }

    private void oppdaterJournalfortInformasjonIHenvendelse(Sak sak, String journalpostId, Melding melding){
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

        } else if (melding.meldingstype.equals(Meldingstype.SVAR)){
            return(behandleJournalSvar(sak, fnr, journalpostIdEldsteMelding));

        } else {
            return(behandleJournalSamtalereferat(sak, fnr, optional(journalpostIdEldsteMelding)));
        }

    }

    private String behandleJournalSporsmal(Melding melding, Sak sak, String fnr){
        JournalfoerInngaaendeHenvendelseRequest journalfoerInngaaendeHenvendelseRequest = new JournalfoerInngaaendeHenvendelseRequest();
        journalfoerInngaaendeHenvendelseRequest.setPersonEtternavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setPersonFornavn(SubjectHandler.getSubjectHandler().getUid());
        journalfoerInngaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);

        Journalpost journalpost = new Journalpost();

        // Set journalpostfelter
        Kommunikasjonskanaler kommunikasjonskanaler = createAndSetKommunikasjonskanaler();
        journalpost.setKanal(kommunikasjonskanaler);

        journalpost.setMottattDato(konverterDateTimeObjektTilGregXML(melding.opprettetDato));

        Signatur signatur = createAndSetSignatur();
        journalpost.setSignatur(signatur);

        Arkivtemaer arkivtemaer = createAndSetArkivtemaer(sak);
        journalpost.setArkivtema(arkivtemaer);

        // TODO Få tak i kodeverk og sett det inn i createAndSetPerson-metoden
        Person bruker = createAndSetPerson(fnr);
        journalpost.getForBruker().add(bruker);

        journalpost.setOpprettetAvNavn(SubjectHandler.getSubjectHandler().getUid());

        journalpost.setInnhold("Elektronisk kommunikasjon med NAV ");

        EksternPart eksternPart = createAndSetEksternPart(bruker);
        journalpost.setEksternPart(eksternPart);

        DokumentinfoRelasjon dokumentinfoRelasjon = new DokumentinfoRelasjon();
        JournalfoertDokumentInfo journalfoertDokumentInfo = new JournalfoertDokumentInfo();
        dokumentinfoRelasjon.setJournalfoertDokument(journalfoertDokumentInfo);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);

        journalpost.setDokumentDato(konverterDateTimeObjektTilGregXML(DateTime.now()));

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalSak = createJournalSak(sak);
        journalpost.setGjelderSak(journalSak);

        journalfoerInngaaendeHenvendelseRequest.setJournalpost(journalpost);

        JournalfoerInngaaendeHenvendelseResponse journalfoerInngaaendeHenvendelseResponse = behandleJournalV2.journalfoerInngaaendeHenvendelse(journalfoerInngaaendeHenvendelseRequest);
        return journalfoerInngaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalSvar(Sak sak, String fnr, String journalfortPostIdForTilhorendeSporsmal){
        JournalfoerUtgaaendeHenvendelseRequest journalfoerUtgaaendeHenvendelseRequest = new JournalfoerUtgaaendeHenvendelseRequest();
        journalfoerUtgaaendeHenvendelseRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setPersonFornavn(getSubjectHandler().getUid());
        journalfoerUtgaaendeHenvendelseRequest.setApplikasjonsID(MODIA_SYSTEM_ID);

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.Journalpost journalpost = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.Journalpost();

        // Set journalpostfelter
        Kommunikasjonskanaler kommunikasjonskanaler = createAndSetKommunikasjonskanaler();
        journalpost.setKanal(kommunikasjonskanaler);

        Signatur signatur = createAndSetSignatur();
        journalpost.setSignatur(signatur);

        Arkivtemaer arkivtemaer = createAndSetArkivtemaer(sak);
        journalpost.setArkivtema(arkivtemaer);

        // TODO Få tak i kodeverk og sett det inn i createAndSetPerson-metoden
        Person bruker = createAndSetPerson(fnr);
        journalpost.getForBruker().add(bruker);

        journalpost.setInnhold("Elektronisk kommunikasjon med NAV ");

        EksternPart eksternPart = createAndSetEksternPart(bruker);
        journalpost.setEksternPart(eksternPart);

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.behandlejournal.Sak journalSak = createJournalSak(sak);
        journalpost.setGjelderSak(journalSak);

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.DokumentinfoRelasjon dokumentinfoRelasjon = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.DokumentinfoRelasjon();
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.JournalfoertDokumentInfo journalfoertDokumentInfo = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoerutgaaendehenvendelse.JournalfoertDokumentInfo();
        dokumentinfoRelasjon.setJournalfoertDokument(journalfoertDokumentInfo);
        journalpost.getDokumentinfoRelasjon().add(dokumentinfoRelasjon);

        journalpost.setDokumentDato(konverterDateTimeObjektTilGregXML(DateTime.now()));

        Kryssreferanse kryssreferanse = createAndSetKryssreferanse(journalfortPostIdForTilhorendeSporsmal);
        journalpost.getKryssreferanseListe().add(kryssreferanse);

        journalfoerUtgaaendeHenvendelseRequest.setJournalpost(journalpost);

        JournalfoerUtgaaendeHenvendelseResponse journalfoerUtgaaendeHenvendelseResponse = behandleJournalV2.journalfoerUtgaaendeHenvendelse(journalfoerUtgaaendeHenvendelseRequest);
        return journalfoerUtgaaendeHenvendelseResponse.getJournalpostId();
    }

    private String behandleJournalSamtalereferat(Sak sak, String fnr, Optional<String> journalfortPostIdForTilhorendeSporsmal){
        JournalfoerNotatRequest journalfoerNotatRequest = new JournalfoerNotatRequest();
        journalfoerNotatRequest.setPersonEtternavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setPersonFornavn(getSubjectHandler().getUid());
        journalfoerNotatRequest.setApplikasjonsID(MODIA_SYSTEM_ID);

        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.Journalpost journalpost = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.Journalpost();

        // Set journalpostfelter
        Kommunikasjonskanaler kommunikasjonskanaler = createAndSetKommunikasjonskanaler();
        journalpost.setKanal(kommunikasjonskanaler);

        Signatur signatur = createAndSetSignatur();
        journalpost.setSignatur(signatur);

        Arkivtemaer arkivtemaer = createAndSetArkivtemaer(sak);
        journalpost.setArkivtema(arkivtemaer);

        // TODO Få tak i kodeverk og sett det inn i createAndSetPerson-metoden
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
        no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.JournalfoertDokumentInfo journalfoertDokumentInfo = new no.nav.tjeneste.virksomhet.behandlejournal.v2.informasjon.journalfoernotat.JournalfoertDokumentInfo();
        dokumentinfoRelasjon.setJournalfoertDokument(journalfoertDokumentInfo);
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

    private XMLGregorianCalendar konverterDateTimeObjektTilGregXML(DateTime dateTime){
        GregorianCalendar dokumentDato = new GregorianCalendar();
        dokumentDato.setTime(dateTime.toDate());
        try {
            return(DatatypeFactory.newInstance().newXMLGregorianCalendar(dokumentDato));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return null;
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
        Arkivtemaer arkivtemaer = new Arkivtemaer();
        arkivtemaer.setValue(sak.tema);
        arkivtemaer.setKodeverksRef("");
        arkivtemaer.setKodeRef("");
        return arkivtemaer;
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
