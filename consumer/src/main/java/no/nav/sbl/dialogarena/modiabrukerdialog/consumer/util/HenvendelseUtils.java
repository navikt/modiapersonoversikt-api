package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse;
import org.apache.commons.collections15.Transformer;

import java.util.HashMap;
import java.util.Map;

import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse.Henvendelsetype;
import static org.joda.time.DateTime.now;

public class HenvendelseUtils {

    public static final String KONTAKT_NAV_SAKSTEMA = "KNA";

    public static final Transformer<XMLHenvendelse, Henvendelse> TIL_HENVENDELSE = new Transformer<XMLHenvendelse, Henvendelse>() {
        @Override
        public Henvendelse transform(XMLHenvendelse xmlHenvendelse) {
            Henvendelse henvendelse = new Henvendelse()
                    .withId(xmlHenvendelse.getBehandlingsId())
                    .withType(HENVENDELSETYPE_MAP.get(XMLHenvendelseType.fromValue(xmlHenvendelse.getHenvendelseType())))
                    .withFnr(xmlHenvendelse.getFnr())
                    .withOpprettetDato(xmlHenvendelse.getOpprettetDato())
                    .withTraadId(xmlHenvendelse.getBehandlingskjedeId())
                    .withKontorsperretEnhet(xmlHenvendelse.getKontorsperreEnhet())
                    .withOppgaveId(xmlHenvendelse.getOppgaveIdGsak());

            fyllInnJournalforingsInformasjon(xmlHenvendelse, henvendelse);

            if (xmlHenvendelse.getMetadataListe() == null) {
                return henvendelse
                        .withTemagruppe(null)
                        .withKanal(null)
                        .withFritekst(null)
                        .withNavIdent(null);
            }

            XMLMetadata xmlMetadata = xmlHenvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
                henvendelse
                        .withTemagruppe(xmlMeldingTilBruker.getTemagruppe())
                        .withKanal(xmlMeldingTilBruker.getKanal())
                        .withFritekst(xmlMeldingTilBruker.getFritekst())
                        .withNavIdent(xmlMeldingTilBruker.getNavident());
            } else if (xmlMetadata instanceof XMLMeldingFraBruker) {
                XMLMeldingFraBruker xmlMeldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
                henvendelse
                        .withTemagruppe(xmlMeldingFraBruker.getTemagruppe())
                        .withFritekst(xmlMeldingFraBruker.getFritekst());
            } else {
                throw new RuntimeException("XMLMetadata er av en ukjent type: " + xmlMetadata);
            }

            return henvendelse;
        }
    };

    private static void fyllInnJournalforingsInformasjon(XMLHenvendelse xmlHenvendelse, Henvendelse henvendelse) {
        XMLJournalfortInformasjon journalfortInformasjon = xmlHenvendelse.getJournalfortInformasjon();
        if (journalfortInformasjon != null) {
            henvendelse
                    .withJournalfortTema(journalfortInformasjon.getJournalfortTema())
                    .withJournalfortSaksId(journalfortInformasjon.getJournalfortSaksId())
                    .withJournalfortAvNavIdent(journalfortInformasjon.getJournalforerNavIdent())
                    .withJournalfortDato(journalfortInformasjon.getJournalfortDato());
        }
    }

    public static XMLHenvendelse createXMLHenvendelseMedMeldingTilBruker(Henvendelse henvendelse, XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withFnr(henvendelse.fnr)
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withTema(KONTAKT_NAV_SAKSTEMA)
                .withBehandlingskjedeId(henvendelse.traadId)
                .withKontorsperreEnhet(henvendelse.kontorsperretEnhet)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withTemagruppe(henvendelse.temagruppe)
                                .withKanal(henvendelse.kanal)
                                .withFritekst(henvendelse.fritekst)
                                .withNavident(henvendelse.navIdent)
                ));
    }

    public static final Map<XMLHenvendelseType, Henvendelsetype> HENVENDELSETYPE_MAP = new HashMap<XMLHenvendelseType, Henvendelsetype>() {
        {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, Henvendelsetype.SPORSMAL_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, Henvendelsetype.SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, Henvendelsetype.SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, Henvendelsetype.SVAR_TELEFON);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, Henvendelsetype.REFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, Henvendelsetype.REFERAT_TELEFON);
        }
    };

}
