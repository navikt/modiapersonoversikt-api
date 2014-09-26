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

import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.SvarEllerReferat.Henvendelsetype;
import static org.joda.time.DateTime.now;

public class HenvendelseUtils {

    public static final String KONTAKT_NAV_SAKSTEMA = "KNA";

    public static Sporsmal createSporsmalFromXMLHenvendelse(XMLHenvendelse henvendelse) {
        Sporsmal sporsmal = new Sporsmal(henvendelse.getBehandlingsId(), henvendelse.getOpprettetDato());
        sporsmal.konorsperretEnhet = henvendelse.getKontorsperreEnhet();
        sporsmal.oppgaveId = henvendelse.getOppgaveIdGsak();

        if (henvendelse.getMetadataListe() == null) {
            sporsmal.temagruppe = null;
            sporsmal.fritekst = null;
            return sporsmal;
        }

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
        SvarEllerReferat svarEllerReferat = new SvarEllerReferat()
                .withType(HENVENDELSETYPE_MAP.get(XMLHenvendelseType.fromValue(henvendelse.getHenvendelseType())))
                .withFnr(henvendelse.getFnr())
                .withOpprettetDato(henvendelse.getOpprettetDato())
                .withSporsmalsId(henvendelse.getBehandlingskjedeId())
                .withKontorsperretEnhet(henvendelse.getKontorsperreEnhet());

        if (henvendelse.getMetadataListe() == null) {
            return svarEllerReferat
                    .withTemagruppe(null)
                    .withKanal(null)
                    .withFritekst(null)
                    .withNavIdent(null);
        }

        XMLMetadata xmlMetadata = henvendelse.getMetadataListe().getMetadata().get(0);
        if (xmlMetadata instanceof XMLMeldingTilBruker) {
            XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
            return svarEllerReferat
                    .withTemagruppe(xmlMeldingTilBruker.getTemagruppe())
                    .withKanal(xmlMeldingTilBruker.getKanal())
                    .withFritekst(xmlMeldingTilBruker.getFritekst())
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
                .withTema(KONTAKT_NAV_SAKSTEMA)
                .withBehandlingskjedeId(svarEllerReferat.sporsmalsId)
                .withKontorsperreEnhet(svarEllerReferat.kontorsperretEnhet)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withTemagruppe(svarEllerReferat.temagruppe)
                                .withKanal(svarEllerReferat.kanal)
                                .withFritekst(svarEllerReferat.fritekst)
                                .withNavident(svarEllerReferat.navIdent)
                ));
    }

    public static final Map<XMLHenvendelseType, Henvendelsetype> HENVENDELSETYPE_MAP = new HashMap<XMLHenvendelseType, Henvendelsetype>() {
        {
            put(XMLHenvendelseType.SVAR_SKRIFTLIG,  Henvendelsetype.SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE,    Henvendelsetype.SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON,    Henvendelsetype.SVAR_TELEFON);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, Henvendelsetype.REFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, Henvendelsetype.REFERAT_TELEFON);
        }
    };

}
