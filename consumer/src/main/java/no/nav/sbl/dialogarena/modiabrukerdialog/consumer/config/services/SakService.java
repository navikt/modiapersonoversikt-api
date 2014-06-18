package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmaal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.HenvendelseUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeFilter;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeSok;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.FinnOppgaveListeSortering;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.HentOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgave.meldinger.v2.HentOppgaveResponse;
import no.nav.virksomhet.tjenester.oppgave.v2.binding.HentOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.EndreOppgave;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.FerdigstillOppgaveBolkRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.meldinger.v2.LagreOppgaveRequest;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.virksomhet.tjenester.oppgavebehandling.v2.binding.Oppgavebehandling;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;

public class SakService {

    public static final int ANSVARLIG_ENHET = 4112;
    public static final int OPPRETTET_AV_ENHET = 4112;
    public static final int ENDRET_AV_ENHET = 4112;
    public static final int FERDIGSTILT_AV_ENHET = 4112;
    public static final String OPPGAVETYPEKODE = "KONT_BRUK_GEN"; // Brukergenerert. Denne brukes lite og er dermed ganske safe
    public static final String PRIORITETKODE = "NORM_GEN"; // Normal prioritet - Generell


    @Inject
    private Oppgavebehandling oppgavebehandlingWS;

    @Inject
    private no.nav.virksomhet.tjenester.oppgave.v2.binding.Oppgave oppgaveWS;

    @Inject
    private HenvendelsePortType henvendelsePortType;

    @Inject
    protected SendHenvendelsePortType sendHenvendelsePortType;

    public Sporsmaal getSporsmaal(String sporsmalsId) {
        Object henvendelsesObjekt = henvendelsePortType.hentHenvendelse(new WSHentHenvendelseRequest().withBehandlingsId(sporsmalsId)).getAny();
        return HenvendelseUtils.createSporsmaalFromHenvendelse(henvendelsesObjekt);
    }

    public void sendSvar(Svar svar) {
        XMLBehandlingsinformasjon info = HenvendelseUtils.createXMLBehandlingsinformasjon(svar);
        sendHenvendelsePortType.sendHenvendelse(new WSSendHenvendelseRequest().withType(SVAR.name()).withFodselsnummer(svar.fnr).withAny(info));
    }

    public void sendReferat(Referat referat) {
        XMLBehandlingsinformasjon info = HenvendelseUtils.createXMLBehandlingsinformasjon(referat);
        sendHenvendelsePortType.sendHenvendelse(new WSSendHenvendelseRequest().withType(REFERAT.name()).withFodselsnummer(referat.fnr).withAny(info));
    }

