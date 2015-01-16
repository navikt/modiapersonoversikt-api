package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import _0._0.nav_cons_sak_gosys_3.no.nav.asbo.navansatt.ASBOGOSYSNAVAnsatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.GOSYSNAVansatt;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSGeneriskfMsg;
import _0._0.nav_cons_sak_gosys_3.no.nav.inf.navansatt.HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg;
import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.oppgave.v3.HentOppgaveOppgaveIkkeFunnet;
import no.nav.tjeneste.virksomhet.oppgave.v3.OppgaveV3;
import no.nav.tjeneste.virksomhet.oppgave.v3.informasjon.oppgave.WSOppgave;
import no.nav.tjeneste.virksomhet.oppgave.v3.meldinger.WSHentOppgaveRequest;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSEnhet;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeRequest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForOppgavetypeResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.core.context.SubjectHandler.getSubjectHandler;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.ukedagerFraDato;
import static org.joda.time.DateTime.now;
import static org.joda.time.format.DateTimeFormat.forPattern;

public class GsakService {

    private static final Logger logger = LoggerFactory.getLogger(GsakService.class);
    public static final int DEFAULT_OPPRETTET_AV_ENHET_ID = 2820;
    public static final String HENVENDELSESTYPE_KODE = "BESVAR_KNA";

    @Inject
    private OppgaveV3 oppgaveWS;
    @Inject
    private OppgavebehandlingV3 oppgavebehandling;
    @Inject
    private no.nav.virksomhet.tjenester.sak.v1.Sak sakWs;
    @Inject
    private Ruting ruting;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private GOSYSNAVansatt ansattWS;

    public List<Sak> hentSakerForBruker(String fnr) {
        WSFinnGenerellSakListeResponse response = sakWs.finnGenerellSakListe(new WSFinnGenerellSakListeRequest().withBrukerId(fnr));
        return on(response.getSakListe()).map(TIL_SAK).collectIn(new ArrayList<Sak>());
    }

    public Optional<AnsattEnhet> hentForeslattEnhet(String fnr, String tema, String type) {
        try {
            WSFinnAnsvarligEnhetForOppgavetypeResponse enhetForOppgaveResponse = ruting.finnAnsvarligEnhetForOppgavetype(
                    new WSFinnAnsvarligEnhetForOppgavetypeRequest()
                            .withAlleEnheter(true)
                            .withBrukerId(fnr)
                            .withFagomradeKode(tema)
                            .withOppgaveKode(type));

            WSEnhet wsEnhet = enhetForOppgaveResponse.getEnhetListe().get(0);
            return optional(new AnsattEnhet(wsEnhet.getEnhetId(), wsEnhet.getEnhetNavn()));
        } catch (Exception e) {
            return none();
        }
    }

    public static final Transformer<WSGenerellSak, Sak> TIL_SAK = new Transformer<WSGenerellSak, Sak>() {
        @Override
        public Sak transform(WSGenerellSak wsGenerellSak) {
            Sak sak = new Sak();
            sak.opprettetDato = wsGenerellSak.getEndringsinfo().getOpprettetDato();
            sak.saksId = wsGenerellSak.getSakId();
            sak.temaKode = wsGenerellSak.getFagomradeKode();
            sak.sakstype = wsGenerellSak.getSakstypeKode();
            sak.fagsystemKode = wsGenerellSak.getFagsystemKode();
            return sak;
        }
    };

    public boolean oppgaveKanManuelltAvsluttes(String oppgaveId) {
        return !hentOppgave(oppgaveId).getFagomrade().getKode().equals("KNA");
    }

    public WSOppgave hentOppgave(String oppgaveId) {
        try {
            return oppgaveWS.hentOppgave(new WSHentOppgaveRequest().withOppgaveId(oppgaveId)).getOppgave();
        } catch (HentOppgaveOppgaveIkkeFunnet hentOppgaveOppgaveIkkeFunnet) {
            throw new RuntimeException("Fant ikke oppgave med id: " + oppgaveId, hentOppgaveOppgaveIkkeFunnet);
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
        oppgavebehandling.opprettOppgave(
                new WSOpprettOppgaveRequest()
                        .withOpprettetAvEnhetId(valgtEnhetId)
                        .withHenvendelsetypeKode(HENVENDELSESTYPE_KODE)
                        .withOpprettOppgave(
                                new WSOpprettOppgave()
                                        .withHenvendelseId(nyOppgave.henvendelseId)
                                        .withAktivFra(LocalDate.now())
                                        .withAktivTil(ukedagerFraDato(nyOppgave.type.dagerFrist, LocalDate.now()))
                                        .withAnsvarligEnhetId(nyOppgave.enhet.enhetId)
                                        .withBeskrivelse(lagBeskrivelse(nyOppgave.beskrivelse, valgtEnhetIdString))
                                        .withFagomradeKode(nyOppgave.tema.kode)
                                        .withBrukerId(nyOppgave.brukerId)
                                        .withOppgavetypeKode(nyOppgave.type.kode)
                                        .withPrioritetKode(nyOppgave.prioritet.kode)
                                        .withLest(false)
                        )
        );
    }

    private String lagBeskrivelse(String beskrivelse, String valgtEnhetId) {
        String ident = getSubjectHandler().getUid();
        String header = String.format("--- %s %s (%s, %s) ---\nOppgave opprettet fra Modia med beskrivelse:\n",
                forPattern("dd.MM.yyyy HH:mm").print(now()),
                hentAnsattNavn(ident),
                ident,
                valgtEnhetId);

        return header + beskrivelse;
    }

    private String hentAnsattNavn(String ident) {
        try {
            ASBOGOSYSNAVAnsatt ansattRequest = new ASBOGOSYSNAVAnsatt();
            ansattRequest.setAnsattId(ident);
            return ansattWS.hentNAVAnsatt(ansattRequest).getAnsattNavn();
        } catch (HentNAVAnsattFaultGOSYSNAVAnsattIkkeFunnetMsg | HentNAVAnsattFaultGOSYSGeneriskfMsg e) {
            throw new RuntimeException("Noe gikk galt ved henting av ansatt med ident " + ident, e);
        }
    }
}
