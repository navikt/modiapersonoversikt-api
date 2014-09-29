package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.modig.security.tilgangskontroll.policy.request.PolicyRequest;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat;
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
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT_TELEFON;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_TELEFON;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.actionId;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceAttribute;
import static no.nav.modig.security.tilgangskontroll.utils.AttributeUtils.resourceId;
import static no.nav.modig.security.tilgangskontroll.utils.RequestUtils.forRequest;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createSporsmalFromXMLHenvendelse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createSvarEllerReferatFromXMLHenvendelse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createXMLHenvendelseMedMeldingTilBruker;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class HenvendelseUtsendingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected SendUtHenvendelsePortType sendUtHenvendelsePortType;
    @Inject
    @Named("pep")
    private EnforcementPoint pep;


    public void sendSvarEllerReferat(SvarEllerReferat svarEllerReferat) {
        XMLHenvendelseType type = XMLHenvendelseType.fromValue(svarEllerReferat.type.name());
        XMLHenvendelse xmlHenvendelse = createXMLHenvendelseMedMeldingTilBruker(svarEllerReferat, type);
        sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest()
                .withType(type.name())
                .withFodselsnummer(svarEllerReferat.fnr)
                .withAny(xmlHenvendelse));
    }

    public Sporsmal getSporsmalFromOppgaveId(String fnr, String oppgaveId) {
        List<Object> henvendelseliste =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withTyper(XMLHenvendelseType.SPORSMAL_SKRIFTLIG.name()).withFodselsnummer(fnr)).getAny();
        XMLHenvendelse henvendelse;
        for (Object o : henvendelseliste) {
            henvendelse = (XMLHenvendelse) o;
            if (erDetteEtSporsmalMedDenneGsakIden(oppgaveId, henvendelse)) {
                return createSporsmalFromXMLHenvendelse(henvendelse);
            }
        }
        throw new RuntimeException("Finner ikke spørsmål med oppgaveId " + oppgaveId);
    }

    private boolean erDetteEtSporsmalMedDenneGsakIden(String oppgaveId, XMLHenvendelse henvendelse) {
        return oppgaveId.equals(henvendelse.getOppgaveIdGsak());
    }

    public List<SvarEllerReferat> getSvarEllerReferatForSporsmal(String fnr, String sporsmalId) {
        List<String> xmlHenvendelseTyper = asList(SVAR_SKRIFTLIG.name(), SVAR_OPPMOTE.name(), SVAR_TELEFON.name(), REFERAT_OPPMOTE.name(), REFERAT_TELEFON.name());
        List<Object> henvendelseliste =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest()
                        .withTyper(xmlHenvendelseTyper)
                        .withFodselsnummer(fnr)).getAny();

        List<SvarEllerReferat> svarliste = new ArrayList<>();

        XMLHenvendelse henvendelse;
        for (Object o : henvendelseliste) {
            henvendelse = (XMLHenvendelse) o;
            XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
            if (erDetteEtSvarEllerReferatForSporsmalet(sporsmalId, xmlMetadata)) {
                svarliste.add(createSvarEllerReferatFromXMLHenvendelse(henvendelse));
            }
        }
        return on(svarliste)
                .map(journalfortTemaTilgang)
                .collect(ELDSTE_FORST);
    }

    private boolean erDetteEtSvarEllerReferatForSporsmalet(String sporsmalId, XMLMetadata xmlMetadata) {
        return xmlMetadata instanceof XMLMeldingTilBruker &&
                ((XMLMeldingTilBruker) xmlMetadata).getSporsmalsId() != null && ((XMLMeldingTilBruker) xmlMetadata).getSporsmalsId().equals(sporsmalId);
    }

    public Sporsmal getSporsmal(String sporsmalId) {
        XMLHenvendelse xmlHenvendelse =
                (XMLHenvendelse) henvendelsePortType.hentHenvendelse(new WSHentHenvendelseRequest().withBehandlingsId(sporsmalId)).getAny();
        return createSporsmalFromXMLHenvendelse(xmlHenvendelse);
    }

    private final Transformer<SvarEllerReferat, SvarEllerReferat> journalfortTemaTilgang = new Transformer<SvarEllerReferat, SvarEllerReferat>() {
        @Override
        public SvarEllerReferat transform(SvarEllerReferat svarEllerReferat) {
            PolicyRequest temagruppePolicyRequest = forRequest(
                    actionId("temagruppe"),
                    resourceId(""),
                    resourceAttribute("urn:nav:ikt:tilgangskontroll:xacml:resource:tema", defaultString(svarEllerReferat.journalfortTema))
            );
            if (!isBlank(svarEllerReferat.journalfortTema) && !pep.hasAccess(temagruppePolicyRequest)) {
                svarEllerReferat.fritekst = "";
            }

            return svarEllerReferat;
        }
    };

}
