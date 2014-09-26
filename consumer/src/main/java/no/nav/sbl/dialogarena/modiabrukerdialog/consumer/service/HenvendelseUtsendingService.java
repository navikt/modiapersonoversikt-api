package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_OPPMOTE;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_SKRIFTLIG;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR_TELEFON;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createSporsmalFromXMLHenvendelse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createSvarEllerReferatFromXMLHenvendelse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util.HenvendelseUtils.createXMLHenvendelseMedMeldingTilBruker;

public class HenvendelseUtsendingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected SendUtHenvendelsePortType sendUtHenvendelsePortType;

    private static final List<String> SVAR = asList(SVAR_OPPMOTE.name(), SVAR_SKRIFTLIG.name(), SVAR_TELEFON.name());

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
        List<String> xmlHenvendelseTyper = asList(SVAR_SKRIFTLIG.name(), SVAR_OPPMOTE.name(), SVAR_TELEFON.name());
        List<Object> henvendelseliste =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest()
                        .withTyper(xmlHenvendelseTyper)
                        .withFodselsnummer(fnr)).getAny();

        List<SvarEllerReferat> svarliste = new ArrayList<>();

        XMLHenvendelse xmlHenvendelse;
        for (Object o : henvendelseliste) {
            xmlHenvendelse = (XMLHenvendelse) o;
            if (erDetteEtSvarEllerReferatForSporsmalet(sporsmalId, xmlHenvendelse)) {
                svarliste.add(createSvarEllerReferatFromXMLHenvendelse(xmlHenvendelse));
            }
        }
        return on(svarliste).collect(ELDSTE_FORST);
    }

    private boolean erDetteEtSvarEllerReferatForSporsmalet(String sporsmalId, XMLHenvendelse xmlHenvendelse) {
        return SVAR.contains(xmlHenvendelse.getHenvendelseType()) && sporsmalId.equals(xmlHenvendelse.getBehandlingskjedeId());
    }

    public Sporsmal getSporsmal(String sporsmalId) {
        XMLHenvendelse xmlHenvendelse =
                (XMLHenvendelse) henvendelsePortType.hentHenvendelse(new WSHentHenvendelseRequest().withBehandlingsId(sporsmalId)).getAny();
        return createSporsmalFromXMLHenvendelse(xmlHenvendelse);
    }

}
