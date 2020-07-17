package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.JournalforingFeilet;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.GsakKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.psak.PsakService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.saker.knyttbehandlingskjedetilsak.KnyttBehandlingskjedeTilSakValidator;
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

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.SakerUtils.leggTilFagsystemnavnOgTemanavn;
import static org.joda.time.DateTime.now;

public class SakerServiceImpl implements SakerService {

    public static final String VEDTAKSLOSNINGEN = "FS36";

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

        // Bør erstattes av reelle saker når bisys kan levere det
        leggTilBidragHack(saker);

        return saker.stream()
                .filter(GODKJENT_FAGSAK.or(GODKJENT_GENERELL))
                .collect(toList());
    }

    @Override
    public List<Sak> hentPensjonSaker(String fnr) {
        List<Sak> saker = psakService.hentSakerFor(fnr);
        leggTilFagsystemnavnOgTemanavn(saker, gsakKodeverk.hentFagsystemMapping(), standardKodeverk);
        return saker;
    }

    @Override
    public void knyttBehandlingskjedeTilSak(String fnr, String behandlingskjede, Sak sak, String enhet) throws JournalforingFeilet {
        KnyttBehandlingskjedeTilSakValidator.validate(fnr, behandlingskjede, sak, enhet);

        if (sak.syntetisk && BIDRAG_MARKOR.equals(sak.fagsystemKode)) {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(behandlingskjede, "BID");
            return;
        }
        if (!sak.finnesIPsak && !sak.finnesIGsak) {
            sak.saksId = opprettSak(fnr, sak);
        }
        try {
            behandleHenvendelsePortType.knyttBehandlingskjedeTilSak(
                    behandlingskjede,
                    sak.saksId,
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
                            .withFagsystemSakId(sak.saksId)
                            .withSakstype(new WSSakstyper().withValue(sak.sakstype)));

            return behandleSakWS.opprettSak(request).getSakId();
        } catch (OpprettSakSakEksistererAllerede opprettSakException) {
            return finnSakIdFraGsak(fnr, sak)
                    .orElseThrow(() -> new RuntimeException("Kunne ikke finne eksisterende sak selvom den eksisterer", opprettSakException));
        } catch (OpprettSakUgyldigInput opprettSakException) {
            throw new RuntimeException(opprettSakException);
        }
    }

    private List<Sak> hentSakerFraGsak(String fnr) {
        try {
            WSFinnSakResponse response = sakV1.finnSak(
                    new WSFinnSakRequest()
                            .withBruker(new no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson().withIdent(fnr)));

            return response.getSakListe().stream().map(TIL_SAK).collect(toList());
        } catch (FinnSakUgyldigInput | FinnSakForMangeForekomster e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<String> finnSakIdFraGsak(String fnr, Sak sak) {
        try {
            WSFinnSakRequest request = new WSFinnSakRequest()
                    .withBruker(new no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSPerson().withIdent(fnr))
                    .withFagomraadeListe(new no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagomraader().withValue(sak.temaKode))
                    .withFagsystem(new no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSFagsystemer().withValue(sak.fagsystemKode))
                    .withFagsystemSakId(sak.saksId);

            return sakV1
                    .finnSak(request)
                    .getSakListe()
                    .stream()
                    .filter((finnSak) -> finnSak.getSakstype().getValue().equals(sak.sakstype))
                    .findFirst()
                    .map(no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak::getSakId);
        } catch (FinnSakForMangeForekomster | FinnSakUgyldigInput finnSakException) {
            throw new RuntimeException(finnSakException);
        }
    }

    private void leggTilFraArena(String fnr, List<Sak> saker) {
        if (saker.stream().noneMatch(IS_ARENA_OPPFOLGING)) {
            Optional<Sak> oppfolging = hentOppfolgingssakFraArena(fnr);
            oppfolging.ifPresent(saker::add);
        }
    }

    private void leggTilBidragHack(List<Sak> saker) {
        Sak bidragSak = new Sak();
        bidragSak.saksId = "-";
        bidragSak.fagsystemSaksId = "-";
        bidragSak.temaKode = BIDRAG_MARKOR;
        bidragSak.temaNavn = "Bidrag";
        bidragSak.fagsystemKode = BIDRAG_MARKOR;
        bidragSak.fagsystemNavn = "Kopiert inn i Bisys";
        bidragSak.sakstype = SAKSTYPE_MED_FAGSAK;
        bidragSak.opprettetDato = null;
        bidragSak.finnesIGsak = false;
        bidragSak.finnesIPsak = false;
        bidragSak.syntetisk = true;

        saker.add(bidragSak);
    }


    private void leggTilManglendeGenerelleSaker(List<Sak> saker) {
        List<Sak> generelleSaker = saker.stream()
                .filter(Sak::isSakstypeForVisningGenerell)
                .collect(toList());

        saker.addAll(GODKJENTE_TEMA_FOR_GENERELL_SAK.stream()
                .filter(temakode -> harIngenSakerMedTemakode(temakode, generelleSaker) && !TEMAKODE_OPPFOLGING.equals(temakode))
                .map(SakerServiceImpl::lagGenerellSakMedTema)
                .collect(toList()));
    }

    private boolean harIngenSakerMedTemakode(String temakode, List<Sak> generelleSaker) {
        return generelleSaker.stream().noneMatch(sak -> temakode.equals(sak.temaKode));
    }

    private void behandleOppfolgingsSaker(List<Sak> saker) {
        List<Sak> generelleSaker = saker.stream()
                .filter(IS_GENERELL_SAK)
                .collect(toList());

        List<Sak> fagsaker = saker.stream()
                .filter(IS_GENERELL_SAK.negate())
                .collect(toList());

        boolean oppfolgingssakFinnesIFagsaker = inneholderOppfolgingssak(fagsaker);
        boolean oppfolgingssakFinnesIGenerelleSaker = inneholderOppfolgingssak(generelleSaker);

        if (oppfolgingssakFinnesIFagsaker && oppfolgingssakFinnesIGenerelleSaker) {
            fjernGenerellOppfolgingssak(saker, generelleSaker);
        } else if (!oppfolgingssakFinnesIFagsaker && !oppfolgingssakFinnesIGenerelleSaker) {
            saker.add(lagGenerellSakMedTema(TEMAKODE_OPPFOLGING));
        }
    }

    private static boolean inneholderOppfolgingssak(List<Sak> saker) {
        return saker.stream().anyMatch(sak -> TEMAKODE_OPPFOLGING.equals(sak.temaKode));
    }

    private void fjernGenerellOppfolgingssak(List<Sak> saker, List<Sak> generelleSaker) {
        for (Sak sak : generelleSaker) {
            if (TEMAKODE_OPPFOLGING.equals(sak.temaKode)) {
                saker.remove(sak);
            }
        }
    }

    private static Sak lagGenerellSakMedTema(String temakode) {
        Sak sak = new Sak();
        sak.temaKode = temakode;
        sak.finnesIGsak = false;
        sak.fagsystemKode = FAGSYSTEM_FOR_OPPRETTELSE_AV_GENERELL_SAK;
        sak.sakstype = SAKSTYPE_GENERELL;
        sak.opprettetDato = now();
        return sak;
    }

    private Optional<Sak> hentOppfolgingssakFraArena(String fnr) {
        WSHentSakListeRequest request = new WSHentSakListeRequest()
                .withBruker(new WSBruker().withBrukertypeKode("PERSON").withBruker(fnr))
                .withFagomradeKode("OPP");
        try {
            return arbeidOgAktivitet.hentSakListe(request).getSakListe().stream()
                    .findFirst()
                    .map(arenaSak -> {
                        Sak sak = new Sak();
                        sak.saksId = arenaSak.getSaksId();
                        sak.fagsystemSaksId = arenaSak.getSaksId();
                        sak.fagsystemKode = FAGSYSTEMKODE_ARENA;
                        sak.sakstype = SAKSTYPE_MED_FAGSAK;
                        sak.temaKode = arenaSak.getFagomradeKode().getKode();
                        sak.opprettetDato = arenaSak.getEndringsInfo().getOpprettetDato().toDateTimeAtStartOfDay();
                        sak.finnesIGsak = false;
                        return sak;
                    });
        } catch (Exception e) {
            return empty();
        }
    }

    public static final Function<no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak, Sak> TIL_SAK = wsSak -> {
        Sak sak = new Sak();
        sak.opprettetDato = wsSak.getOpprettelsetidspunkt();
        sak.saksId = wsSak.getSakId();
        sak.fagsystemSaksId = getFagsystemSakId(wsSak);
        sak.temaKode = wsSak.getFagomraade().getValue();
        sak.sakstype = getSakstype(wsSak);
        sak.fagsystemKode = wsSak.getFagsystem().getValue();
        sak.finnesIGsak = true;

        return sak;
    };

    private static String getSakstype(no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak wsSak) {
        return VEDTAKSLOSNINGEN.equals(wsSak.getFagsystem().getValue()) ? SAKSTYPE_MED_FAGSAK : wsSak.getSakstype().getValue();
    }

    private static String getFagsystemSakId(no.nav.tjeneste.virksomhet.sak.v1.informasjon.WSSak wsSak) {
        return VEDTAKSLOSNINGEN.equals(wsSak.getFagsystem().getValue()) ? wsSak.getSakId() : wsSak.getFagsystemSakId();
    }

    private static final Predicate<Sak> GODKJENT_FAGSAK = sak -> !sak.isSakstypeForVisningGenerell() &&
            GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystemKode) &&
            !TEMAKODE_KLAGE_ANKE.equals(sak.temaKode);

    private static final Predicate<Sak> GODKJENT_GENERELL = sak -> sak.isSakstypeForVisningGenerell() &&
            GYLDIGE_FAGSYSTEM_FOR_GENERELLE_SAKER.contains(sak.fagsystemKode) &&
            GODKJENTE_TEMA_FOR_GENERELL_SAK.contains(sak.temaKode);

}
