package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Oppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSUnderkategori;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeFilter;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeRequest;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeSok;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSFinnOppgaveListeSortering;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
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
import static org.apache.commons.lang3.StringUtils.isBlank;

public class OppgaveBehandlingService {

    public static final int ENHET = 4112;

    @Inject
    private OppgavebehandlingV3 oppgavebehandlingWS;

    @Inject
    private OppgaveV3 oppgaveWS;

    public void tilordneOppgaveIGsak(String oppgaveId) {
        tilordneOppgaveIGsak(hentOppgaveFraGsak(oppgaveId));
    }

    public Optional<Oppgave> plukkOppgaveFraGsak(String temagruppe) {
        Optional<WSOppgave> oppgave = finnIkkeTilordnedeOppgaver(temagruppe);
        if (oppgave.isSome()) {
            WSOppgave tilordnet = tilordneOppgaveIGsak(oppgave.get());
            return optional(new Oppgave(tilordnet.getOppgaveId(), tilordnet.getGjelder().getBrukerId()));
        } else {
            return none();
        }
    }

    public void ferdigstillOppgaveIGsak(Optional<String> oppgaveId) {
        if (oppgaveId.isSome()) {
            oppgavebehandlingWS.ferdigstillOppgaveBolk(new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgaveId.get()).withFerdigstiltAvEnhetId(ENHET));
        }
    }

    public void leggTilbakeOppgaveIGsak(Optional<String> oppgaveId, String beskrivelse, String temagruppe) {
        if (oppgaveId.isSome()) {
            WSOppgave wsOppgave = hentOppgaveFraGsak(oppgaveId.get());
            wsOppgave.withAnsvarligId("");
            wsOppgave.withBeskrivelse(leggTilBeskrivelse(wsOppgave.getBeskrivelse(), beskrivelse));
            if (!isBlank(temagruppe)) {
                wsOppgave.withUnderkategori(new WSUnderkategori().withKode(underkategoriKode(temagruppe)));
            }
            lagreOppgaveIGsak(wsOppgave);
        }
    }

    public void systemLeggTilbakeOppgaveIGsak(String oppgaveId) {
        WSOppgave wsOppgave = hentOppgaveFraGsak(oppgaveId).withAnsvarligId("");
        lagreOppgaveIGsak(wsOppgave);
    }

    private static String leggTilBeskrivelse(String gammelBeskrivelse, String leggTil) {
        return isBlank(gammelBeskrivelse) ? leggTil : gammelBeskrivelse + "\n" + leggTil;
    }

    private WSOppgave hentOppgaveFraGsak(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException(hentOppgaveOppgaveIkkeFunnet);
        }
    }

    private WSOppgave tilordneOppgaveIGsak(WSOppgave oppgave) {
        WSOppgave wsOppgave = oppgave.withAnsvarligId(getSubjectHandler().getUid());
        lagreOppgaveIGsak(wsOppgave);
        return wsOppgave;
    }

    private void lagreOppgaveIGsak(WSOppgave wsOppgave) {
        try {
            oppgavebehandlingWS.lagreOppgave(
                    new WSLagreOppgaveRequest()
                            .withEndreOppgave(tilWSEndreOppgave(wsOppgave))
                            .withEndretAvEnhetId(ENHET)
            );
        } catch (LagreOppgaveOppgaveIkkeFunnet lagreOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", lagreOppgaveOppgaveIkkeFunnet);
        } catch (LagreOppgaveOptimistiskLasing lagreOppgaveOptimistiskLasing) {
            //TODO: Hva skal skje ved optimistisk låsing. Noen andre har låst filen.
            throw new RuntimeException("Oppgaven kunne ikke lagres, den er for øyeblikket låst av en annen bruker.", lagreOppgaveOptimistiskLasing);
        }
    }

    private Optional<WSOppgave> finnIkkeTilordnedeOppgaver(String temagruppe) {
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

}
