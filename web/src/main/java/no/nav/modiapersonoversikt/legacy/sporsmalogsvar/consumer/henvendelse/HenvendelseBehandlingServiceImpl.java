package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse;

import kotlin.Pair;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.modiapersonoversikt.infrastructure.AuthContextUtils;
import no.nav.modiapersonoversikt.infrastructure.content.ContentRetriever;
import no.nav.modiapersonoversikt.legacy.api.domain.Temagruppe;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Fritekst;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.HenvendelseUtils;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.legacy.api.domain.norg.EnhetsGeografiskeTilknytning;
import no.nav.modiapersonoversikt.legacy.api.service.arbeidsfordeling.ArbeidsfordelingV1Service;
import no.nav.modiapersonoversikt.legacy.api.service.kodeverk.StandardKodeverk;
import no.nav.modiapersonoversikt.legacy.api.service.ldap.LDAPService;
import no.nav.modiapersonoversikt.infrastructure.tilgangskontroll.*;
import no.nav.modiapersonoversikt.infrastructure.naudit.Audit;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditIdentifier;
import no.nav.modiapersonoversikt.infrastructure.naudit.AuditResources;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.legacy.MeldingVM;
import no.nav.modiapersonoversikt.legacy.sporsmalogsvar.legacy.TraadVM;
import no.nav.modiapersonoversikt.rest.persondata.Persondata;
import no.nav.modiapersonoversikt.rest.persondata.PersondataService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.legacy.api.utils.MeldingUtils.tilMelding;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HenvendelseBehandlingServiceImpl implements HenvendelseBehandlingService {
    private static Audit.AuditDescriptor<XMLHenvendelse> auditLogger = Audit.describe(
            Audit.Action.READ,
            AuditResources.Person.Henvendelse.Les,
            (henvendelse) -> singletonList(new Pair<>(AuditIdentifier.FNR, henvendelse.getFnr()))
    );

    private static Logger logger = LoggerFactory.getLogger(HenvendelseBehandlingService.class);

    private final HenvendelsePortType henvendelsePortType;
    private final BehandleHenvendelsePortType behandleHenvendelsePortType;
    private final PersondataService persondataService;
    private final Tilgangskontroll tilgangskontroll;
    private final StandardKodeverk standardKodeverk;
    private final ContentRetriever propertyResolver;
    private final LDAPService ldapService;
    private final ArbeidsfordelingV1Service arbeidsfordelingService;

    @Autowired
    public HenvendelseBehandlingServiceImpl(
            HenvendelsePortType henvendelsePortType,
            BehandleHenvendelsePortType behandleHenvendelsePortType,
            PersondataService persondataService,
            Tilgangskontroll tilgangskontroll,
            StandardKodeverk standardKodeverk,
            ContentRetriever propertyResolver,
            LDAPService ldapService,
            ArbeidsfordelingV1Service arbeidsfordelingService
    ) {
        this.henvendelsePortType = henvendelsePortType;
        this.behandleHenvendelsePortType = behandleHenvendelsePortType;
        this.persondataService = persondataService;
        this.tilgangskontroll = tilgangskontroll;
        this.standardKodeverk = standardKodeverk;
        this.propertyResolver = propertyResolver;
        this.ldapService = ldapService;
        this.arbeidsfordelingService = arbeidsfordelingService;
    }


    @Override
    public Meldinger hentMeldinger(String fnr, String valgtEnhet) {
        List<Melding> meldinger = hentMeldingerFraHenvendelse(fnr, valgtEnhet);
        return new Meldinger(meldinger);
    }

    private List<Melding> hentMeldingerFraHenvendelse(String fnr, String valgtEnhet) {
        List<EnhetsGeografiskeTilknytning> valgtEnhetSGTenheter = arbeidsfordelingService.hentGTnummerForEnhet(valgtEnhet);
        List<String> enhetslistGTogEnhet = new ArrayList<>();
        enhetslistGTogEnhet.add(valgtEnhet);
        for (EnhetsGeografiskeTilknytning enhetsGeografiskeTilknytning : valgtEnhetSGTenheter) {
            enhetslistGTogEnhet.add(enhetsGeografiskeTilknytning.geografiskOmraade);
        }
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = henvendelsePortType
                .hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(HenvendelseUtils.AKTUELLE_HENVENDELSE_TYPER));

        List<Object> wsMeldinger = wsHentHenvendelseListeResponse.getAny();

        if (!wsMeldinger.isEmpty()) {
            auditLogger.log((XMLHenvendelse) wsMeldinger.get(0));
        }

        return wsMeldinger.stream()
                .map(melding -> (XMLHenvendelse) melding)
                .map(tilMelding(propertyResolver, ldapService))
                .map(this::journalfortTemaTilTemanavn)
                .filter(kontorsperreTilgangMedGT(enhetslistGTogEnhet))
                .map(okonomiskSosialhjelpTilgangMedGT(enhetslistGTogEnhet, valgtEnhet))
                .map(journalfortTemaTilgang(valgtEnhet))
                .collect(toList());
    }

    @Override
    public void merkSomKontorsperret(String fnr, TraadVM valgtTraad) {
        String enhet = getEnhet(fnr);
        List<String> ider = valgtTraad.getMeldinger().stream()
                .map(MeldingVM::getId)
                .collect(toList());

        behandleHenvendelsePortType.oppdaterKontorsperre(enhet, ider);
    }

    @Override
    public void merkSomFeilsendt(TraadVM valgtTraad) {
        List<String> behandlingsIdListe = valgtTraad.getMeldinger().stream()
                .filter(melding -> !melding.erFeilsendt())
                .map(MeldingVM::getId)
                .collect(toList());
        if (!behandlingsIdListe.isEmpty()) {
            behandleHenvendelsePortType.oppdaterTilKassering(behandlingsIdListe);
        }
    }

    @Override
    public void merkSomBidrag(TraadVM valgtTraad) {
        behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(valgtTraad.getEldsteMelding().melding.traadId, "BID");
    }

    @Override
    public void merkSomAvsluttet(TraadVM valgtTraad, String enhetId) {
        behandleHenvendelsePortType.ferdigstillUtenSvar(valgtTraad.getEldsteMelding().melding.traadId, enhetId);
    }

    @Override
    public void merkForHastekassering(TraadVM valgtTraad) {
        List<String> behandlingsIdListe = valgtTraad.getMeldinger().stream()
                .filter(melding -> !melding.erFeilsendt())
                .map(MeldingVM::getId)
                .collect(toList());
        behandleHenvendelsePortType.markerTraadForHasteKassering(behandlingsIdListe);
    }

    @Override
    public String getEnhet(String fnr) {
        Persondata.Enhet navEnhet = persondataService.hentNavEnhet(fnr);
        if (navEnhet != null) {
            return navEnhet.getId();
        } else {
            return null;
        }

    }

    private Predicate<Melding> kontorsperreTilgangMedGT(final List<String> valgEnhetsGT) {
        return melding -> isBlank(melding.kontorsperretEnhet) || (valgEnhetsGT
                .stream()
                .anyMatch(enhet -> enhet.equals(melding.kontorsperretEnhet))
        );
    }

    private Function<Melding, Melding> okonomiskSosialhjelpTilgangMedGT(final List<String> valgtEnhetsGT, final String valgtEnhet) {
        return melding -> {
            if (valgtEnhetsGT.stream().anyMatch(enhet -> enhet.equals(melding.brukersEnhet))) {
                return melding;
            } else {
                return okonomiskSosialhjelpTilgang(valgtEnhet).apply(melding);
            }
        };
    }

    private Predicate<Melding> kontorsperreTilgang(final String valgtEnhet) {
        return melding -> {
            TilgangTilKontorSperreData data = new TilgangTilKontorSperreData(valgtEnhet, melding.kontorsperretEnhet);
            return tilgangskontroll
                    .check(Policies.tilgangTilKontorsperretMelding.with(data))
                    .getDecision()
                    .isPermit();
        };
    }

    private Function<Melding, Melding> okonomiskSosialhjelpTilgang(final String valgtEnhet) {
        return melding -> {
            TilgangTilOksosSperreData data = new TilgangTilOksosSperreData(valgtEnhet, melding.brukersEnhet);
            boolean tilgangTilMelding = tilgangskontroll
                    .check(Policies.tilgangTilOksosMelding.with(data))
                    .getDecision()
                    .isPermit();

            if (melding.gjeldendeTemagruppe == Temagruppe.OKSOS && !tilgangTilMelding) {
                String ident = AuthContextUtils.requireIdent();
                logger.info("HenvendelseBehandlingServiceImpl::okonomiskSosialhjelpTilgang feilet. Ident: {} Enhet: {} Tema: {} SaksId: {} JournalpostId: {}",
                        ident,
                        valgtEnhet,
                        melding.journalfortTema,
                        melding.journalfortSaksId,
                        melding.journalpostId
                );
                melding.withFritekst(new Fritekst(propertyResolver.hentTekst("tilgang.OKSOS"), melding.skrevetAv, melding.ferdigstiltDato));
            }

            return melding;
        };
    }

    private Function<Melding, Melding> journalfortTemaTilgang(final String valgtEnhet) {
        String ident = AuthContextUtils.requireIdent();
        return (melding) -> {
            TilgangTilTemaData data = new TilgangTilTemaData(valgtEnhet, melding.journalfortTema);
            boolean tilgangTilTema = tilgangskontroll
                    .check(Policies.tilgangTilTema.with(data))
                    .getDecision()
                    .isPermit();

            if (!isBlank(melding.journalfortTema) && !tilgangTilTema) {
                logger.info("HenvendelseBehandlingServiceImpl::journalfortTemaTilgang feilet. Ident: {} Enhet: {} Tema: {} SaksId: {} JournalpostId: {}",
                        ident,
                        valgtEnhet,
                        melding.journalfortTema,
                        melding.journalfortSaksId,
                        melding.journalpostId
                );
                melding.withFritekst(new Fritekst(propertyResolver.hentTekst("tilgang.journalfort"), melding.skrevetAv, melding.ferdigstiltDato));
                melding.ingenTilgangJournalfort = true;
            }

            return melding;
        };
    }

    private Melding journalfortTemaTilTemanavn(Melding melding) {
        if (melding.journalfortTema != null) {
            String temaNavn = standardKodeverk.getArkivtemaNavn(melding.journalfortTema);
            melding.journalfortTemanavn = temaNavn != null ? temaNavn : melding.journalfortTema;
        }
        return melding;
    }

}
