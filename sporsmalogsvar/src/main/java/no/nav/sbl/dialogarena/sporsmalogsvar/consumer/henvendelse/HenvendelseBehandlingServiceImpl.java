package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse;

import no.nav.common.auth.SubjectHandler;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.HenvendelseUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.*;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import no.nav.sbl.dialogarena.sporsmalogsvar.legacy.MeldingVM;
import no.nav.sbl.dialogarena.sporsmalogsvar.legacy.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.tilMelding;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HenvendelseBehandlingServiceImpl implements HenvendelseBehandlingService {
    private static Logger logger = LoggerFactory.getLogger(HenvendelseBehandlingService.class);

    private final HenvendelsePortType henvendelsePortType;
    private final BehandleHenvendelsePortType behandleHenvendelsePortType;
    private final PersonKjerneinfoServiceBi kjerneinfo;
    private final Tilgangskontroll tilgangskontroll;
    private final StandardKodeverk standardKodeverk;
    private final ContentRetriever propertyResolver;
    private final SporingsLogger sporingsLogger;
    private final LDAPService ldapService;

    @Inject
    public HenvendelseBehandlingServiceImpl(
            HenvendelsePortType henvendelsePortType,
            BehandleHenvendelsePortType behandleHenvendelsePortType,
            PersonKjerneinfoServiceBi kjerneinfo,
            Tilgangskontroll tilgangskontroll,
            StandardKodeverk standardKodeverk,
            @Named("propertyResolver") ContentRetriever propertyResolver,
            SporingsLogger sporingsLogger,
            LDAPService ldapService
    ) {
        this.henvendelsePortType = henvendelsePortType;
        this.behandleHenvendelsePortType = behandleHenvendelsePortType;
        this.kjerneinfo = kjerneinfo;
        this.tilgangskontroll = tilgangskontroll;
        this.standardKodeverk = standardKodeverk;
        this.propertyResolver = propertyResolver;
        this.sporingsLogger = sporingsLogger;
        this.ldapService = ldapService;
    }


    @Override
    public Meldinger hentMeldinger(String fnr, String valgtEnhet) {
        List<Melding> meldinger = hentMeldingerFraHenvendelse(fnr, valgtEnhet);
        return new Meldinger(meldinger);
    }

    private List<Melding> hentMeldingerFraHenvendelse(String fnr, String valgtEnhet) {
        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = henvendelsePortType
                .hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(HenvendelseUtils.AKTUELLE_HENVENDELSE_TYPER));

        List<Object> wsMeldinger = wsHentHenvendelseListeResponse.getAny();

        if (!wsMeldinger.isEmpty()) {
            sporingsLogger.logg(wsMeldinger.get(0), SporingsAksjon.Les);
        }

        return wsMeldinger.stream()
                .map(melding -> (XMLHenvendelse) melding)
                .map(tilMelding(propertyResolver, ldapService))
                .map(this::journalfortTemaTilTemanavn)
                .filter(kontorsperreTilgang(valgtEnhet))
                .map(okonomiskSosialhjelpTilgang(valgtEnhet))
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
        HentKjerneinformasjonRequest kjerneinfoRequest = new HentKjerneinformasjonRequest(fnr);
        kjerneinfoRequest.setBegrunnet(true);
        Person person = kjerneinfo.hentKjerneinformasjon(kjerneinfoRequest).getPerson();

        if (person.getPersonfakta().getAnsvarligEnhet() != null) {
            return person.getPersonfakta().getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
        } else {
            return null;
        }

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
                String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
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
        String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
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
