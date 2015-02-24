package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.modig.lang.option.Optional;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.SaksbehandlerInnstillingerService;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseResponse;
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
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.*;
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

    public Melding sendHenvendelse(Melding melding) {
        try {
            return sendHenvendelse(melding, Optional.<String>none());
        } catch (OppgaveErFerdigstilt oppgaveErFerdigstilt) {
            throw new RuntimeException(oppgaveErFerdigstilt);
        }
    }

    public Melding sendHenvendelse(Melding melding, Optional<String> oppgaveId) throws OppgaveErFerdigstilt {
        if (oppgaveId.isSome() && oppgaveBehandlingService.oppgaveErFerdigstilt(oppgaveId.get())) {
            throw new OppgaveErFerdigstilt();
        }

        XMLHenvendelseType type = getXMLHenvendelseTypeBasertPaaMeldingstype(melding.meldingstype);
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedMeldingTilBruker(melding, type);
        WSSendUtHenvendelseResponse wsSendUtHenvendelseResponse = sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest()
                .withType(type.name())
                .withFodselsnummer(melding.fnrBruker)
                .withAny(xmlHenvendelse));

        melding.id = wsSendUtHenvendelseResponse.getBehandlingsId();
        if (melding.traadId == null) {
            melding.traadId = melding.id;
        }

        return melding;
    }

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
                        .map(TIL_MELDING)
                        .map(journalfortTemaTilgang)
                        .collect(ELDSTE_FORST);

        Melding sporsmal = meldinger.get(0);
        if (sporsmal.kontorsperretEnhet != null) {
            pep.assertAccess(forRequest(
                    actionId("kontorsperre"),
                    resourceId(""),
                    subjectAttribute("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet", defaultString(saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet())),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:ansvarlig-enhet", defaultString(sporsmal.kontorsperretEnhet))));
        }

        return meldinger;
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

    public static class OppgaveErFerdigstilt extends Exception {
    }
}
