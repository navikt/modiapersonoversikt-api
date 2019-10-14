package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.brukerdialog.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.kjerneinfo.consumer.fim.person.PersonKjerneinfoServiceBi;
import no.nav.kjerneinfo.consumer.fim.person.to.HentKjerneinformasjonRequest;
import no.nav.kjerneinfo.domain.person.Person;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.modig.content.ContentRetriever;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.HenvendelseUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.exceptions.TraadAlleredeBesvart;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.HenvendelseUtsendingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.OppgaveBehandlingService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.gsak.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.ldap.LDAPService;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.cache.HenvendelsePortTypeCacheUtil;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.henvendelse.delsvar.DelsvarSammenslaaer;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.henvendelse.delsvar.DelsvarUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.WSBehandlingskjedeErAlleredeBesvart;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.brukerdialog.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ANSOS;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.OKSOS;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.tilMelding;
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
    private final ContentRetriever propertyResolver;
    private final PersonKjerneinfoServiceBi kjerneinfo;
    private final LDAPService ldapService;

    @Inject
    public HenvendelseUtsendingServiceImpl(HenvendelsePortType henvendelsePortType,
                                           SendUtHenvendelsePortType sendUtHenvendelsePortType,
                                           BehandleHenvendelsePortType behandleHenvendelsePortType,
                                           OppgaveBehandlingService oppgaveBehandlingService,
                                           SakerService sakerService,
                                           @Named("pep") EnforcementPoint pep,
                                           @Named("propertyResolver") ContentRetriever propertyResolver,
                                           PersonKjerneinfoServiceBi kjerneinfo,
                                           LDAPService ldapService) {

        this.henvendelsePortType = henvendelsePortType;
        this.sendUtHenvendelsePortType = sendUtHenvendelsePortType;
        this.behandleHenvendelsePortType = behandleHenvendelsePortType;
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.sakerService = sakerService;
        this.pep = pep;
        this.propertyResolver = propertyResolver;
        this.kjerneinfo = kjerneinfo;
        this.ldapService = ldapService;
    }

    @Override
    public void sendHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String saksbehandlersValgteEnhet) throws Exception {
        if (oppgaveId.isPresent() && oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId.get())) {
            throw new OppgaveErFerdigstilt();
        }

        XMLHenvendelse xmlHenvendelse = lagXMLHenvendelseOgSettEnhet(melding);

        WSSendUtHenvendelseResponse wsSendUtHenvendelseResponse = sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest()
                .withType(xmlHenvendelse.getHenvendelseType())
                .withFodselsnummer(melding.fnrBruker)
                .withAny(xmlHenvendelse));

        fullbyrdeSendtInnHenvendelse(melding, oppgaveId, sak, wsSendUtHenvendelseResponse.getBehandlingsId(), saksbehandlersValgteEnhet);
    }

    @Override
    public String opprettHenvendelse(String type, String fnr, String behandlingskjedeId) {
        return sendUtHenvendelsePortType.opprettHenvendelse(type, fnr, behandlingskjedeId);
    }

    @Override
    public void ferdigstillHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String behandlingsId, String saksbehandlersValgteEnhet) throws Exception {
        if (oppgaveId.isPresent() && oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId.get())) {
            throw new OppgaveErFerdigstilt();
        }

        XMLHenvendelse xmlHenvendelse = lagXMLHenvendelseOgSettEnhet(melding);

        sendUtHenvendelsePortType.ferdigstillHenvendelse(new WSFerdigstillHenvendelseRequest()
                .withAny(xmlHenvendelse)
                .withBehandlingsId(behandlingsId));

        fullbyrdeSendtInnHenvendelse(melding, oppgaveId, sak, behandlingsId, saksbehandlersValgteEnhet);
        invaliderCacheForHentHenvendelseListe(melding);
    }

    private void invaliderCacheForHentHenvendelseListe(Melding melding) {
        HenvendelsePortTypeCacheUtil.invaliderHentHenvendelseListeCacheElement(henvendelsePortType, melding.fnrBruker, HenvendelseUtils.AKTUELLE_HENVENDELSE_TYPER);
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

    private void fullbyrdeSendtInnHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String behandlingsId, String saksbehandlersValgteEnhet) throws Exception {
        Temagruppe temagruppe = Temagruppe.valueOf(melding.temagruppe);
        melding.id = behandlingsId;

        if (melding.traadId == null) {
            melding.traadId = melding.id;
        }
        if (sak.isPresent()) {
            sakerService.knyttBehandlingskjedeTilSak(melding.fnrBruker, melding.traadId, sak.get(), saksbehandlersValgteEnhet);
        }
        if (oppgaveId.isPresent()) {
            oppgaveBehandlingService.ferdigstillOppgaveIGsak(oppgaveId.get(), temagruppe, saksbehandlersValgteEnhet);
        }
        if (temagruppe == ANSOS) {
            merkSomKontorsperret(melding.fnrBruker, singletonList(melding.id));
        }
    }

    @Override
    public List<Melding> hentTraad(String fnr, String traadId, String valgtEnhet) {
        List<Melding> meldinger =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest()
                        .withTyper(HenvendelseUtils.AKTUELLE_HENVENDELSE_TYPER)
                        .withFodselsnummer(fnr))
                        .getAny().stream()
                        .map(melding -> (XMLHenvendelse) melding)
                        .filter(henvendelse -> traadId.equals(henvendelse.getBehandlingskjedeId()))
                        .map(tilMelding(propertyResolver, ldapService))
                        .map(journalfortTemaTilgang(valgtEnhet))
                        .sorted(ELDSTE_FORST)
                        .collect(toList());

        if (meldinger.isEmpty()) {
            throw new ApplicationException(String.format("Fant ingen meldinger for fnr: %s med traadId: %s", fnr, traadId));
        }

        gjorTilgangSjekk(valgtEnhet, meldinger);

        if (DelsvarUtils.harAvsluttendeSvarEtterDelsvar(meldinger)) {
            meldinger = DelsvarSammenslaaer.sammenslaFullforteDelsvar(meldinger);
        }

        return meldinger;
    }

    private void gjorTilgangSjekk(String valgtEnhet, List<Melding> meldinger) {
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
    }

    private Function<Melding, Melding> journalfortTemaTilgang(final String valgtEnhet) {
        return melding -> {
            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(valgtEnhet)),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(melding.journalfortTema))
            );
            if (isNotBlank(melding.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                melding.withFritekst(new Fritekst("", melding.skrevetAv, melding.ferdigstiltDato));
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

    @Override
    public String slaaSammenTraader(List<String> traadIder) {
        try {
            return sendUtHenvendelsePortType.slaSammenHenvendelser(traadIder);
        } catch (WSBehandlingskjedeErAlleredeBesvart e) {
            throw new TraadAlleredeBesvart(e.getFaultInfo());
        }
    }

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
