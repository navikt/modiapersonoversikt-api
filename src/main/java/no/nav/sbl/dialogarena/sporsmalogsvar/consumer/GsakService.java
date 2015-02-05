package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.GsakKodeTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.AnsattService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.LagreOppgaveOptimistiskLasing;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.*;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSEnhet;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeRequest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import org.apache.commons.collections15.Transformer;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.ukedagerFraDato;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;

public class GsakService {

    private static final Logger logger = LoggerFactory.getLogger(GsakService.class);

    public static final int DEFAULT_OPPRETTET_AV_ENHET_ID = 2820;
    public static final String HENVENDELSESTYPE_KODE = "BESVAR_KNA";
    public static final String KODE_OPPGAVE_FERDIGSTILT = "F";
    public static final String KODE_KONTAKT_NAV = "KNA";

    @Inject
    private OppgaveV3 oppgaveWS;
    @Inject
    private OppgavebehandlingV3 oppgavebehandlingWS;
    @Inject
    private Ruting ruting;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private AnsattService ansattWS;

    public List<AnsattEnhet> hentForeslatteEnheter(String fnr, String tema, String type, Optional<GsakKodeTema.Underkategori> underkategori) {
        try {
            WSFinnAnsvarligEnhetForOppgavetypeRequest request = new WSFinnAnsvarligEnhetForOppgavetypeRequest()
                    .withAlleEnheter(true)
                    .withBrukerId(fnr)
                    .withFagomradeKode(tema)
                    .withOppgaveKode(type);

            if (underkategori.isSome() && !isBlank(underkategori.get().kode)) {
                request.withGjelderKode(underkategori.get().kode);
            }

            WSFinnAnsvarligEnhetForOppgavetypeResponse enhetForOppgaveResponse = ruting.finnAnsvarligEnhetForOppgavetype(request);

            List<WSEnhet> wsEnheter = enhetForOppgaveResponse.getEnhetListe();

            return on(wsEnheter).map(new Transformer<WSEnhet, AnsattEnhet>() {
                @Override
                public AnsattEnhet transform(WSEnhet wsEnhet) {
                    return new AnsattEnhet(wsEnhet.getEnhetId(), wsEnhet.getEnhetNavn());
                }
            }).collect();
        } catch (Exception e) {
            return emptyList();
        }
    }

    public boolean oppgaveKanManuelltAvsluttes(String oppgaveId) {
        WSOppgave wsOppgave = hentOppgave(oppgaveId);

        boolean kontaktNav = equalsIgnoreCase(wsOppgave.getFagomrade().getKode(), KODE_KONTAKT_NAV);
        boolean ferdigstilt = oppgaveErFerdigstilt(wsOppgave);

        return !kontaktNav && !ferdigstilt;
    }

    private boolean oppgaveErFerdigstilt(WSOppgave wsOppgave) {
        return equalsIgnoreCase(wsOppgave.getStatus().getKode(), KODE_OPPGAVE_FERDIGSTILT);
    }

    public WSOppgave hentOppgave(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Fant ikke oppgave med id: " + oppgaveId, hentOppgaveOppgaveIkkeFunnet);
        }
    }

    public void ferdigstillGsakOppgave(Optional<String> oppgaveId, String beskrivelse) throws LagreOppgaveOptimistiskLasing, OppgaveErFerdigstilt {
        if (oppgaveId.isSome()) {
            try {
                WSOppgave oppgave = oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId.get())).getOppgave();

                if (oppgaveErFerdigstilt(oppgave)) {
                    throw new OppgaveErFerdigstilt(new Throwable("Oppgaven er allerede ferdigstilt"));
                }

                String nyBeskrivelse = "Oppgaven er ferdigstilt i Modia med beskrivelse:\n" + beskrivelse;
                String valgtEnhetIdString = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();
                int valgtEnhetId = Integer.parseInt(valgtEnhetIdString);
                oppgave.withBeskrivelse(leggTilBeskrivelse(oppgave.getBeskrivelse(), nyBeskrivelse, valgtEnhetIdString));

                lagreGsakOppgave(oppgave, valgtEnhetId);

                oppgavebehandlingWS.ferdigstillOppgaveBolk(new WSFerdigstillOppgaveBolkRequest().withOppgaveIdListe(oppgaveId.get()).withFerdigstiltAvEnhetId(valgtEnhetId));
            } catch (HentOppgaveOppgaveIkkeFunnet | NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void opprettGsakOppgave(NyOppgave nyOppgave) {
        int valgtEnhetId;
        String valgtEnhetIdString = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();
        try {
            valgtEnhetId = Integer.parseInt(valgtEnhetIdString);
        } catch (NumberFormatException e) {
            logger.error(String.format("EnhetId %s kunne ikke gjøres om til Integer", valgtEnhetIdString));
            valgtEnhetId = DEFAULT_OPPRETTET_AV_ENHET_ID;
        }

        String beskrivelse = "Oppgave opprettet fra Modia med beskrivelse:\n" + nyOppgave.beskrivelse;

        GsakKodeTema.Underkategori underkategori = optional(nyOppgave.underkategori).getOrElse(new GsakKodeTema.Underkategori(null, null));

        oppgavebehandlingWS.opprettOppgave(
                new WSOpprettOppgaveRequest()
                        .withOpprettetAvEnhetId(valgtEnhetId)
                        .withHenvendelsetypeKode(HENVENDELSESTYPE_KODE)
                        .withOpprettOppgave(
                                new WSOpprettOppgave()
                                        .withHenvendelseId(nyOppgave.henvendelseId)
                                        .withAktivFra(LocalDate.now())
                                        .withAktivTil(ukedagerFraDato(nyOppgave.type.dagerFrist, LocalDate.now()))
                                        .withAnsvarligEnhetId(nyOppgave.enhet.enhetId)
                                        .withBeskrivelse(leggTilBeskrivelse(beskrivelse, valgtEnhetIdString))
                                        .withFagomradeKode(nyOppgave.tema.kode)
                                        .withUnderkategoriKode(underkategori.kode)
                                        .withBrukerId(nyOppgave.brukerId)
                                        .withOppgavetypeKode(nyOppgave.type.kode)
                                        .withPrioritetKode(nyOppgave.prioritet.kode)
                                        .withLest(false)
                        )
        );
    }

    private String leggTilBeskrivelse(String beskrivelse, String valgtEnhetId) {
        return leggTilBeskrivelse("", beskrivelse, valgtEnhetId);
    }

    private String leggTilBeskrivelse(String gammelBeskrivelse, String leggTil, String valgtEnhetId) {
        String ident = getSubjectHandler().getUid();
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

    public static class OppgaveErFerdigstilt extends Exception {
        public OppgaveErFerdigstilt(Throwable cause) {
            super(cause);
        }
    }
}
