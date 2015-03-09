package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.*;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.virksomhet.behandlesak.v1.*;
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.*;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Sak.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.SakerUtils.hentGenerelleOgIkkeGenerelleSaker;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.SakerUtils.leggTilFagsystemnavnOgTemanavn;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.joda.time.DateTime.now;

public class SakerServiceImpl implements SakerService {

    @Inject
    private SakV1 sakV1;
    @Inject
    private BehandleSakV1 behandleSakWS;
    @Inject
    private GsakKodeverk gsakKodeverk;
    @Inject
    private StandardKodeverk standardKodeverk;
    @Inject
    private LokaltKodeverk lokaltKodeverk;
    @Inject
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private ArbeidOgAktivitet arbeidOgAktivitet;


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
        behandleOppfolgingsSaker(saker);
        return saker;
    }

    @Override
    public void knyttBehandlingskjedeTilSak(String fnr, String behandlingskjede, Sak sak) throws Exception {
        if (!sak.finnesIGsak) {
            sak.saksId = optional(opprettSak(fnr, sak));
        }

        behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                behandlingskjede,
                sak.saksId.get(),
                sak.temaKode,
                saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
    }

    private String opprettSak(String fnr, Sak sak) {
        try {
            WSOpprettSakRequest request = new WSOpprettSakRequest().withSak(
                    new WSSak()
                            .withGjelderBrukerListe(new WSPerson().withIdent(fnr))
                            .withFagomraade(new WSFagomraader().withValue(sak.temaKode))
                            .withFagsystem(new WSFagsystemer().withValue(sak.fagsystemKode))
                            .withFagsystemSakId(sak.saksId.getOrElse(null))
                            .withSakstype(new WSSakstyper().withValue(sak.sakstype)));

            return behandleSakWS.opprettSak(request).getSakId();
        } catch (OpprettSakUgyldigInput | OpprettSakSakEksistererAllerede e) {
            throw new RuntimeException(e);
        }
    }

    private List<Sak> hentSakerFraGsak(String fnr) {
        try {
            WSFinnSakResponse response = sakV1.finnSak(new WSFinnSakRequest().withBruker(new no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson().withIdent(fnr)));
            return on(response.getSakListe()).map(TIL_SAK).collectIn(new ArrayList<Sak>());
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster e) {
            throw new RuntimeException(e);
        }
    }

    private void leggTilFraArena(String fnr, List<Sak> saker) {
        if (!on(saker).exists(IS_ARENA_OPPFOLGING)) {
            Optional<Sak> oppfolging = hentOppfolgingssakFraArena(fnr);
            if (oppfolging.isSome()) {
                saker.add(oppfolging.get());
            }
        }
    }

    private void leggTilManglendeGenerelleSaker(List<Sak> saker) {
        List<Sak> generelleSaker = on(saker).filter(where(IS_GENERELL_SAK, equalTo(true))).collect();
        for (String temakode : GODKJENTE_TEMA_FOR_GENERELLE) {
            if (!on(generelleSaker).exists(where(TEMAKODE, equalTo(temakode))) && !TEMAKODE_OPPFOLGING.equals(temakode)) {
                saker.add(lagGenerellSak(temakode));
            }
        }
    }

    private void behandleOppfolgingsSaker(List<Sak> saker) {
        List<Sak> generelleSaker = on(saker).filter(where(IS_GENERELL_SAK, equalTo(true))).collect();
        List<Sak> fagsaker = on(saker).filter(where(IS_GENERELL_SAK, equalTo(false))).collect();

        boolean oppfolgingssakFinnesIFagsaker = inneholderOppfolgingssak(fagsaker);
        boolean oppfolgingssakFinnesIGenerelleSaker = inneholderOppfolgingssak(generelleSaker);

        if (oppfolgingssakFinnesIFagsaker && oppfolgingssakFinnesIGenerelleSaker) {
            fjernGenerellOppfolgingssak(saker, generelleSaker);
        } else if (!oppfolgingssakFinnesIFagsaker && !oppfolgingssakFinnesIGenerelleSaker) {
            saker.add(lagGenerellSak(TEMAKODE_OPPFOLGING));
        }
    }

    private static boolean inneholderOppfolgingssak(List<Sak> saker) {
        return on(saker).exists(where(TEMAKODE, equalTo(TEMAKODE_OPPFOLGING)));
    }

    private void fjernGenerellOppfolgingssak(List<Sak> saker, List<Sak> generelleSaker) {
        for (Sak sak : generelleSaker) {
            if (TEMAKODE_OPPFOLGING.equals(sak.temaKode)) {
                saker.remove(sak);
            }
        }
    }

    private static Sak lagGenerellSak(String temakode) {
        Sak sak = new Sak();
        sak.temaKode = temakode;
        sak.finnesIGsak = false;
        sak.fagsystemKode = GODKJENT_FAGSYSTEM_FOR_GENERELLE;
        sak.sakstype = SAKSTYPE_GENERELL;
        sak.opprettetDato = now();
        return sak;
    }

    private Optional<Sak> hentOppfolgingssakFraArena(String fnr) {
        WSHentSakListeRequest request = new WSHentSakListeRequest()
                .withBruker(new WSBruker().withBrukertypeKode("PERSON").withBruker(fnr))
                .withFagomradeKode("OPP");
        try {
            return on(arbeidOgAktivitet.hentSakListe(request).getSakListe())
                    .head()
                    .map(new Transformer<no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak, Sak>() {
                        @Override
                        public Sak transform(no.nav.virksomhet.gjennomforing.sak.arbeidogaktivitet.v1.Sak arenaSak) {
                            Sak sak = new Sak();
                            sak.saksId = optional(arenaSak.getSaksId());
                            sak.fagsystemSaksId = optional(arenaSak.getSaksId());
                            sak.fagsystemKode = "AO01";
                            sak.sakstype = arenaSak.getSakstypeKode().getKode();
                            sak.temaKode = arenaSak.getFagomradeKode().getKode();
                            sak.opprettetDato = arenaSak.getEndringsInfo().getOpprettetDato().toDateTimeAtStartOfDay();
                            sak.finnesIGsak = false;
                            return sak;
                        }
                    });
        } catch (Exception e) {
            return none();
        }
    }

    static final Transformer<no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak, Sak> TIL_SAK = new Transformer<no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak, Sak>() {
        @Override
        public Sak transform(no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak wsSak) {
            Sak sak = new Sak();
            sak.opprettetDato = wsSak.getOpprettelsetidspunkt();
            sak.saksId = optional(wsSak.getSakId());
            sak.fagsystemSaksId = isBlank(wsSak.getFagsystemSakId()) ? Optional.<String>none() : optional(wsSak.getFagsystemSakId());
            sak.temaKode = wsSak.getFagomraade().getValue();
            sak.sakstype = wsSak.getSakstype().getValue();
            sak.fagsystemKode = wsSak.getFagsystem().getValue();
            sak.finnesIGsak = true;
            return sak;
        }
    };

}
