package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse;

import java.util.HashMap;
import java.util.Map;

import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.modiabrukerdialog.consumer.domain.Henvendelse.Henvendelsetype;
import static org.joda.time.DateTime.now;

public class HenvendelseUtils {

    public static final String KONTAKT_NAV_SAKSTEMA = "KNA";

    public static Optional<Henvendelse> lagHenvendelseFraXMLHenvendelse(XMLHenvendelse xmlHenvendelse) {
        Henvendelse henvendelse = new Henvendelse()
                .withId(xmlHenvendelse.getBehandlingsId())
                .withType(HENVENDELSETYPE_MAP.get(XMLHenvendelseType.fromValue(xmlHenvendelse.getHenvendelseType())))
                .withFnr(xmlHenvendelse.getFnr())
                .withOpprettetDato(xmlHenvendelse.getOpprettetDato())
                .withTraadId(xmlHenvendelse.getBehandlingskjedeId())
                .withKontorsperretEnhet(xmlHenvendelse.getKontorsperreEnhet())
                .withOppgaveId(xmlHenvendelse.getOppgaveIdGsak());

        if (xmlHenvendelse.getMetadataListe() == null) {
            return optional(henvendelse
                    .withTemagruppe(null)
                    .withKanal(null)
                    .withFritekst(null)
                    .withNavIdent(null));
        }

        XMLMetadata xmlMetadata = xmlHenvendelse.getMetadataListe().getMetadata().get(0);
        if (xmlMetadata instanceof XMLMeldingTilBruker) {
            XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
            henvendelse
                    .withTemagruppe(xmlMeldingTilBruker.getTemagruppe())
                    .withKanal(xmlMeldingTilBruker.getKanal())
                    .withFritekst(xmlMeldingTilBruker.getFritekst())
                    .withNavIdent(xmlMeldingTilBruker.getNavident());

            fyllInnJournalforingsInformasjon(xmlHenvendelse, henvendelse);
        } else if (xmlMetadata instanceof XMLMeldingFraBruker) {
            XMLMeldingFraBruker xmlMeldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
            henvendelse
                    .withTemagruppe(xmlMeldingFraBruker.getTemagruppe())
                    .withFritekst(xmlMeldingFraBruker.getFritekst());
        } else {
            return none();
        }

        return optional(henvendelse);
    }

    private static void fyllInnJournalforingsInformasjon(XMLHenvendelse henvendelse, Henvendelse utgaende) {
        XMLJournalfortInformasjon journalfortInformasjon = henvendelse.getJournalfortInformasjon();
        if (journalfortInformasjon != null) {
            utgaende
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
