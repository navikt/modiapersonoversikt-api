package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createXMLHenvendelseMedMeldingTilBruker;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.lagHenvendelseFraXMLHenvendelse;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HenvendelseUtsendingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    private SendUtHenvendelsePortType sendUtHenvendelsePortType;
    @Inject
    private OppgaveBehandlingService oppgaveBehandlingService;
    @Inject
    @Named("pep")
    private EnforcementPoint pep;
    @Inject
    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private static final List<String> SVAR = asList(SVAR_OPPMOTE.name(), SVAR_SKRIFTLIG.name(), SVAR_TELEFON.name());

    public void sendHenvendelse(Henvendelse henvendelse, Optional<String> oppgaveId) throws OppgaveErFerdigstilt {
        if (oppgaveId.isSome() && oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId.get())) {
            throw new OppgaveErFerdigstilt();
        }

        XMLHenvendelseType type = XMLHenvendelseType.fromValue(henvendelse.type.name());
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedMeldingTilBruker(henvendelse, type);
        sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest()
                .withType(type.name())
                .withFodselsnummer(henvendelse.fnr)
                .withAny(xmlHenvendelse));
    }

    public List<Henvendelse> hentTraad(String fnr, String traadId) {
        List<Henvendelse> traad = new ArrayList<>();

        XMLHenvendelse xmlHenvendelse =
                (XMLHenvendelse) henvendelsePortType.hentHenvendelse(new WSHentHenvendelseRequest().withBehandlingsId(traadId)).getAny();

        String kontorsperreEnhet = xmlHenvendelse.getKontorsperreEnhet();
        if (kontorsperreEnhet != null) {
            pep.assertAccess(forRequest(
                    actionId("kontorsperre"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(kontorsperreEnhet))));
        }

        Optional<Henvendelse> henvendelse = lagHenvendelseFraXMLHenvendelse(xmlHenvendelse);
        if (henvendelse.isSome()) {
            traad.add(henvendelse.get());
            traad.addAll(hentHenvendelserTilTraad(fnr, traadId));
        }

        return on(traad).collect(ELDSTE_FORST);
    }

    private List<Henvendelse> hentHenvendelserTilTraad(String fnr, String traadId) {
        List<Object> henvendelseliste =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest()
                        .withTyper(SVAR)
                        .withFodselsnummer(fnr)).getAny();

        List<Henvendelse> svarliste = new ArrayList<>();

        XMLHenvendelse xmlHenvendelse;
        for (Object o : henvendelseliste) {
            xmlHenvendelse = (XMLHenvendelse) o;
            if (henvendelseTilTraad(traadId, xmlHenvendelse)) {
                svarliste.add(lagHenvendelseFraXMLHenvendelse(xmlHenvendelse).get());
            }
        }
        return on(svarliste)
                .map(journalfortTemaTilgang)
                .collect();
    }

    private boolean henvendelseTilTraad(String traadId, XMLHenvendelse xmlHenvendelse) {
        return SVAR.contains(xmlHenvendelse.getHenvendelseType()) && traadId.equals(xmlHenvendelse.getBehandlingskjedeId());
    }

    private final Transformer<Henvendelse, Henvendelse> journalfortTemaTilgang = new Transformer<Henvendelse, Henvendelse>() {
        @Override
        public Henvendelse transform(Henvendelse henvendelse) {
            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(henvendelse.journalfortTema))
            );
            if (!isBlank(henvendelse.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                henvendelse.fritekst = "";
            }

            return henvendelse;
        }
    };

    public static class OppgaveErFerdigstilt extends Exception {
    }
}
