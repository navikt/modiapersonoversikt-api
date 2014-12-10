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
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.*;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.TransformerUtils.castTo;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.*;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.TIL_HENVENDELSE;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createXMLHenvendelseMedMeldingTilBruker;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

    public void sendHenvendelse(Henvendelse henvendelse) {
        try {
            sendHenvendelse(henvendelse, Optional.<String>none());
        } catch (OppgaveErFerdigstilt oppgaveErFerdigstilt) {
            throw new RuntimeException(oppgaveErFerdigstilt);
        }
    }

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
        List<Henvendelse> henvendelser =
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
                        .map(TIL_HENVENDELSE)
                        .map(journalfortTemaTilgang)
                        .collect(ELDSTE_FORST);

        Henvendelse sporsmal = henvendelser.get(0);
        if (sporsmal.kontorsperretEnhet != null) {
            pep.assertAccess(forRequest(
                    actionId("kontorsperre"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(sporsmal.kontorsperretEnhet))));
        }

        return henvendelser;
    }

    private static final Transformer<XMLHenvendelse, String> BEHANDLINGSKJEDE_ID = new Transformer<XMLHenvendelse, String>() {
        @Override
        public String transform(XMLHenvendelse xmlHenvendelse) {
            return xmlHenvendelse.getBehandlingskjedeId();
        }
    };

    private final Transformer<Henvendelse, Henvendelse> journalfortTemaTilgang = new Transformer<Henvendelse, Henvendelse>() {
        @Override
        public Henvendelse transform(Henvendelse henvendelse) {
            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(henvendelse.journalfortTema))
            );
            if (isNotBlank(henvendelse.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                henvendelse.fritekst = "";
            }

            return henvendelse;
        }
    };

    public static class OppgaveErFerdigstilt extends Exception {
    }
}
