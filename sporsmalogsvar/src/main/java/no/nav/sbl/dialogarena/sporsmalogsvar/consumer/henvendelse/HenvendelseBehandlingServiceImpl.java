package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.brukerdialog.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.brukerdialog.security.tilgangskontroll.policy.request.attributes.PolicyAttribute;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.tilMelding;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HenvendelseBehandlingServiceImpl implements HenvendelseBehandlingService {
    private static Logger logger = LoggerFactory.getLogger(HenvendelseBehandlingService.class);

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private PersonKjerneinfoServiceBi kjerneinfo;
    @Inject
    @Named("pep")
    private EnforcementPoint pep;
    @Inject
    private StandardKodeverk standardKodeverk;
    @Inject
    @Named("propertyResolver")
    private ContentRetriever propertyResolver;
    @Inject
    private SporingsLogger sporingsLogger;
    @Inject
    private LDAPService ldapService;

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
                .map(journalfortTemaTilTemanavn)
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
            List<PolicyAttribute> attributes = new ArrayList<>(Arrays.asList(actionId("kontorsperre"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(melding.kontorsperretEnhet))));

            PolicyRequest kontorsperrePolicyRequest = forRequest(attributes);

            return isBlank(melding.kontorsperretEnhet) || pep.hasAccess(kontorsperrePolicyRequest);
        };
    }

    private Function<Melding, Melding> okonomiskSosialhjelpTilgang(final String valgtEnhet) {
        String ident = SubjectHandler.getIdent().orElseThrow(() -> new RuntimeException("Fant ikke ident"));
        return melding -> {
            List<PolicyAttribute> attributes = new ArrayList<>(Arrays.asList(
                    actionId("oksos"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:bruker-enhet", defaultString(melding.brukersEnhet))
            ));


            PolicyRequest okonomiskSosialhjelpPolicyRequest = forRequest(attributes);

            if (melding.gjeldendeTemagruppe == Temagruppe.OKSOS && !pep.hasAccess(okonomiskSosialhjelpPolicyRequest)) {
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
            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(melding.journalfortTema)));

            if (!isBlank(melding.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
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

    private final Function<Melding, Melding> journalfortTemaTilTemanavn = (melding) -> {
        if (melding.journalfortTema != null) {
            String temaNavn = standardKodeverk.getArkivtemaNavn(melding.journalfortTema);
            melding.journalfortTemanavn = temaNavn != null ? temaNavn : melding.journalfortTema;
        }
        return melding;
    };

}
