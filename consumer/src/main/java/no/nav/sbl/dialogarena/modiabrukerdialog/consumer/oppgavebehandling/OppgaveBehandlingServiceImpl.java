package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.oppgavebehandling;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.TildelOppgaveUgyldigInput;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.*;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;

public class OppgaveBehandlingServiceImpl implements OppgaveBehandlingService {

    private static final Logger logger = LoggerFactory.getLogger(OppgaveBehandlingServiceImpl.class);
    public static final Integer DEFAULT_ENHET = 4100;
    public static final String KODE_OPPGAVE_FERDIGSTILT = "F";
    public static final String SPORSMAL_OG_SVAR = "SPM_OG_SVR";
    public static final String KONTAKT_NAV = "KNA";

    private final OppgavebehandlingV3 oppgavebehandlingWS;
    private final OppgaveV3 oppgaveWS;
    private final SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    private final AnsattService ansattWS;
    private final Ruting ruting;
    private LeggTilbakeOppgaveIGsakDelegate leggTilbakeOppgaveIGsakDelegate;

    @Inject
    public OppgaveBehandlingServiceImpl(OppgavebehandlingV3 oppgavebehandlingWS, OppgaveV3 oppgaveWS, SaksbehandlerInnstillingerService saksbehandlerInnstillingerService, AnsattService ansattWS, Ruting ruting) {
        this.oppgavebehandlingWS = oppgavebehandlingWS;
        this.oppgaveWS = oppgaveWS;
        this.saksbehandlerInnstillingerService = saksbehandlerInnstillingerService;
        this.ansattWS = ansattWS;
        this.ruting = ruting;
        this.leggTilbakeOppgaveIGsakDelegate = new LeggTilbakeOppgaveIGsakDelegate(this, ruting);
    }

    @Override
    public void tilordneOppgaveIGsak(String oppgaveId, Temagruppe temagruppe) throws FikkIkkeTilordnet {
        tilordneOppgaveIGsak(oppgaveId, optional(temagruppe));
    }

    @Override
    public Optional<Oppgave> plukkOppgaveFraGsak(Temagruppe temagruppe) {
        int enhetsId = Integer.parseInt(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
        Optional<WSOppgave> tilordnetOptional = tildelEldsteLedigeOppgave(temagruppe, enhetsId);
        if (tilordnetOptional.isSome()) {
            WSOppgave tilordnet = tilordnetOptional.get();
            return optional(new Oppgave(tilordnet.getOppgaveId(), tilordnet.getGjelder().getBrukerId(), tilordnet.getHenvendelseId()));
        } else {
            return none();
        }
    }

    @Override
    public void ferdigstillOppgaveIGsak(String oppgaveId, Temagruppe temagruppe) {
        ferdigstillOppgaveIGsak(oppgaveId, optional(temagruppe));
    }

    @Override
    public void ferdigstillOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe) {
        try {
            WSOppgave oppgave = oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
            oppgave.withBeskrivelse(leggTilBeskrivelse(oppgave.getBeskrivelse(), "Oppgaven er ferdigstilt i Modia"));
            lagreOppgaveIGsak(oppgave, temagruppe);

            oppgavebehandlingWS.ferdigstillOppgaveBolk(new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgaveId).withFerdigstiltAvEnhetId(Integer.valueOf(enhetFor(temagruppe))));
        } catch (HentOppgaveOppgaveIkkeFunnet | LagreOppgaveOptimistiskLasing e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void leggTilbakeOppgaveIGsak(String oppgaveId, String beskrivelse, Temagruppe temagruppe) {
        if (oppgaveId == null) {
            return;
        }

        WSOppgave oppgaveFraGsak = hentOppgaveFraGsak(oppgaveId);
        leggTilbakeOppgaveIGsakDelegate.leggTilbake(oppgaveFraGsak, beskrivelse, temagruppe);
    }

    @Override
    public void systemLeggTilbakeOppgaveIGsak(String oppgaveId, Temagruppe temagruppe) {
        try {
            WSOppgave wsOppgave = hentOppgaveFraGsak(oppgaveId).withAnsvarligId("");
            lagreOppgaveIGsak(wsOppgave, temagruppe);
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing);
        }
    }

    @Override
    public boolean oppgaveErFerdigstilt(String oppgaveid) {
        return equalsIgnoreCase(hentOppgaveFraGsak(oppgaveid).getStatus().getKode(), KODE_OPPGAVE_FERDIGSTILT);
    }

