package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.services;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.SakUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.SendUtHenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v1.senduthenvendelse.meldinger.WSSendUtHenvendelseRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.henvendelse.HenvendelsePortType;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseListeRequest;
import no.nav.tjeneste.domene.brukerdialog.henvendelse.v2.meldinger.WSHentHenvendelseRequest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.SakUtils.createSporsmalFromHenvendelse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.SakUtils.createSvarFromHenvendelse;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util.SakUtils.createXMLHenvendelse;

public class HenvendelseUtsendingService {

    @Inject
    private HenvendelsePortType henvendelsePortType;
    @Inject
    protected SendUtHenvendelsePortType sendUtHenvendelsePortType;

    public void sendSvar(Svar svar) {
        XMLHenvendelse info = createXMLHenvendelse(svar);
        sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest().withType(SVAR.name()).withFodselsnummer(svar.fnr).withAny(info));
    }

    public void sendReferat(Referat referat) {
        XMLHenvendelse info = SakUtils.createXMLHenvendelse(referat);
        sendUtHenvendelsePortType.sendUtHenvendelse(new WSSendUtHenvendelseRequest().withType(REFERAT.name()).withFodselsnummer(referat.fnr).withAny(info));
    }

    public Sporsmal getSporsmalFromOppgaveId(String fnr, String oppgaveId) {
        List<Object> henvendelseliste =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withTyper(SPORSMAL.name()).withFodselsnummer(fnr)).getAny();

        XMLHenvendelse henvendelse;
        for (Object o : henvendelseliste) {
            henvendelse = (XMLHenvendelse) o;
            XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLSporsmal && oppgaveId.equals(((XMLSporsmal) xmlMetadata).getOppgaveIdGsak())) {
                return createSporsmalFromHenvendelse(henvendelse);
            }
        }
        return null;
    }

    public List<Svar> getSvarTilSporsmal(String fnr, String sporsmalId) {
        List<Object> henvendelseliste =
                henvendelsePortType.hentHenvendelseListe(new WSHentHenvendelseListeRequest().withTyper(SVAR.name()).withFodselsnummer(fnr)).getAny();

        List<Svar> svarliste = new ArrayList<>();

        XMLHenvendelse henvendelse;
        for (Object o : henvendelseliste) {
            henvendelse = (XMLHenvendelse) o;
            XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLSvar && ((XMLSvar) xmlMetadata).getSporsmalsId().equals(sporsmalId)) {
                svarliste.add(createSvarFromHenvendelse(henvendelse));
            }
        }
        return svarliste;
    }

    public Sporsmal getSporsmal(String sporsmalId) {
        XMLHenvendelse xmlHenvendelse =
                (XMLHenvendelse) henvendelsePortType.hentHenvendelse(new WSHentHenvendelseRequest().withBehandlingsId(sporsmalId)).getAny();
        return createSporsmalFromHenvendelse(xmlHenvendelse);
    }

}
