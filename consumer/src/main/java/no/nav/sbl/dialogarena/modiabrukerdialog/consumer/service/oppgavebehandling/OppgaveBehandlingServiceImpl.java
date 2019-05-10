package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.oppgavebehandling;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.LeggTilbakeOppgaveIGsakRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.*;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSFerdigstillOppgaveBolkRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.TildelOppgaveV1;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverRequest;
import no.nav.tjeneste.virksomhet.tildeloppgave.v1.WSTildelFlereOppgaverResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.brukerdialog.security.context.SubjectHandler.getSubjectHandler;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.*;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;

public class OppgaveBehandlingServiceImpl implements OppgaveBehandlingService {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveBehandlingServiceImpl.class);
    public static final Integer DEFAULT_ENHET = 4100;
    public static final String STORD_ENHET = "1221";
    public static final String KODE_OPPGAVE_FERDIGSTILT = "F";
    public static final String SPORSMAL_OG_SVAR = "SPM_OG_SVR";
    public static final String KONTAKT_NAV = "KNA";

    private final OppgavebehandlingV3 oppgavebehandlingWS;
    private final TildelOppgaveV1 tildelOppgaveWS;
    private final OppgaveV3 oppgaveWS;
    private final AnsattService ansattWS;
    private LeggTilbakeOppgaveIGsakDelegate leggTilbakeOppgaveIGsakDelegate;

    @Inject
    public OppgaveBehandlingServiceImpl(OppgavebehandlingV3 oppgavebehandlingWS,
                                        TildelOppgaveV1 tildelOppgaveWS,
                                        OppgaveV3 oppgaveWS,
                                        AnsattService ansattWS,
                                        ArbeidsfordelingV1Service arbeidsfordelingService) {
        this.oppgavebehandlingWS = oppgavebehandlingWS;
        this.tildelOppgaveWS = tildelOppgaveWS;
        this.oppgaveWS = oppgaveWS;
        this.ansattWS = ansattWS;
        this.leggTilbakeOppgaveIGsakDelegate = new LeggTilbakeOppgaveIGsakDelegate(this, arbeidsfordelingService);
    }

    @Override
    public void tilordneOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet) throws FikkIkkeTilordnet {
        tilordneOppgaveIGsak(oppgaveId, ofNullable(temagruppe), saksbehandlersValgteEnhet);
    }

    @Override
    public List<Oppgave> finnTildelteOppgaverIGsak() {
        return oppgaveWS
                .finnOppgaveListe(new WSFinnOppgaveListeRequest()
                        .withSok(new WSFinnOppgaveListeSok()
                                .withAnsvarligId(getSubjectHandler().getUid())
                                .withFagomradeKodeListe(KONTAKT_NAV))
                        .withFilter(new WSFinnOppgaveListeFilter()
                                .withAktiv(true)
                                .withOppgavetypeKodeListe(SPORSMAL_OG_SVAR)))
                .getOppgaveListe().stream()
                .map(OppgaveBehandlingServiceImpl::wsOppgaveToOppgave)
                .collect(toList());
    }

    @Override
    public List<Oppgave> plukkOppgaverFraGsak(Temagruppe temagruppe, String saksbehandlersValgteEnhet) {
        int enhetsId = Integer.parseInt(saksbehandlersValgteEnhet);
        return tildelEldsteLedigeOppgaver(temagruppe, enhetsId, saksbehandlersValgteEnhet).stream()
                .map(OppgaveBehandlingServiceImpl::wsOppgaveToOppgave)
                .collect(toList());
    }

    private static Oppgave wsOppgaveToOppgave(WSOppgave wsOppgave) {
        return new Oppgave(wsOppgave.getOppgaveId(), wsOppgave.getGjelder().getBrukerId(), wsOppgave.getHenvendelseId());
    }

    @Override
    public void ferdigstillOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet) {
        ferdigstillOppgaveIGsak(oppgaveId, ofNullable(temagruppe), saksbehandlersValgteEnhet);
    }

    @Override
    public void ferdigstillOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet) {
        ferdigstillOppgaverIGsak(singletonList(oppgaveId), temagruppe, saksbehandlersValgteEnhet);
    }

    @Override
    public void ferdigstillOppgaverIGsak(List<String> oppgaveIder, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet) {
        for (String oppgaveId : oppgaveIder) {
            oppdaterBeskrivelseIGsak(temagruppe, saksbehandlersValgteEnhet, oppgaveId);
        }

        oppgavebehandlingWS.ferdigstillOppgaveBolk(new WSFerdigstillOppgaveBolkRequest()
                .withOppgaveIdListe(oppgaveIder)
                .withFerdigstiltAvEnhetId(Integer.valueOf(enhetFor(temagruppe, saksbehandlersValgteEnhet))));
    }

    private void oppdaterBeskrivelseIGsak(Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet, String oppgaveId) {
        try {
            WSOppgave oppgave = oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
            oppgave.withBeskrivelse(leggTilBeskrivelse(oppgave.getBeskrivelse(), "Oppgaven er ferdigstilt i Modia",
                    saksbehandlersValgteEnhet));
            lagreOppgaveIGsak(oppgave, temagruppe, saksbehandlersValgteEnhet);
        } catch (HentOppgaveOppgaveIkkeFunnet | LagreOppgaveOptimistiskLasing e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leggTilbakeOppgaveIGsak(LeggTilbakeOppgaveIGsakRequest request) {
        if (request.getOppgaveId() == null) {
            return;
        }

        WSOppgave oppgaveFraGsak = hentOppgaveFraGsak(request.getOppgaveId());
        leggTilbakeOppgaveIGsakDelegate.leggTilbake(oppgaveFraGsak, request);
    }

    @Override
    public void systemLeggTilbakeOppgaveIGsak(String oppgaveId, Temagruppe temagruppe, String saksbehandlersValgteEnhet) {
        try {
            WSOppgave wsOppgave = hentOppgaveFraGsak(oppgaveId).withAnsvarligId("");
            lagreOppgaveIGsak(wsOppgave, temagruppe, saksbehandlersValgteEnhet);
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing);
        }
    }

    @Override
    public boolean oppgaveErFerdigstilt(String oppgaveid) {
        return equalsIgnoreCase(hentOppgaveFraGsak(oppgaveid).getStatus().getKode(), KODE_OPPGAVE_FERDIGSTILT);
    }

    String leggTilBeskrivelse(String gammelBeskrivelse, String leggTil, String valgtEnhet) {
        String ident = getSubjectHandler().getUid();
        String header = String.format("--- %s %s (%s, %s) ---\n",
                forPattern("dd.MM.yyyy HH:mm").print(now()),
                ansattWS.hentAnsattNavn(ident),
                ident,
                valgtEnhet);

        String nyBeskrivelse = header + leggTil;
        return isBlank(gammelBeskrivelse) ? nyBeskrivelse : nyBeskrivelse + "\n\n" + gammelBeskrivelse;
    }

    WSOppgave hentOppgaveFraGsak(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
    }

    private void tilordneOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet) throws FikkIkkeTilordnet {
        tilordneOppgaveIGsak(hentOppgaveFraGsak(oppgaveId), temagruppe, saksbehandlersValgteEnhet);
    }

    private WSOppgave tilordneOppgaveIGsak(WSOppgave oppgave, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet) throws FikkIkkeTilordnet {
        try {
            WSOppgave wsOppgave = oppgave.withAnsvarligId(getSubjectHandler().getUid());
            lagreOppgaveIGsak(wsOppgave, temagruppe, saksbehandlersValgteEnhet);
            return wsOppgave;
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new FikkIkkeTilordnet(lagreOppgaveOptimistiskLasing);
        }
    }

    void lagreOppgaveIGsak(WSOppgave wsOppgave, Temagruppe temagruppe, String saksbehandlersValgteEnhet) throws LagreOppgaveOptimistiskLasing {
        lagreOppgaveIGsak(wsOppgave, ofNullable(temagruppe), saksbehandlersValgteEnhet);
    }

    private void lagreOppgaveIGsak(WSOppgave wsOppgave, Optional<Temagruppe> temagruppe, String saksbehandlersValgteEnhet) throws LagreOppgaveOptimistiskLasing {
        try {
            oppgavebehandlingWS.lagreOppgave(
                    new WSLagreOppgaveRequest()
                            .withEndreOppgave(tilWSEndreOppgave(wsOppgave))
                            .withEndretAvEnhetId(Integer.valueOf(enhetFor(temagruppe, saksbehandlersValgteEnhet)))
            );
        } catch (LagreOppgaveOppgaveIkkeFunnet lagreOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", lagreOppgaveOppgaveIkkeFunnet);
        }
    }

    private List<WSOppgave> tildelEldsteLedigeOppgaver(Temagruppe temagruppe, int enhetsId, String saksbehandlersValgteEnhet) {
        WSTildelFlereOppgaverResponse response = tildelOppgaveWS.tildelFlereOppgaver(
                new WSTildelFlereOppgaverRequest()
                        .withUnderkategori(underkategoriKode(temagruppe))
                        .withOppgavetype(SPORSMAL_OG_SVAR)
                        .withFagomrade(KONTAKT_NAV)
                        .withAnsvarligEnhetId(enhetFor(temagruppe, saksbehandlersValgteEnhet))
                        .withIkkeTidligereTildeltSaksbehandlerId(getSubjectHandler().getUid())
                        .withTildeltAvEnhetId(enhetsId)
                        .withTildelesSaksbehandlerId(getSubjectHandler().getUid()));

        if (response == null) {
            return emptyList();
        }

        return response.getOppgaveIder().stream()
                .map(this::hentTildeltOppgave)
                .filter(Objects::nonNull)
                .map(WSHentOppgaveResponse::getOppgave)
                .collect(toList());
    }

    private WSHentOppgaveResponse hentTildeltOppgave(Integer oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest()
                    .withOppgaveId(String.valueOf(oppgaveId)));
        } catch (HentOppgaveOppgaveIkkeFunnet exc) {
            throw new IllegalStateException(exc);
        }
    }

    private String enhetFor(Temagruppe temagruppe, String saksbehandlersValgteEnhet) {
        return enhetFor(ofNullable(temagruppe), saksbehandlersValgteEnhet);
    }

    private String enhetFor(Optional<Temagruppe> optional, String saksbehandlersValgteEnhet) {
        if (!optional.isPresent()) {
            return DEFAULT_ENHET.toString();
        }
        Temagruppe temagruppe = optional.get();

        if(temagruppe.equals(FMLI) && saksbehandlersValgteEnhet.equals(STORD_ENHET)){
            return STORD_ENHET;
        } else if (asList(ARBD, FMLI, ORT_HJE, PENS, UFRT, PLEIEPENGERSY, UTLAND).contains(temagruppe)) {
            return DEFAULT_ENHET.toString();
        } else {
            return saksbehandlersValgteEnhet;
        }
    }

    private static String underkategoriKode(Temagruppe temagruppe) {
        return temagruppe + "_KNA";
    }

    public static WSEndreOppgave tilWSEndreOppgave(WSOppgave wsOppgave) {
        return new WSEndreOppgave()
                .withOppgaveId(wsOppgave.getOppgaveId())
                .withAnsvarligId(wsOppgave.getAnsvarligId())
                .withBrukerId(wsOppgave.getGjelder().getBrukerId())
                .withDokumentId(wsOppgave.getDokumentId())
                .withKravId(wsOppgave.getKravId())
                .withAnsvarligEnhetId(wsOppgave.getAnsvarligEnhetId())

                .withFagomradeKode(wsOppgave.getFagomrade().getKode())
                .withOppgavetypeKode(wsOppgave.getOppgavetype().getKode())
                .withPrioritetKode(wsOppgave.getPrioritet().getKode())
                .withBrukertypeKode(wsOppgave.getGjelder().getBrukertypeKode())
                .withUnderkategoriKode(wsOppgave.getUnderkategori().getKode())

                .withAktivFra(wsOppgave.getAktivFra())
                .withBeskrivelse(wsOppgave.getBeskrivelse())
                .withVersjon(wsOppgave.getVersjon())
                .withSaksnummer(wsOppgave.getSaksnummer())
                .withLest(wsOppgave.isLest());
    }

}
