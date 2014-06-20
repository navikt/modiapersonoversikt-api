package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.HenvendelseUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSSendHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.sendhenvendelse.SendHenvendelsePortType;
import no.nav.virksomhet.gjennomforing.oppgave.v2.Oppgave;
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

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.HenvendelseUtils.createSporsmaalFromHenvendelse;

public class SakService {

    public static final int ANSVARLIG_ENHET = 2820;
    public static final int OPPRETTET_AV_ENHET = 2820;
    public static final int ENDRET_AV_ENHET = 2820;
    public static final int FERDIGSTILT_AV_ENHET = 2820;
    public static final String OPPGAVETYPEKODE = "KONT_BRUK_GEN"; // Brukergenerert. Denne brukes lite og er dermed ganske safe

    private static final Map<String, String> FAGOMRADE;
    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", "BAR");
        tmp.put("FAMILIE_OG_BARN", "BID");
        tmp.put("HJELPEMIDLER", "HJE");
        tmp.put("OVRIGE_HENVENDELSER","GRA");
        FAGOMRADE = unmodifiableMap(tmp);
    }

    @Inject
    private Oppgavebehandling oppgavebehandlingWS;

    @Inject
    private no.nav.virksomhet.tjenester.oppgave.v2.binding.Oppgave oppgaveWS;

    @Inject
    private HenvendelsePortType henvendelsePortType;

    @Inject
    protected SendHenvendelsePortType sendHenvendelsePortType;

    public void sendSvar(Svar svar) {
        XMLBehandlingsinformasjon info = HenvendelseUtils.createXMLBehandlingsinformasjon(svar);
        sendHenvendelsePortType.sendHenvendelse(new WSSendHenvendelseRequest().withType(SVAR.name()).withFodselsnummer(svar.fnr).withAny(info));
    }

    public void sendReferat(Referat referat) {
        XMLBehandlingsinformasjon info = HenvendelseUtils.createXMLBehandlingsinformasjon(referat);
        sendHenvendelsePortType.sendHenvendelse(new WSSendHenvendelseRequest().withType(REFERAT.name()).withFodselsnummer(referat.fnr).withAny(info));
    }

    public Sporsmal getSporsmalFromOppgaveId(String fnr, String oppgaveid) {
        List<Object> henvendelseListe =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(SPORSMAL.toString())).getAny();

        for (Object o : henvendelseListe) {
            XMLBehandlingsinformasjon henvendelse = (XMLBehandlingsinformasjon) o;
            XMLSporsmal xmlSporsmal = (XMLSporsmal) henvendelse.getMetadataListe().getMetadata().get(0);
            if (oppgaveid.equals(xmlSporsmal.getOppgaveIdGsak())) {
                return createSporsmaalFromHenvendelse(henvendelse);
            }
        }
        return null;
    }

    public Sporsmal getSporsmalOgTilordneIGsak(String sporsmalsId) {
        XMLBehandlingsinformasjon behandlingsinformasjon =
                (XMLBehandlingsinformasjon) henvendelsePortType.hentHenvendelse(new WSHentHenvendelseRequest().withBehandlingsId(sporsmalsId)).getAny();
        XMLSporsmal xmlSporsmal = (XMLSporsmal) behandlingsinformasjon.getMetadataListe().getMetadata().get(0);
        tilordneOppgave(optional(hentOppgaveFraGsak(xmlSporsmal.getOppgaveIdGsak())));
        return createSporsmaalFromHenvendelse(behandlingsinformasjon);
    }

    public Optional<Oppgave> plukkOppgaveFraGsak(String tema) {
        Optional<Oppgave> oppgave = finnIkkeTilordnedeOppgaver(FAGOMRADE.get(tema));
        if (oppgave.isSome()) {
            Oppgave tilordnet = tilordneOppgave(oppgave);
            return optional(tilordnet);
        } else {
            return none();
        }
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
        return hentOppgaveResponse.getOppgave();
    }

    public void ferdigstillOppgaveFraGsak(String oppgaveId) {
        FerdigstillOppgaveBolkRequest ferdigstillOppgaveBolkRequest = new FerdigstillOppgaveBolkRequest();
        ferdigstillOppgaveBolkRequest.getOppgaveIdListe().add(oppgaveId);
        ferdigstillOppgaveBolkRequest.setFerdigstiltAvEnhetId(FERDIGSTILT_AV_ENHET);
        oppgavebehandlingWS.ferdigstillOppgaveBolk(ferdigstillOppgaveBolkRequest);
    }

    private Oppgave tilordneOppgave(Optional<Oppgave> oppgave) {
        Oppgave wsOppgave = oppgave.get();
        wsOppgave.setAnsvarligId(getSubjectHandler().getUid());

        LagreOppgaveRequest lagreOppgaveRequest = new LagreOppgaveRequest();
        lagreOppgaveRequest.setEndreOppgave(tilEndreOppgave(wsOppgave));
        lagreOppgaveRequest.setEndretAvEnhetId(ENDRET_AV_ENHET);
        try {
            oppgavebehandlingWS.lagreOppgave(lagreOppgaveRequest);
        } catch (LagreOppgaveOppgaveIkkeFunnet e) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", e);
        }
        return wsOppgave;
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

        List<Oppgave> oppgaveListe =
                oppgaveWS.finnOppgaveListe(finnOppgaveListeRequest).getOppgaveListe();

        return on(oppgaveListe).head();
    }

    private static EndreOppgave tilEndreOppgave(Oppgave oppgave) {
        EndreOppgave endreOppgave = new EndreOppgave();

        endreOppgave.setDokumentId(oppgave.getDokumentId());
        endreOppgave.setKravId(oppgave.getKravId());
        endreOppgave.setOppgaveId(oppgave.getOppgaveId());
        endreOppgave.setBrukerId(oppgave.getGjelder().getBrukerId());
        endreOppgave.setAnsvarligId(oppgave.getAnsvarligId());
        endreOppgave.setAnsvarligEnhetId(oppgave.getAnsvarligEnhetId());

        endreOppgave.setFagomradeKode(oppgave.getFagomrade().getKode());
        endreOppgave.setOppgavetypeKode(oppgave.getOppgavetype().getKode());
        endreOppgave.setPrioritetKode(oppgave.getPrioritet().getKode());
        endreOppgave.setBrukertypeKode(oppgave.getGjelder().getBrukertypeKode());
        endreOppgave.setUnderkategoriKode(oppgave.getUnderkategori().getKode());

        endreOppgave.setAktivFra(oppgave.getAktivFra());
        endreOppgave.setBeskrivelse(oppgave.getBeskrivelse());
        endreOppgave.setVersjon(oppgave.getVersjon());
        endreOppgave.setSaksnummer(oppgave.getSaksnummer());
        endreOppgave.setLest(oppgave.isLest());

        return endreOppgave;
    }

    private FinnOppgaveListeFilter fellesFilter() {
        FinnOppgaveListeFilter finnOppgaveListeFilter = new FinnOppgaveListeFilter();
        finnOppgaveListeFilter.setOpprettetEnhetId(String.valueOf(OPPRETTET_AV_ENHET));
        finnOppgaveListeFilter.getOppgavetypeKodeListe().add(OPPGAVETYPEKODE);
        return finnOppgaveListeFilter;
    }

}
