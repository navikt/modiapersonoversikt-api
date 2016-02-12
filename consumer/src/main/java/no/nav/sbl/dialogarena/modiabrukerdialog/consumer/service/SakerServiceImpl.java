package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.virksomhet.behandlesak.v1.BehandleSakV1;
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakSakEksistererAllerede;
import no.nav.tjeneste.virksomhet.behandlesak.v1.OpprettSakUgyldigInput;
import no.nav.tjeneste.virksomhet.behandlesak.v1.informasjon.*;
import no.nav.tjeneste.virksomhet.behandlesak.v1.meldinger.WSOpprettSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakForMangeForekomster;
import no.nav.tjeneste.virksomhet.sak.v1.FinnSakUgyldigInput;
import no.nav.tjeneste.virksomhet.sak.v1.SakV1;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakRequest;
import no.nav.tjeneste.virksomhet.sak.v1.meldinger.WSFinnSakResponse;
import no.nav.virksomhet.tjenester.sak.arbeidogaktivitet.v1.ArbeidOgAktivitet;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSBruker;
import no.nav.virksomhet.tjenester.sak.meldinger.v1.WSHentSakListeRequest;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.*;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;
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
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private ArbeidOgAktivitet arbeidOgAktivitet;
    @Inject
    private PsakService psakService;


    @Override
    public List<Sak> hentSammensatteSaker(String fnr) {
        List<Sak> saker = hentSakerFraGsak(fnr);
        leggTilFraArena(fnr, saker);
        leggTilManglendeGenerelleSaker(saker);
        behandleOppfolgingsSaker(saker);
        leggTilFagsystemnavnOgTemanavn(saker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);
        return on(saker).filter(either(GODSKJENT_FAGSAK).or(GODSKJENT_GENERELL)).collect();
    }

    @Override
    public List<Sak> hentPensjonSaker(String fnr) {
        List<Sak> saker = psakService.hentSakerFor(fnr);
        leggTilFagsystemnavnOgTemanavn(saker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);
        return saker;
    }

    @Override
    public void knyttBehandlingskjedeTilSak(String fnr, String behandlingskjede, Sak sak) throws JournalforingFeilet {
        String enhet = saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet();
        knyttBehandlingskjedeTilSak(fnr, behandlingskjede, sak, enhet);
    }

    @Override
    public void knyttBehandlingskjedeTilSak(String fnr, String behandlingskjede, Sak sak, String enhet) throws JournalforingFeilet {
        if (!sak.finnesIPsak && !sak.finnesIGsak) {
            sak.saksId = optional(opprettSak(fnr, sak));
        }
        try {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                    behandlingskjede,
                    sak.saksId.get(),
                    sak.temaKode,
                    enhet);
        } catch (Exception e) {
            throw new JournalforingFeilet(e);
        }
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

    public List<Sak> hentSakerFraGsak(String fnr) {
        try {
            WSFinnSakResponse response = sakV1.finnSak(new WSFinnSakRequest().withBruker(new no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson().withIdent(fnr)));
            return on(response.getSakListe()).map(TIL_SAK).collectIn(new ArrayList<>());
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
        saker.addAll(GODKJENTE_TEMA_FOR_GENERELLE.stream().filter(temakode -> !on(generelleSaker).exists(where(TEMAKODE, equalTo(temakode))) && !TEMAKODE_OPPFOLGING.equals(temakode)).map(SakerServiceImpl::lagGenerellSak).collect(Collectors.toList()));
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
                    .map(arenaSak -> {
                        Sak sak = new Sak();
                        sak.saksId = optional(arenaSak.getSaksId());
                        sak.fagsystemSaksId = optional(arenaSak.getSaksId());
                        sak.fagsystemKode = FAGSYSTEMKODE_ARENA;
                        sak.sakstype = SAKSTYPE_MED_FAGSAK;
                        sak.temaKode = arenaSak.getFagomradeKode().getKode();
                        sak.opprettetDato = arenaSak.getEndringsInfo().getOpprettetDato().toDateTimeAtStartOfDay();
                        sak.finnesIGsak = false;
                        return sak;
                    });
        } catch (Exception e) {
            return none();
        }
    }

    static final Transformer<no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak, Sak> TIL_SAK = wsSak -> {
        Sak sak = new Sak();
        sak.opprettetDato = wsSak.getOpprettelsetidspunkt();
        sak.saksId = optional(wsSak.getSakId());
        sak.fagsystemSaksId = isBlank(wsSak.getFagsystemSakId()) ? Optional.<String>none() : optional(wsSak.getFagsystemSakId());
        sak.temaKode = wsSak.getFagomraade().getValue();
        sak.sakstype = wsSak.getSakstype().getValue();
        sak.fagsystemKode = wsSak.getFagsystem().getValue();
        sak.finnesIGsak = true;
        return sak;
    };

    private static final Predicate<Sak> GODSKJENT_FAGSAK = sak -> !sak.isSakstypeForVisningGenerell() &&
            GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode) &&
            !TEMAKODE_KLAGE_ANKE.equals(sak.temaKode);

    private static final Predicate<Sak> GODSKJENT_GENERELL = sak -> sak.isSakstypeForVisningGenerell() &&
            GODKJENT_FAGSYSTEM_FOR_GENERELLE.equals(sak.fagsystemKode) &&
            GODKJENTE_TEMA_FOR_GENERELLE.contains(sak.temaKode);

}
