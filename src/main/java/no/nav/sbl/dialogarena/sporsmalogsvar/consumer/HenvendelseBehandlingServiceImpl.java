package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.modig.common.SporingsAksjon;
import no.nav.modig.common.SporingsLogger;
import no.nav.modig.content.PropertyResolver;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.request.attributes.PolicyAttribute;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.AnsattEnhetUtil;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.TraadVM;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeResponse;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.tilMelding;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.FEILSENDT;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.ID;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HenvendelseBehandlingServiceImpl implements HenvendelseBehandlingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private PersonKjerneinfoServiceBi kjerneinfo;
    @Inject
    @Named("pep")
    private EnforcementPoint pep;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private StandardKodeverk standardKodeverk;
    @Inject
    private PropertyResolver propertyResolver;
    @Inject
    private SporingsLogger sporingsLogger;
    @Inject
    private LDAPService ldapService;

    @Override
    public List<Melding> hentMeldinger(String fnr) {
        return hentMeldinger(fnr, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
    }

    @Override
    public List<Melding> hentMeldinger(String fnr, String valgtEnhet) {
        List<String> typer = asList(
                SPORSMAL_SKRIFTLIG.name(),
                SVAR_SKRIFTLIG.name(),
                SVAR_OPPMOTE.name(),
                SVAR_TELEFON.name(),
                REFERAT_OPPMOTE.name(),
                REFERAT_TELEFON.name(),
                SPORSMAL_MODIA_UTGAAENDE.name(),
                SVAR_SBL_INNGAAENDE.name());

        WSHentHenvendelseListeResponse wsHentHenvendelseListeResponse = henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withFodselsnummer(fnr).withTyper(typer));

        List<Object> wsMeldinger = wsHentHenvendelseListeResponse.getAny();

        if (!wsMeldinger.isEmpty()) {
            sporingsLogger.logg(wsMeldinger.get(0), SporingsAksjon.Les);
        }

        return on(wsMeldinger)
                .map(castTo(XMLHenvendelse.class))
                .map(tilMelding(propertyResolver, ldapService))
                .map(journalfortTemaTilTemanavn)
                .filter(kontorsperreTilgang(valgtEnhet))
                .map(okonomiskSosialhjelpTilgang(valgtEnhet))
                .map(journalfortTemaTilgang(valgtEnhet))
                .collect();
    }

    @Override
    public void merkSomKontorsperret(String fnr, TraadVM valgtTraad) {
        String enhet = getEnhet(fnr);
        List<String> ider = on(valgtTraad.getMeldinger()).map(ID).collect();

        behandleHenvendelsePortType.oppdaterKontorsperre(enhet, ider);
    }

    @Override
    public void merkSomFeilsendt(TraadVM valgtTraad) {
        List<String> behandlingsIdListe = on(valgtTraad.getMeldinger()).filter(where(FEILSENDT, equalTo(false))).map(ID).collect();
        if (!behandlingsIdListe.isEmpty()) {
            behandleHenvendelsePortType.oppdaterTilKassering(behandlingsIdListe);
        }
    }

    @Override
    public void merkSomBidrag(TraadVM valgtTraad) {
        behandleHenvendelsePortType.knyttBehandlingskjedeTilTema(valgtTraad.getEldsteMelding().melding.traadId, "BID");
    }

    @Override
    public String getEnhet(String fnr) {
        HentKjerneinformasjonRequest kjerneinfoRequest = new HentKjerneinformasjonRequest(fnr);
        kjerneinfoRequest.setBegrunnet(true);
        Person person = kjerneinfo.hentKjerneinformasjon(kjerneinfoRequest).getPerson();
        return person.getPersonfakta().getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
    }

    protected Predicate<Melding> kontorsperreTilgang(final String valgtEnhet) {
        final Set<String> enheter = AnsattEnhetUtil.hentEnheterForValgtEnhet(valgtEnhet);

        return new Predicate<Melding>() {
            @Override
            public boolean evaluate(Melding melding) {
                List<PolicyAttribute> attributes = new ArrayList<>(Arrays.asList(actionId("kontorsperre"),
                        resourceId(""),
                        resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(melding.kontorsperretEnhet))));

                for (String enhet : enheter) {
                    attributes.add(subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(enhet)));
                }

                PolicyRequest kontorsperrePolicyRequest = forRequest(attributes);

                return isBlank(melding.kontorsperretEnhet) || pep.hasAccess(kontorsperrePolicyRequest);
            }
        };
    }

    protected Transformer<Melding, Melding> okonomiskSosialhjelpTilgang(final String valgtEnhet) {
        final Set<String> enheter = AnsattEnhetUtil.hentEnheterForValgtEnhet(valgtEnhet);
        return new Transformer<Melding, Melding>() {
            @Override
            public Melding transform(Melding melding) {
                List<PolicyAttribute> attributes = new ArrayList<>(Arrays.asList(
                        actionId("oksos"),
                        resourceId(""),
                        resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:bruker-enhet", defaultString(melding.brukersEnhet))
                ));

                for (String enhet : enheter) {
                    attributes.add(subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(enhet)));
                }

                PolicyRequest okonomiskSosialhjelpPolicyRequest = forRequest(attributes);

                if (melding.gjeldendeTemagruppe == Temagruppe.OKSOS && !pep.hasAccess(okonomiskSosialhjelpPolicyRequest)) {
                    melding.fritekst = propertyResolver.getProperty("tilgang.OKSOS");
                }

                return melding;
            }
        };
    }

    private Transformer<Melding, Melding> journalfortTemaTilgang(final String valgtEnhet) {
        return new Transformer<Melding, Melding>() {
            @Override
            public Melding transform(Melding melding) {
                PolicyRequest temagruppePolicyRequest = forRequest(
                        actionId("temagruppe"),
                        resourceId(""),
                        subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                        resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(melding.journalfortTema)));

                if (!isBlank(melding.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                    melding.fritekst = propertyResolver.getProperty("tilgang.journalfort");
                    melding.ingenTilgangJournalfort = true;
                }

                return melding;
            }
        };
    }

    private final Transformer<Melding, Melding> journalfortTemaTilTemanavn = new Transformer<Melding, Melding>() {
        @Override
        public Melding transform(Melding melding) {
            if (melding.journalfortTema != null) {
                String temaNavn = standardKodeverk.getArkivtemaNavn(melding.journalfortTema);
                melding.journalfortTemanavn = temaNavn != null ? temaNavn : melding.journalfortTema;
            }
            return melding;
        }
    };

}
