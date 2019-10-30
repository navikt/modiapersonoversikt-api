package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.common.auth.SubjectHandler;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.GsakKodeTema;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.norg.AnsattService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.arbeidsdagerFraDato;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;

public class GsakServiceImpl implements GsakService {

    private static final Logger logger = LoggerFactory.getLogger(GsakServiceImpl.class);

    public static final int DEFAULT_OPPRETTET_AV_ENHET_ID = 2820;
    public static final String HENVENDELSESTYPE_KODE = "DIALOG";
    public static final String KODE_OPPGAVE_FERDIGSTILT = "F";
    public static final String KODE_KONTAKT_NAV = "KNA";

    @Inject
    private OppgaveV3 oppgaveWS;
    @Inject
    private OppgavebehandlingV3 oppgavebehandlingWS;
    @Inject
    private AnsattService ansattWS;

    @Override
    public boolean oppgaveKanManuelltAvsluttes(String oppgaveId) {
        WSOppgave wsOppgave = hentOppgave(oppgaveId);

        boolean kontaktNav = equalsIgnoreCase(wsOppgave.getFagomrade().getKode(), KODE_KONTAKT_NAV);
        boolean ferdigstilt = oppgaveErFerdigstilt(wsOppgave);

        return !kontaktNav && !ferdigstilt;
    }

    private boolean oppgaveErFerdigstilt(WSOppgave wsOppgave) {
        return equalsIgnoreCase(wsOppgave.getStatus().getKode(), KODE_OPPGAVE_FERDIGSTILT);
    }

    @Override
    public WSOppgave hentOppgave(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Fant ikke oppgave med id: " + oppgaveId, hentOppgaveOppgaveIkkeFunnet);
        }
    }

    @Override
    public void ferdigstillGsakOppgave(String enhetId, WSOppgave oppgave, String beskrivelse) throws LagreOppgaveOptimistiskLasing, OppgaveErFerdigstilt {
        int valgtEnhetId = Integer.parseInt(enhetId);
        try {
            String nyBeskrivelse = "Oppgaven er ferdigstilt i Modia med beskrivelse:\n" + beskrivelse;
            oppgave.withBeskrivelse(leggTilBeskrivelse(oppgave.getBeskrivelse(), nyBeskrivelse, enhetId));
            lagreGsakOppgave(oppgave, valgtEnhetId);
        } catch (LagreOppgaveOptimistiskLasing e) {
            logger.error("LagreOppgaveOptimistiskLasing feil i oppdatering av beskrivelse for oppgave " + oppgave.getOppgaveId(), e);
            if (oppgaveErFerdigstilt(hentOppgave(oppgave.getOppgaveId()))) {
                throw new OppgaveErFerdigstilt(e);
            }
            throw e;
        }
        oppgavebehandlingWS.ferdigstillOppgaveBolk(new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgave.getOppgaveId()).withFerdigstiltAvEnhetId(valgtEnhetId));
    }

    @Override
    public void opprettGsakOppgave(String enhetId, NyOppgave nyOppgave) {
        int valgtEnhetId;
        try {
            valgtEnhetId = Integer.parseInt(enhetId);
        } catch (NumberFormatException e) {
            logger.error(String.format("EnhetId %s kunne ikke gjøres om til Integer", enhetId));
            valgtEnhetId = DEFAULT_OPPRETTET_AV_ENHET_ID;
        }

        String beskrivelse = "Fra Modia:\n" + nyOppgave.beskrivelse;

        GsakKodeTema.Underkategori underkategori = nyOppgave.underkategori != null ? nyOppgave.underkategori : new GsakKodeTema.Underkategori(null, null);
        GsakKodeTema.Prioritet prioritet = nyOppgave.prioritet != null ? nyOppgave.prioritet : new GsakKodeTema.Prioritet("NORM_" + nyOppgave.tema.kode, "Normal");

        oppgavebehandlingWS.opprettOppgave(
                new WSOpprettOppgaveRequest()
                        .withOpprettetAvEnhetId(valgtEnhetId)
                        .withHenvendelsetypeKode(HENVENDELSESTYPE_KODE)
                        .withOpprettOppgave(
                                new WSOpprettOppgave()
                                        .withHenvendelseId(nyOppgave.henvendelseId)
                                        .withAktivFra(LocalDate.now())
                                        .withAktivTil(arbeidsdagerFraDato(nyOppgave.type.dagerFrist, LocalDate.now()))
                                        .withAnsvarligEnhetId(nyOppgave.enhet.enhetId)
                                        .withAnsvarligId(nyOppgave.valgtAnsatt != null ? nyOppgave.valgtAnsatt.ident : null)
                                        .withBeskrivelse(leggTilBeskrivelse(beskrivelse, enhetId))
                                        .withFagomradeKode(nyOppgave.tema.kode)
                                        .withUnderkategoriKode(underkategori.kode)
                                        .withBrukerId(nyOppgave.brukerId)
                                        .withOppgavetypeKode(nyOppgave.type.kode)
                                        .withPrioritetKode(prioritet.kode)
                                        .withLest(false)
                        )
        );
    }

    private String leggTilBeskrivelse(String beskrivelse, String valgtEnhetId) {
        return leggTilBeskrivelse("", beskrivelse, valgtEnhetId);
    }

    private String leggTilBeskrivelse(String gammelBeskrivelse, String leggTil, String valgtEnhetId) {
        String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
        String header = String.format("--- %s %s (%s, %s) ---\n",
                forPattern("dd.MM.yyyy HH:mm").print(now()),
                ansattWS.hentAnsattNavn(ident),
                ident,
                valgtEnhetId);

        String nyBeskrivelse = header + leggTil;
        return isBlank(gammelBeskrivelse) ? nyBeskrivelse : gammelBeskrivelse + "\n\n" + nyBeskrivelse;
    }

    private void lagreGsakOppgave(WSOppgave wsOppgave, int endretAvEnhetId) throws LagreOppgaveOptimistiskLasing {
        try {
            oppgavebehandlingWS.lagreOppgave(
                    new WSLagreOppgaveRequest()
                            .withEndreOppgave(tilWSEndreOppgave(wsOppgave))
                            .withEndretAvEnhetId(endretAvEnhetId));

        } catch (LagreOppgaveOppgaveIkkeFunnet lagreOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Oppgaven ble ikke funnet ved tilordning til saksbehandler", lagreOppgaveOppgaveIkkeFunnet);
        }
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
