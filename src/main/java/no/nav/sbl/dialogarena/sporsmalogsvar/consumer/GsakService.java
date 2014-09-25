package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.AnsattEnhet;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.NyOppgave;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.OppgavebehandlingV3;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgave;
import no.nav.tjeneste.virksomhet.oppgavebehandling.v3.meldinger.WSOpprettOppgaveRequest;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSBrukersok;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakRequest;
import no.nav.virksomhet.tjenester.ruting.meldinger.v1.WSFinnAnsvarligEnhetForSakResponse;
import no.nav.virksomhet.tjenester.ruting.v1.Ruting;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.LocalDate;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.DateUtils.ukedagerFraDato;

public class GsakService {

    public static final int OPPRETTET_AV_ENHET_ID = 2820;

    @Inject
    private OppgavebehandlingV3 oppgavebehandling;
    @Inject
    private no.nav.virksomhet.tjenester.sak.v1.Sak sakWs;
    @Inject
    private Ruting ruting;

    public List<Sak> hentSakerForBruker(String fnr) {
        WSFinnGenerellSakListeResponse response = sakWs.finnGenerellSakListe(new WSFinnGenerellSakListeRequest().withBrukerId(fnr));
        return on(response.getSakListe()).map(TIL_SAK).collectIn(new ArrayList<Sak>());
    }

    public Optional<AnsattEnhet> hentForeslattEnhet(String fnr, String tema) {
        try {
            WSFinnAnsvarligEnhetForSakResponse enhetForSakResponse = ruting.finnAnsvarligEnhetForSak(
                    new WSFinnAnsvarligEnhetForSakRequest().withBrukersok(new WSBrukersok().withBrukerId(fnr).withFagomradeKode(tema)));

            return optional(new AnsattEnhet(enhetForSakResponse.getEnhetId(), enhetForSakResponse.getEnhetNavn()));
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
            sak.tema = wsGenerellSak.getFagomradeKode();
            sak.sakstype = wsGenerellSak.getSakstypeKode();
            sak.fagsystemKode = wsGenerellSak.getFagsystemKode();
            return sak;
        }
    };

    public void opprettGsakOppgave(NyOppgave nyOppgave) {
        oppgavebehandling.opprettOppgave(
                new WSOpprettOppgaveRequest()
                        .withOpprettetAvEnhetId(OPPRETTET_AV_ENHET_ID)   // TODO: Endre til å hente den faktiske enhetsid
                        .withHenvendelsetypeKode("BESVAR_KNA")
                        .withOpprettOppgave(
                                new WSOpprettOppgave()
                                        .withHenvendelseId(nyOppgave.henvendelseId)
                                        .withAktivFra(LocalDate.now())
                                        .withAktivTil(ukedagerFraDato(nyOppgave.type.dagerFrist, LocalDate.now()))
                                        .withAnsvarligEnhetId(nyOppgave.enhet.enhetId)
                                        .withBeskrivelse(nyOppgave.beskrivelse)
                                        .withFagomradeKode(nyOppgave.tema.kode)
                                        .withOppgavetypeKode(nyOppgave.type.kode)
                                        .withPrioritetKode(nyOppgave.prioritet.kode)
                                        .withLest(false)
                        ));
    }

}
