package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Oppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSUnderkategori;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.*;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSEndreOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSFerdigstillOppgaveBolkRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSLagreOppgaveRequest;

import javax.inject.Inject;

import static java.lang.String.valueOf;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;

public class OppgaveBehandlingService {

    public static final int ENHET = 4100;
    public static final int ANTALL_PLUKK_FORSOK = 20;
    public static final String KODE_OPPGAVE_FERDIGSTILT = "F";

    @Inject
    private OppgavebehandlingV3 oppgavebehandlingWS;
    @Inject
    private OppgaveV3 oppgaveWS;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private AnsattService ansattWS;

    public void tilordneOppgaveIGsak(String oppgaveId) throws FikkIkkeTilordnet {
        tilordneOppgaveIGsak(hentOppgaveFraGsak(oppgaveId));
    }

    public Optional<Oppgave> plukkOppgaveFraGsak(String temagruppe) {
        return plukkOppgaveFraGsak(temagruppe, ANTALL_PLUKK_FORSOK);
    }

    private Optional<Oppgave> plukkOppgaveFraGsak(String temagruppe, int antallForsokIgjen) {
        if (antallForsokIgjen <= 0) {
            return none();
        }

        Optional<WSOppgave> oppgave = finnEldsteIkkeTilordnedeOppgave(temagruppe);
        if (oppgave.isSome()) {
            try {
                WSOppgave tilordnet = tilordneOppgaveIGsak(oppgave.get());
                return optional(new Oppgave(tilordnet.getOppgaveId(), tilordnet.getGjelder().getBrukerId(), tilordnet.getHenvendelseId()));
            } catch (FikkIkkeTilordnet fikkIkkeTilordnet) {
                return plukkOppgaveFraGsak(temagruppe, antallForsokIgjen - 1);
            }
        } else {
            return none();
        }
    }

    public void ferdigstillOppgaveIGsak(Optional<String> oppgaveId) {
        if (oppgaveId.isSome()) {
            try {
                WSOppgave oppgave = oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId.get())).getOppgave();
                oppgave.withBeskrivelse(leggTilBeskrivelse(oppgave.getBeskrivelse(), "Oppgaven er ferdigstilt i Modia"));
                lagreOppgaveIGsak(oppgave);

                oppgavebehandlingWS.ferdigstillOppgaveBolk(new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgaveId.get()).withFerdigstiltAvEnhetId(ENHET));
            } catch (HentOppgaveOppgaveIkkeFunnet | LagreOppgaveOptimistiskLasing e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void leggTilbakeOppgaveIGsak(Optional<String> oppgaveId, String beskrivelse, String temagruppe) {
        if (oppgaveId.isSome()) {
            try {
                WSOppgave wsOppgave = hentOppgaveFraGsak(oppgaveId.get());
                wsOppgave.withAnsvarligId("");
                wsOppgave.withBeskrivelse(leggTilBeskrivelse(wsOppgave.getBeskrivelse(), beskrivelse));
                if (!isBlank(temagruppe)) {
                    wsOppgave.withUnderkategori(new WSUnderkategori().withKode(underkategoriKode(temagruppe)));
                }

                lagreOppgaveIGsak(wsOppgave);
            } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
                throw new RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing);
            }
        }
    }

    public void systemLeggTilbakeOppgaveIGsak(String oppgaveId) {
        try {
            WSOppgave wsOppgave = hentOppgaveFraGsak(oppgaveId).withAnsvarligId("");
            lagreOppgaveIGsak(wsOppgave);
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing);
        }
    }

    public boolean oppgaveErFerdigstilt(String oppgaveid) {
        return equalsIgnoreCase(hentOppgaveFraGsak(oppgaveid).getStatus().getKode(), KODE_OPPGAVE_FERDIGSTILT);
    }

    private String leggTilBeskrivelse(String gammelBeskrivelse, String leggTil) {
        String ident = getSubjectHandler().getUid();
        String header = String.format("--- %s %s (%s, %s) ---\n",
                forPattern("dd.MM.yyyy HH:mm").print(now()),
                ansattWS.hentAnsattNavn(ident),
                ident,
                saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());

        String nyBeskrivelse = header + leggTil;
        return isBlank(gammelBeskrivelse) ? nyBeskrivelse : gammelBeskrivelse + "\n\n" + nyBeskrivelse;
    }

    private WSOppgave hentOppgaveFraGsak(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
    }

    private WSOppgave tilordneOppgaveIGsak(WSOppgave oppgave) throws FikkIkkeTilordnet {
        try {
            WSOppgave wsOppgave = oppgave.withAnsvarligId(getSubjectHandler().getUid());
            lagreOppgaveIGsak(wsOppgave);
            return wsOppgave;
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            throw new FikkIkkeTilordnet(lagreOppgaveOptimistiskLasing);
        }
    }

    private void lagreOppgaveIGsak(WSOppgave wsOppgave) throws LagreOppgaveOptimistiskLasing {
        try {
            oppgavebehandlingWS.lagreOppgave(
                    new WSLagreOppgaveRequest()
                            .withEndreOppgave(tilWSEndreOppgave(wsOppgave))
                            .withEndretAvEnhetId(ENHET)
            );
        } catch (LagreOppgaveOppgaveIkkeFunnet lagreOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", lagreOppgaveOppgaveIkkeFunnet);
        }
    }

    private Optional<WSOppgave> finnEldsteIkkeTilordnedeOppgave(String temagruppe) {
        return on(oppgaveWS.finnOppgaveListe(
                new WSFinnOppgaveListeRequest()
                        .withFilter(new WSFinnOppgaveListeFilter()
                                .withOpprettetEnhetId(valueOf(ENHET))
                                .withOppgavetypeKodeListe("SPM_OG_SVR")
                                .withUnderkategoriKode(underkategoriKode(temagruppe))
                                .withMaxAntallSvar(0)
                                .withUfordelte(true))
                        .withSok(new WSFinnOppgaveListeSok()
                                .withAnsvarligEnhetId(valueOf(ENHET))
                                .withFagomradeKodeListe("KNA"))
                        .withSorteringKode(new WSFinnOppgaveListeSortering()
                                .withSorteringKode("STIGENDE")
                                .withSorteringselementKode("OPPRETTET_DATO"))
                        .withIkkeTidligereFordeltTil(getSubjectHandler().getUid()))
                .getOppgaveListe())
                .head();
    }

    private static String underkategoriKode(String temagruppe) {
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

    public static class FikkIkkeTilordnet extends Exception {
        public FikkIkkeTilordnet(Throwable cause) {
            super(cause);
        }
    }
}
