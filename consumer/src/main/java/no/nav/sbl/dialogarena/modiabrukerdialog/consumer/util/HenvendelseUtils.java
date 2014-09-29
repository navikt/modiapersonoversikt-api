package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
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
            SvarEllerReferat svarEllerReferat = new SvarEllerReferat()
                    .withType(HENVENDELSETYPE_MAP.get(XMLHenvendelseType.fromValue(henvendelse.getHenvendelseType())))
                    .withFnr(henvendelse.getFnr())
                    .withOpprettetDato(henvendelse.getOpprettetDato())
                    .withSporsmalsId(xmlMeldingTilBruker.getSporsmalsId())
                    .withTemagruppe(xmlMeldingTilBruker.getTemagruppe())
                    .withKanal(xmlMeldingTilBruker.getKanal())
                    .withFritekst(xmlMeldingTilBruker.getFritekst())
                    .withKontorsperretEnhet(henvendelse.getKontorsperreEnhet())
                    .withNavIdent(xmlMeldingTilBruker.getNavident());

            fyllInnJournalforingsInformasjon(henvendelse, svarEllerReferat);
            return svarEllerReferat;
        } else {
            throw new ApplicationException("Henvendelsen er ikke av typen XMlMeldingTilBruker: " + xmlMetadata);
        }
    }

    private static void fyllInnJournalforingsInformasjon(XMLHenvendelse henvendelse, SvarEllerReferat svarEllerReferat) {
        XMLJournalfortInformasjon journalfortInformasjon = henvendelse.getJournalfortInformasjon();
        if (journalfortInformasjon != null) {
            svarEllerReferat
                    .withJournalfortTema(journalfortInformasjon.getJournalfortTema())
                    .withJournalfortSaksId(journalfortInformasjon.getJournalfortSaksId())
                    .withJournalfortAvNavIdent(journalfortInformasjon.getJournalforerNavIdent())
                    .withJournalfortDato(journalfortInformasjon.getJournalfortDato());
        }
    }

    public static XMLHenvendelse createXMLHenvendelseMedMeldingTilBruker(SvarEllerReferat svarEllerReferat, XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withFnr(svarEllerReferat.fnr)
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withTema(KONTAKT_NAV_SAKSTEMA)
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

    public static final Map<XMLHenvendelseType, Henvendelsetype> HENVENDELSETYPE_MAP = new HashMap<XMLHenvendelseType, Henvendelsetype>() {
        {
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, Henvendelsetype.SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, Henvendelsetype.SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, Henvendelsetype.SVAR_TELEFON);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, Henvendelsetype.REFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, Henvendelsetype.REFERAT_TELEFON);
        }
    };

}
