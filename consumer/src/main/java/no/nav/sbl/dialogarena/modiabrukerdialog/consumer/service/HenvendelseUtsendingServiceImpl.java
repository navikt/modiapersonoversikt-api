package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.ANSOS;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe.OKSOS;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.tilMelding;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createXMLHenvendelseMedMeldingTilBruker;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.getXMLHenvendelseTypeBasertPaaMeldingstype;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.BadRequestException;

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
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Policies;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangTilKontorSperreData;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangTilOksosSperreData;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.TilgangTilTemaData;
import no.nav.sbl.dialogarena.modiabrukerdialog.tilgangskontroll.Tilgangskontroll;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.behandlehenvendelse.BehandleHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.WSBehandlingskjedeErAlleredeBesvart;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSFerdigstillHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HenvendelseUtsendingServiceImpl implements HenvendelseUtsendingService {

    private final HenvendelsePortType henvendelsePortType;
    private final SendUtHenvendelsePortType sendUtHenvendelsePortType;
    private final BehandleHenvendelsePortType behandleHenvendelsePortType;
    private final OppgaveBehandlingService oppgaveBehandlingService;
    private final SakerService sakerService;
    private final ContentRetriever propertyResolver;
    private final PersonKjerneinfoServiceBi kjerneinfo;
    private final LDAPService ldapService;
    private final Tilgangskontroll tilgangskontroll;
    private static final Logger logger = LoggerFactory.getLogger(HenvendelseUtsendingServiceImpl.class);

    @Inject
    public HenvendelseUtsendingServiceImpl(HenvendelsePortType henvendelsePortType,
                                           SendUtHenvendelsePortType sendUtHenvendelsePortType,
                                           BehandleHenvendelsePortType behandleHenvendelsePortType,
                                           OppgaveBehandlingService oppgaveBehandlingService,
                                           SakerService sakerService,
                                           Tilgangskontroll tilgangskontroll,
                                           @Named("propertyResolver") ContentRetriever propertyResolver,
                                           PersonKjerneinfoServiceBi kjerneinfo,
                                           LDAPService ldapService) {

        this.henvendelsePortType = henvendelsePortType;
        this.sendUtHenvendelsePortType = sendUtHenvendelsePortType;
        this.behandleHenvendelsePortType = behandleHenvendelsePortType;
        this.oppgaveBehandlingService = oppgaveBehandlingService;
        this.sakerService = sakerService;
        this.tilgangskontroll = tilgangskontroll;
        this.propertyResolver = propertyResolver;
        this.kjerneinfo = kjerneinfo;
        this.ldapService = ldapService;
    }

    @Override
    public String sendHenvendelse(Melding melding, Optional<String> oppgaveId,
        Optional<Sak> sak, String saksbehandlersValgteEnhet) throws Exception {
        if (oppgaveId.isPresent() && oppgaveBehandlingService
            .oppgaveErFerdigstilt(oppgaveId.get())) {
            throw new OppgaveErFerdigstilt();
        }

        XMLHenvendelse xmlHenvendelse = lagXMLHenvendelseOgSettEnhet(melding);

        WSSendUtHenvendelseResponse wsSendUtHenvendelseResponse = sendUtHenvendelsePortType
            .sendUtHenvendelse(new WSSendUtHenvendelseRequest()
                .withType(xmlHenvendelse.getHenvendelseType())
                .withFodselsnummer(melding.fnrBruker)
                .withAny(xmlHenvendelse));

        try {
            fullbyrdeSendtInnHenvendelse(melding, oppgaveId, sak,
                wsSendUtHenvendelseResponse.getBehandlingsId(), saksbehandlersValgteEnhet);
            return wsSendUtHenvendelseResponse.getBehandlingsId();
        } catch (Exception e) {
            throw new JournalforingFeiletException(e);
        }
    }

    @Override
    public String opprettHenvendelse(String type, String fnr, String behandlingskjedeId) {
        return sendUtHenvendelsePortType.opprettHenvendelse(type, fnr, behandlingskjedeId);
    }

    @Override
    public void ferdigstillHenvendelse(Melding melding, Optional<String> oppgaveId, Optional<Sak> sak, String behandlingsId, String saksbehandlersValgteEnhet) throws Exception {
        if (oppgaveId.isPresent() && oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId.get())) {
            logger.error("Oppgaven er ferdigstilt med id: {}", oppgaveId);
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
        oppgaveId.ifPresent(s -> oppgaveBehandlingService
            .ferdigstillOppgaveIGsak(s, temagruppe, saksbehandlersValgteEnhet));
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
            TilgangTilKontorSperreData data = new TilgangTilKontorSperreData(valgtEnhet, sporsmal.kontorsperretEnhet);
            tilgangskontroll
                    .check(Policies.tilgangTilKontorsperretMelding.with(data))
                    .getDecision()
                    .assertPermit();
        }
        if (sporsmal.gjeldendeTemagruppe == OKSOS) {
            TilgangTilOksosSperreData data = new TilgangTilOksosSperreData(valgtEnhet, sporsmal.brukersEnhet);
            tilgangskontroll
                    .check(Policies.tilgangTilOksosMelding.with(data))
                    .getDecision()
                    .assertPermit();
        }
    }

    private Function<Melding, Melding> journalfortTemaTilgang(final String valgtEnhet) {
        return melding -> {
            TilgangTilTemaData data = new TilgangTilTemaData(valgtEnhet, melding.journalfortTema);
            boolean tilgangPaTema = tilgangskontroll
                    .check(Policies.tilgangTilTema.with(data))
                    .getDecision()
                    .isPermit();

            if (isNotBlank(melding.journalfortTema) && !tilgangPaTema) {
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
