package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Referat;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain.Svar;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static org.joda.time.DateTime.now;

public class HenvendelseUtils {

    public static Sporsmal createSporsmaalFromHenvendelse(Object henvendelsesObjekt) {
        XMLBehandlingsinformasjon info = (XMLBehandlingsinformasjon) henvendelsesObjekt;

        Sporsmal sporsmal = new Sporsmal(info.getBehandlingsId(), info.getOpprettetDato());

        XMLMetadata xmlMetadata = info.getMetadataListe().getMetadata().get(0);
        if(xmlMetadata instanceof XMLSporsmal){
            XMLSporsmal xmlSporsmal = (XMLSporsmal) xmlMetadata;
            sporsmal.tema = xmlSporsmal.getTemagruppe();
            sporsmal.fritekst = xmlSporsmal.getFritekst();
            sporsmal.oppgaveId = xmlSporsmal.getOppgaveIdGsak();
            return sporsmal;
        } else {
            throw new ApplicationException("Henvendelsen er ikke av typen XMLSporsmal : " + xmlMetadata);
        }
    }

    public static XMLBehandlingsinformasjon createXMLBehandlingsinformasjon(Svar svar) {
        return new XMLBehandlingsinformasjon()
                .withHenvendelseType(SVAR.name())
                .withAktor(new XMLAktor().withFodselsnummer(svar.fnr).withNavIdent(svar.navIdent))
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLSvar()
                                .withSporsmalsId(svar.sporsmalsId)
                                .withTemagruppe(svar.tema)
                                .withFritekst(svar.fritekst)
                ));
    }

    public static XMLBehandlingsinformasjon createXMLBehandlingsinformasjon(Referat referat) {
        return new XMLBehandlingsinformasjon()
                .withHenvendelseType(REFERAT.name())
                .withAktor(new XMLAktor().withFodselsnummer(referat.fnr).withNavIdent(referat.navIdent))
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLReferat().withTemagruppe(referat.tema).withKanal(referat.kanal).withFritekst(referat.fritekst)));
    }
}