    String leggTilBeskrivelse(String gammelBeskrivelse, String leggTil) {
        String ident = getSubjectHandler().getUid();
        String header = String.format("--- %s %s (%s, %s) ---\n",
                forPattern("dd.MM.yyyy HH:mm").print(now()),
                ansattWS.hentAnsattNavn(ident),
                ident,
                saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());

        String nyBeskrivelse = header + leggTil;
        return isBlank(gammelBeskrivelse) ? nyBeskrivelse :  nyBeskrivelse + "\n\n" + gammelBeskrivelse;
    }

    WSOppgave hentOppgaveFraGsak(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
    }

    private void tilordneOppgaveIGsak(String oppgaveId, Optional<Temagruppe> temagruppe) throws FikkIkkeTilordnet {
        tilordneOppgaveIGsak(hentOppgaveFraGsak(oppgaveId), temagruppe);
    }

    private WSOppgave tilordneOppgaveIGsak(WSOppgave oppgave, Optional<Temagruppe> temagruppe) throws FikkIkkeTilordnet {
        try {
            WSOppgave wsOppgave = oppgave.withAnsvarligId(getSubjectHandler().getUid());
            lagreOppgaveIGsak(wsOppgave, temagruppe);
            return wsOppgave;
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new FikkIkkeTilordnet(lagreOppgaveOptimistiskLasing);
        }
    }

    void lagreOppgaveIGsak(WSOppgave wsOppgave, Temagruppe temagruppe) throws LagreOppgaveOptimistiskLasing {
        lagreOppgaveIGsak(wsOppgave, optional(temagruppe));
    }

    private void lagreOppgaveIGsak(WSOppgave wsOppgave, Optional<Temagruppe> temagruppe) throws LagreOppgaveOptimistiskLasing {
        try {
            oppgavebehandlingWS.lagreOppgave(
                    new WSLagreOppgaveRequest()
                            .withEndreOppgave(tilWSEndreOppgave(wsOppgave))
                            .withEndretAvEnhetId(Integer.valueOf(enhetFor(temagruppe)))
            );
        } catch (LagreOppgaveOppgaveIkkeFunnet lagreOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", lagreOppgaveOppgaveIkkeFunnet);
        }
    }

    private Optional<WSOppgave> tildelEldsteLedigeOppgave(Temagruppe temagruppe, int enhetsId) {
        WSOppgave oppgave;
        try {
            String tildeltOppgaveId = oppgavebehandlingWS.tildelOppgave(
                    new WSTildelOppgaveRequest()
                            .withFilter(new WSTildelOppgaveFilter()
                                    .withOppgavetypeKodeListe(SPORSMAL_OG_SVAR)
                                    .withUnderkategoriKode(underkategoriKode(temagruppe)))
                            .withSok(new WSTildelOppgaveSok()
                                    .withAnsvarligEnhetId(enhetFor(temagruppe))
                                    .withFagomradeKodeListe(KONTAKT_NAV))
                            .withIkkeTidligereTildeltSaksbehandlerId(getSubjectHandler().getUid())
                            .withTildeltAvEnhetId(enhetsId)
                            .withTildelesSaksbehandlerId(getSubjectHandler().getUid()))
                    .getOppgaveId();

            if (tildeltOppgaveId == null) {
                return none();
            }
            oppgave = oppgaveWS.hentOppgave(
                    new WSHentOppgaveRequest()
                            .withOppgaveId(tildeltOppgaveId))
                    .getOppgave();

        } catch (TildelOppgaveUgyldigInput exc) {
            logger.warn(exc.getFaultInfo().getErrorMessage());
            return none();
        } catch (HentOppgaveOppgaveIkkeFunnet exc) {
            logger.warn(exc.getFaultInfo().getErrorMessage());
            return none();
        }

        return optional(oppgave);
    }

    private String enhetFor(Temagruppe temagruppe) {
        return enhetFor(optional(temagruppe));
    }

    private String enhetFor(Optional<Temagruppe> optional) {
        if (!optional.isSome()) {
            return DEFAULT_ENHET.toString();
        }
        Temagruppe temagruppe = optional.get();
        if (asList(ARBD, FMLI, ORT_HJE, PENS, UFRT, PLEIEPENGERSY, UTLAND).contains(temagruppe)) {
            return DEFAULT_ENHET.toString();
        } else {
            return saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();
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
