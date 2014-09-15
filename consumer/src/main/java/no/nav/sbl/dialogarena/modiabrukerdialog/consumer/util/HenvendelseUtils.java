package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadataListe;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Sporsmal;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype.REFERAT;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype.SVAR;
import static org.joda.time.DateTime.now;

public class HenvendelseUtils {

    public static Sporsmal createSporsmalFromXMLHenvendelse(XMLHenvendelse henvendelse) {
        Sporsmal sporsmal = new Sporsmal(henvendelse.getBehandlingsId(), henvendelse.getOpprettetDato());
        sporsmal.konorsperretEnhet = henvendelse.getKontorsperreEnhet();
        sporsmal.oppgaveId = henvendelse.getOppgaveIdGsak();

        XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
        if (xmlMetadata instanceof XMLMeldingFraBruker) {
            XMLMeldingFraBruker xmlMeldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
            sporsmal.temagruppe = xmlMeldingFraBruker.getTemagruppe();
            sporsmal.fritekst = xmlMeldingFraBruker.getFritekst();
            return sporsmal;
        } else {
            throw new ApplicationException("Henvendelsen er ikke av typen XMLMeldingFraBruker: " + xmlMetadata);
        }
    }

    public static SvarEllerReferat createSvarEllerReferatFromXMLHenvendelse(XMLHenvendelse henvendelse) {
        XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
        if (xmlMetadata instanceof XMLMeldingTilBruker) {
            XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
            return new SvarEllerReferat()
                    .withType(henvendelse.getHenvendelseType().equals(XMLHenvendelseType.SVAR.name()) ? SVAR : REFERAT)
                    .withFnr(henvendelse.getFnr())
                    .withOpprettetDato(henvendelse.getOpprettetDato())
                    .withSporsmalsId(xmlMeldingTilBruker.getSporsmalsId())
                    .withTemagruppe(xmlMeldingTilBruker.getTemagruppe())
                    .withKanal(xmlMeldingTilBruker.getKanal())
                    .withFritekst(xmlMeldingTilBruker.getFritekst())
                    .withKontorsperretEnhet(henvendelse.getKontorsperreEnhet())
                    .withNavIdent(xmlMeldingTilBruker.getNavident());
        } else {
            throw new ApplicationException("Henvendelsen er ikke av typen XMlMeldingTilBruker: " + xmlMetadata);
        }
    }

    public static XMLHenvendelse createXMLHenvendelseMedMeldingTilBruker(SvarEllerReferat svarEllerReferat, XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withFnr(svarEllerReferat.fnr)
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withKontorsperreEnhet(svarEllerReferat.kontorsperretEnhet)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withSporsmalsId(svarEllerReferat.sporsmalsId)
                                .withTemagruppe(svarEllerReferat.temagruppe)
                                .withKanal(svarEllerReferat.kanal)
                                .withFritekst(svarEllerReferat.fritekst)
                                .withNavident(svarEllerReferat.navIdent)
                ));
    }

}