    public Oppgave hentOppgaveFraGsak(String oppgaveId) {
        HentOppgaveResponse hentOppgaveResponse;
        try {
            HentOppgaveRequest hentOppgaveRequest = new HentOppgaveRequest();
            hentOppgaveRequest.setOppgaveId(oppgaveId);
            hentOppgaveResponse = oppgaveWS.hentOppgave(hentOppgaveRequest);
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
        return tilOppgave(hentOppgaveResponse.getOppgave());
    }

    public Optional<Oppgave> plukkOppgaveFraGsak(String tema) {
        Optional<Oppgave> oppgave = finnIkkeTilordnedeOppgaver(tema);
        if (oppgave.isSome()) {
            Oppgave tilordnet = oppgave.get().withSaksbehandlerid(optional(getSubjectHandler().getUid()));
            oppdaterOppgave(tilordnet);
            return optional(tilordnet);
        } else {
            return none();
        }
    }

    public void ferdigstillOppgaveFraGsak(String oppgaveId) {
        FerdigstillOppgaveBolkRequest ferdigstillOppgaveBolkRequest = new FerdigstillOppgaveBolkRequest();
        ferdigstillOppgaveBolkRequest.getOppgaveIdListe().add(oppgaveId);
        ferdigstillOppgaveBolkRequest.setFerdigstiltAvEnhetId(FERDIGSTILT_AV_ENHET);
        oppgavebehandlingWS.ferdigstillOppgaveBolk(ferdigstillOppgaveBolkRequest);
    }


    private Optional<Oppgave> finnIkkeTilordnedeOppgaver(String tema) {
        FinnOppgaveListeRequest finnOppgaveListeRequest = new FinnOppgaveListeRequest();

        FinnOppgaveListeFilter finnOppgaveListeFilter = fellesFilter();
        finnOppgaveListeFilter.setMaxAntallSvar(1);
        finnOppgaveListeFilter.setUfordelte(true);
        finnOppgaveListeRequest.setFilter(finnOppgaveListeFilter);

        FinnOppgaveListeSok finnOppgaveListeSok = new FinnOppgaveListeSok();
        finnOppgaveListeSok.setAnsvarligEnhetId(String.valueOf(ANSVARLIG_ENHET));
        finnOppgaveListeSok.getFagomradeKodeListe().add(tema);
        finnOppgaveListeRequest.setSok(finnOppgaveListeSok);

        FinnOppgaveListeSortering finnOppgaveListeSortering = new FinnOppgaveListeSortering();
        finnOppgaveListeSortering.setSorteringKode("FRIST_DATO_STIG");

        List<no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave> oppgaveListe =
                oppgaveWS.finnOppgaveListe(finnOppgaveListeRequest).getOppgaveListe();

        return on(oppgaveListe).map(TIL_OPPGAVE).head();
    }


    private void oppdaterOppgave(Oppgave oppgave) {
        LagreOppgaveRequest lagreOppgaveRequest = new LagreOppgaveRequest();
        lagreOppgaveRequest.setEndreOppgave(tilEndreOppgave(oppgave));
        lagreOppgaveRequest.setEndretAvEnhetId(ENDRET_AV_ENHET);
        try {
            oppgavebehandlingWS.lagreOppgave(lagreOppgaveRequest);
        } catch (LagreOppgaveOppgaveIkkeFunnet e) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", e);
        }
    }

    private static Oppgave tilOppgave(no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave wsOppgave) {
        return new Oppgave()
                .withId(wsOppgave.getOppgaveId())
                .withBehandlingsid(wsOppgave.getHenvendelseId())
                .withFodselsnummer(wsOppgave.getGjelder().getBrukerId())
                .withSaksbehandlerid(optional(wsOppgave.getAnsvarligId()))
                .withBeskrivelse(optional(wsOppgave.getBeskrivelse()))
                .withFerdigstilt(wsOppgave.getStatus().getKode().equals("F"))
                .withTema(wsOppgave.getFagomrade().getKode())
                .withAktivFra(new DateTime(wsOppgave.getAktivFra().toGregorianCalendar().getTime()).toLocalDate())
                .withVersjon(wsOppgave.getVersjon());
    }

    private static EndreOppgave tilEndreOppgave(Oppgave oppgave) {
        EndreOppgave endreOppgave = new EndreOppgave();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(oppgave.getAktivFra().toDate());
        try {
            endreOppgave.setAktivFra(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
        endreOppgave.setBeskrivelse(oppgave.getBeskrivelse().getOrElse(""));
        endreOppgave.setFagomradeKode(oppgave.getTema());
        endreOppgave.setOppgaveId(oppgave.getId());
        endreOppgave.setOppgavetypeKode(OPPGAVETYPEKODE);
        endreOppgave.setPrioritetKode(PRIORITETKODE);
        endreOppgave.setAnsvarligId(oppgave.getSaksbehandlerid().getOrElse(""));
        endreOppgave.setVersjon(oppgave.getVersjon());
        return endreOppgave;
    }

    private FinnOppgaveListeFilter fellesFilter() {
        FinnOppgaveListeFilter finnOppgaveListeFilter = new FinnOppgaveListeFilter();
        finnOppgaveListeFilter.setOpprettetEnhetId(String.valueOf(OPPRETTET_AV_ENHET));
        finnOppgaveListeFilter.getOppgavetypeKodeListe().add(OPPGAVETYPEKODE);
        return finnOppgaveListeFilter;
    }

    private static final Transformer<no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave, Oppgave> TIL_OPPGAVE = new Transformer<no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave, Oppgave>() {
        @Override
        public Oppgave transform(no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave wsOppgave) {
            return tilOppgave(wsOppgave);
        }
    };

}
