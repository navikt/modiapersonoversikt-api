package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.modig.content.PropertyResolver;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.modig.security.tilgangskontroll.policy.request.attributes.PolicyAttribute;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.AnsattEnhetUtil;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

import static java.util.Collections.singletonList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ANSOS;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.OKSOS;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.ELDSTE_FORST;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.SPORSMAL_MODIA_UTGAAENDE;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.tilMelding;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createXMLHenvendelseMedMeldingTilBruker;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.getXMLHenvendelseTypeBasertPaaMeldingstype;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class HenvendelseUtsendingServiceImpl implements HenvendelseUtsendingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    private SendUtHenvendelsePortType sendUtHenvendelsePortType;
    @Inject
    private BehandleHenvendelsePortType behandleHenvendelsePortType;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    private SakerService sakerService;
    @Inject
    @Named("pep")
    private EnforcementPoint pep;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    @Inject
    private PropertyResolver propertyResolver;
    @Inject
    private PersonKjerneinfoServiceBi kjerneinfo;
    @Inject
    private LDAPService ldapService;

    @Override
    public void sendHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak) throws Exception {
        if (oppgaveId.isSome() && oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId.get())) {
            throw new OppgaveErFerdigstilt();
        }

        XMLHenvendelseType type = getXMLHenvendelseTypeBasertPaaMeldingstype(melding.meldingstype);
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedMeldingTilBruker(melding, type);
        String enhet = isNotBlank(melding.brukersEnhet) ? melding.brukersEnhet : getEnhet(melding.fnrBruker);
        xmlHenvendelse.setBrukersEnhet(enhet);

        WSSendUtHenvendelseResponse wsSendUtHenvendelseResponse = sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest()
                .withType(type.name())
                .withFodselsnummer(melding.fnrBruker)
                .withAny(xmlHenvendelse));

        melding.id = wsSendUtHenvendelseResponse.getBehandlingsId();
        Temagruppe temagruppe = Temagruppe.valueOf(melding.temagruppe);
        if (melding.traadId == null) {
            melding.traadId = melding.id;
        }
        if (sak.isSome()) {
            sakerService.knyttBehandlingskjedeTilSak(melding.fnrBruker, melding.traadId, sak.get());
        }
        if (oppgaveId.isSome()) {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(oppgaveId.get(), temagruppe);
        }
        if (temagruppe == ANSOS) {
            merkSomKontorsperret(melding.fnrBruker, singletonList(melding.id));
        }
    }

    @Override
    public List<Melding> hentTraad(String fnr, String traadId) {
        List<Melding> meldinger =
                on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest()
                        .withTyper(
                                SPORSMAL_SKRIFTLIG.name(),
                                SVAR_SKRIFTLIG.name(),
                                SVAR_OPPMOTE.name(),
                                SVAR_TELEFON.name(),
                                REFERAT_OPPMOTE.name(),
                                REFERAT_TELEFON.name(),
                                SPORSMAL_MODIA_UTGAAENDE.name(),
                                SVAR_SBL_INNGAAENDE.name())
                        .withFodselsnummer(fnr))
                        .getAny())
                        .map(castTo(XMLHenvendelse.class))
                        .filter(where(BEHANDLINGSKJEDE_ID, equalTo(traadId)))
                        .map(tilMelding(propertyResolver, ldapService))
                        .map(journalfortTemaTilgang)
                        .collect(ELDSTE_FORST);

        if (meldinger.isEmpty()) {
            throw new ApplicationException(String.format("Fant ingen meldinger for fnr: %s med traadId: %s", fnr, traadId));
        }



        Melding sporsmal = meldinger.get(0);
        String valgtEnhet = defaultString(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());
        Set<String> valgteEnheter = AnsattEnhetUtil.hentEnheterForValgtEnhet(valgtEnhet);
        if (sporsmal.kontorsperretEnhet != null) {
            List<PolicyAttribute> requestList = new ArrayList<>(Arrays.asList(
                    actionId("kontorsperre"),
                    resourceId(""),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(sporsmal.kontorsperretEnhet))));

            for (String enhet : valgteEnheter) {
                requestList.add(subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", enhet));
            }

            pep.assertAccess(forRequest(requestList));
        }
        if (sporsmal.gjeldendeTemagruppe == OKSOS) {
            pep.assertAccess(forRequest(
                    actionId("oksos"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", valgtEnhet),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:bruker-enhet", defaultString(sporsmal.brukersEnhet))));
        }

        return meldinger;
    }

    @Override
    public void merkSomKontorsperret(String fnr, List<String> meldingsIDer) {
        String enhet = getEnhet(fnr);
        behandleHenvendelsePortType.oppdaterKontorsperre(enhet, meldingsIDer);
    }

    @Override
    public void oppdaterTemagruppe(String behandlingsId, String temagruppe) {
        behandleHenvendelsePortType.oppdaterTemagruppe(behandlingsId, temagruppe);
    }

    private static final Transformer<XMLHenvendelse, String> BEHANDLINGSKJEDE_ID = new Transformer<XMLHenvendelse, String>() {
        @Override
        public String transform(XMLHenvendelse xmlHenvendelse) {
            return xmlHenvendelse.getBehandlingskjedeId();
        }
    };

    private final Transformer<Melding, Melding> journalfortTemaTilgang = new Transformer<Melding, Melding>() {
        @Override
        public Melding transform(Melding melding) {
            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(melding.journalfortTema))
            );
            if (isNotBlank(melding.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                melding.fritekst = "";
            }

            return melding;
        }
    };

    private String getEnhet(String fnr) {
        HentKjerneinformasjonRequest kjerneinfoRequest = new HentKjerneinformasjonRequest(fnr);
        kjerneinfoRequest.setBegrunnet(true);
        Person person = kjerneinfo.hentKjerneinformasjon(kjerneinfoRequest).getPerson();
        return person.getPersonfakta().getHarAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
    }

}
