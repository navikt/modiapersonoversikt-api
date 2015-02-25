package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.LokaltKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.StandardKodeverk;
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1;
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakSakEksistererAllerede;
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakUgyldigInput;
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest;
import no.nav.virksomhet.gjennomforing.sak.v1.WSGenerellSak;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeRequest;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSFinnGenerellSakListeResponse;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.SakerUtils.hentGenerelleOgIkkeGenerelleSaker;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.SakerUtils.leggTilFagsystemnavnOgTemanavn;

public class SakerServiceImpl implements SakerService {

    @Inject
    private no.nav.virksomhet.tjenester.sak.v1.Sak sakWS;
    @Inject
    private BehandleSakV1 behandleSakWS;
    @Inject
    private GsakKodeverk gsakKodeverk;
    @Inject
    private StandardKodeverk standardKodeverk;
    @Inject
    private LokaltKodeverk lokaltKodeverk;


    @Override
    public Saker hentSaker(String fnr) {
        List<Sak> sakerForBruker = hentListeAvSaker(fnr);
        leggTilFagsystemnavnOgTemanavn(sakerForBruker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);
        return hentGenerelleOgIkkeGenerelleSaker(sakerForBruker, lokaltKodeverk);
    }

    @Override
    public List<Sak> hentListeAvSaker(String fnr) {
        List<Sak> saker = hentSakerFraGsak(fnr);
        leggTilFraArena(fnr, saker);
        leggTilManglendeGenerelleSaker(saker);
        return saker;
    }

    @Override
    public void opprettSak(String fnr, Sak sak) {
        try {
            WSOpprettSakRequest request = new WSOpprettSakRequest().withSak(
                    new WSSak()
                            .withGjelderBrukerListe(new WSPerson().withIdent(fnr))
                            .withFagomraade(new WSFagomraader().withValue(sak.temaKode))
                            .withFagsystem(new WSFagsystemer().withValue(sak.fagsystemKode))
                            .withFagsystemSakId(sak.saksId)
                            .withSakstype(new WSSakstyper().withValue(sak.sakstype)));

            behandleSakWS.opprettSak(request);
        } catch (OpprettSakUgyldigInput | OpprettSakSakEksistererAllerede e) {
            throw new RuntimeException(e);
        }
    }

    private List<Sak> hentSakerFraGsak(String fnr) {
        WSFinnGenerellSakListeResponse response = sakWS.finnGenerellSakListe(new WSFinnGenerellSakListeRequest().withBrukerId(fnr));
        return on(response.getSakListe()).map(TIL_SAK).collectIn(new ArrayList<Sak>());
    }

    private void leggTilFraArena(String fnr, List<Sak> saker) {
        if (!on(saker).exists(IS_ARENA_OPPFOLGING)) {
            Optional<Sak> oppfolging = hentOppfolgingssakFraArena(fnr);
            if (oppfolging.isSome()) {
                saker.add(oppfolging.get());
            } else {
                saker.add(lagGenerellSak(TEMAKODE_OPPFOLGING));
            }
        }
    }

    private void leggTilManglendeGenerelleSaker(List<Sak> saker) {
        List<Sak> generelleSaker = on(saker).filter(where(IS_GENERELL_SAK, equalTo(true))).collect();
        for (String temakode : GODKJENTE_TEMA_FOR_GENERELLE) {
            if (!on(generelleSaker).exists(where(TEMAKODE, equalTo(temakode)))) {
                saker.add(lagGenerellSak(temakode));
            }
        }
    }

    private static Sak lagGenerellSak(String temakode) {
        Sak sak = new Sak();
        sak.temaKode = temakode;
        sak.finnesIGsak = false;
        sak.fagsystemKode = GODKJENT_FAGSYSTEM_FOR_GENERELLE;
        sak.sakstype = SAKSTYPE_GENERELL;
        sak.saksId = "";
        return sak;
    }

    private Optional<Sak> hentOppfolgingssakFraArena(String fnr) {
        // TODO: Hent sak fra Arena
        return none();
    }

    static final Transformer<WSGenerellSak, Sak> TIL_SAK = new Transformer<WSGenerellSak, Sak>() {
        @Override
        public Sak transform(WSGenerellSak wsGenerellSak) {
            Sak sak = new Sak();
            sak.opprettetDato = wsGenerellSak.getEndringsinfo().getOpprettetDato();
            sak.saksId = wsGenerellSak.getSakId();
            sak.temaKode = wsGenerellSak.getFagomradeKode();
            sak.sakstype = wsGenerellSak.getSakstypeKode();
            sak.fagsystemKode = wsGenerellSak.getFagsystemKode();
            sak.finnesIGsak = true;
            return sak;
        }
    };

}
