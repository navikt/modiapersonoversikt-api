package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.modig.content.PropertyResolver;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.Feature;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.featuretoggling.FeatureToggle;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.*;
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

    private final HenvendelsePortType henvendelsePortType;
    private final SendUtHenvendelsePortType sendUtHenvendelsePortType;
    private final BehandleHenvendelsePortType behandleHenvendelsePortType;
    private final OppgaveBehandlingService oppgaveBehandlingService;
    private final SakerService sakerService;
    private final EnforcementPoint pep;
    private final SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;
    private final PropertyResolver propertyResolver;
    private final PersonKjerneinfoServiceBi kjerneinfo;
    private final LDAPService ldapService;

    @Inject
    public HenvendelseUtsendingServiceImpl(HenvendelsePortType henvendelsePortType,
                                           SendUtHenvendelsePortType sendUtHenvendelsePortType,
                                           BehandleHenvendelsePortType behandleHenvendelsePortType,
                                           OppgaveBehandlingService oppgaveBehandlingService,
                                           SakerService sakerService,
                                           @Named("pep") EnforcementPoint pep,
                                           SaksbehandlerInnstillingerService saksbehandlerInnstillingerService,
                                           PropertyResolver propertyResolver,
                                           PersonKjerneinfoServiceBi kjerneinfo,
                                           LDAPService ldapService) {

        this.henvendelsePortType = henvendelsePortType;
        this.sendUtHenvendelsePortType = sendUtHenvendelsePortType;
        this.behandleHenvendelsePortType = behandleHenvendelsePortType;
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.sakerService = sakerService;
        this.pep = pep;
        this.saksbehandlerInnstillingerService = saksbehandlerInnstillingerService;
        this.propertyResolver = propertyResolver;
        this.kjerneinfo = kjerneinfo;
        this.ldapService = ldapService;
    }

    @Override
    public void sendHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak) throws Exception {
        if (oppgaveId.isPresent() && oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId.get())) {
            throw new OppgaveErFerdigstilt();
        }

        XMLHenvendelse xmlHenvendelse = lagXMLHenvendelseOgSettEnhet(melding);

        WSSendUtHenvendelseResponse wsSendUtHenvendelseResponse = sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest()
                .withType(xmlHenvendelse.getHenvendelseType())
                .withFodselsnummer(melding.fnrBruker)
                .withAny(xmlHenvendelse));

        fullbyrdeSendtInnHenvendelse(melding, oppgaveId, sak, wsSendUtHenvendelseResponse.getBehandlingsId());
    }

    @Override
    public String opprettHenvendelse(String type, String fnr, String behandlingskjedeId) {
        return sendUtHenvendelsePortType.opprettHenvendelse(type, fnr, behandlingskjedeId);
    }

    @Override
    public void ferdigstillHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String behandlingsId) throws Exception {
        if (oppgaveId.isPresent() && oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId.get())) {
            throw new OppgaveErFerdigstilt();
        }

        XMLHenvendelse xmlHenvendelse = lagXMLHenvendelseOgSettEnhet(melding);

        sendUtHenvendelsePortType.ferdigstillHenvendelse(new WSFerdigstillHenvendelseRequest()
                .withAny(xmlHenvendelse)
                .withBehandlingsId(behandlingsId));

        fullbyrdeSendtInnHenvendelse(melding, oppgaveId, sak, behandlingsId);
    }

    @Override
    public void avbrytHenvendelse(String behandlingsId){
        sendUtHenvendelsePortType.avbrytHenvendelse(behandlingsId);
    }

    private XMLHenvendelse lagXMLHenvendelseOgSettEnhet(Melding melding) {
        XMLHenvendelseType type = getXMLHenvendelseTypeBasertPaaMeldingstype(melding.meldingstype);
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedMeldingTilBruker(melding, type);
        String enhet = isNotBlank(melding.brukersEnhet) ? melding.brukersEnhet : getEnhet(melding.fnrBruker);
        xmlHenvendelse.setBrukersEnhet(enhet);

        return xmlHenvendelse;
    }

    private void fullbyrdeSendtInnHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String behandlingsId) throws Exception {
        Temagruppe temagruppe = Temagruppe.valueOf(melding.temagruppe);
        melding.id = behandlingsId;

        if (melding.traadId == null) {
            melding.traadId = melding.id;
        }
        if (sak.isPresent()) {
            sakerService.knyttBehandlingskjedeTilSak(melding.fnrBruker, melding.traadId, sak.get());
        }
        if (oppgaveId.isPresent()) {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(oppgaveId.get(), temagruppe);
        }
        if (temagruppe == ANSOS) {
            merkSomKontorsperret(melding.fnrBruker, singletonList(melding.id));
        }
    }

    @Override
    public List<Melding> hentTraad(String fnr, String traadId, String valgtEnhet) {
        List<Melding> meldinger =
                on(henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest()
                        .withTyper(getHenvendelseTyper())
                        .withFodselsnummer(fnr))
                        .getAny())
                        .map(castTo(XMLHenvendelse.class))
                        .filter(where(BEHANDLINGSKJEDE_ID, equalTo(traadId)))
                        .map(tilMelding(propertyResolver, ldapService))
                        .map(journalfortTemaTilgang(valgtEnhet))
                        .collect(ELDSTE_FORST);

        if (meldinger.isEmpty()) {
            throw new ApplicationException(String.format("Fant ingen meldinger for fnr: %s med traadId: %s", fnr, traadId));
        }

        Melding sporsmal = meldinger.get(0);
        if (sporsmal.kontorsperretEnhet != null) {
            pep.assertAccess(forRequest(
                    actionId("kontorsperre"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", valgtEnhet),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(sporsmal.kontorsperretEnhet))));
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

    private String[] getHenvendelseTyper() {
        List<String> typer = new ArrayList<>();
        typer.add(SPORSMAL_SKRIFTLIG.name());
        typer.add(SVAR_SKRIFTLIG.name());
        typer.add(SVAR_OPPMOTE.name());
        typer.add(SVAR_TELEFON.name());
        typer.add(REFERAT_OPPMOTE.name());
        typer.add(REFERAT_TELEFON.name());
        typer.add(SPORSMAL_MODIA_UTGAAENDE.name());
        typer.add(SVAR_SBL_INNGAAENDE.name());

        if (FeatureToggle.visFeature(Feature.DELVISE_SVAR)) {
            typer.add(DELVIS_SVAR_SKRIFTLIG.name());
        }

        return typer.toArray(new String[typer.size()]);
    }

    private Transformer<Melding, Melding> journalfortTemaTilgang(final String valgtEnhet) {
        return melding -> {
            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(melding.journalfortTema))
            );
            if (isNotBlank(melding.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                melding.withFritekst(new Fritekst("", melding.skrevetAv, melding.opprettetDato));
            }

            return melding;
        };
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

    private static final Transformer<XMLHenvendelse, String> BEHANDLINGSKJEDE_ID = xmlHenvendelse -> xmlHenvendelse.getBehandlingskjedeId();

    private String getEnhet(String fnr) {
        HentKjerneinformasjonRequest kjerneinfoRequest = new HentKjerneinformasjonRequest(fnr);
        kjerneinfoRequest.setBegrunnet(true);
        Person person = kjerneinfo.hentKjerneinformasjon(kjerneinfoRequest).getPerson();

        if (person.getPersonfakta().getAnsvarligEnhet() != null) {
            return person.getPersonfakta().getAnsvarligEnhet().getOrganisasjonsenhet().getOrganisasjonselementId();
        } else {
            return null;
        }
    }
}
