package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.util;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype;
import org.apache.commons.collections15.Transformer;

import java.util.*;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain.Meldingstype.*;
import static org.joda.time.DateTime.now;

public class HenvendelseUtils {

    public static final String KONTAKT_NAV_SAKSTEMA = "KNA";

    public static final Transformer<XMLHenvendelse, Melding> TIL_MELDING = new Transformer<XMLHenvendelse, Melding>() {
        @Override
        public Melding transform(XMLHenvendelse xmlHenvendelse) {
            Melding melding = new Melding()
                    .withId(xmlHenvendelse.getBehandlingsId())
                    .withType(MELDINGSTYPE_MAP.get(XMLHenvendelseType.fromValue(xmlHenvendelse.getHenvendelseType())))
                    .withFnr(xmlHenvendelse.getFnr())
                    .withOpprettetDato(xmlHenvendelse.getOpprettetDato())
                    .withTraadId(xmlHenvendelse.getBehandlingskjedeId())
                    .withKontorsperretEnhet(xmlHenvendelse.getKontorsperreEnhet())
                    .withOppgaveId(xmlHenvendelse.getOppgaveIdGsak())
                    .withEksternAktor(xmlHenvendelse.getEksternAktor())
                    .withTilknyttetEnhet(xmlHenvendelse.getTilknyttetEnhet());

            fyllInnJournalforingsInformasjon(xmlHenvendelse, melding);

            if (xmlHenvendelse.getMetadataListe() == null) {
                return melding
                        .withTemagruppe(null)
                        .withKanal(null)
                        .withFritekst(null)
                        .withNavIdent(null);
            }

            XMLMetadata xmlMetadata = xmlHenvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker xmlMeldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
                melding
                        .withTemagruppe(xmlMeldingTilBruker.getTemagruppe())
                        .withKanal(xmlMeldingTilBruker.getKanal())
                        .withFritekst(xmlMeldingTilBruker.getFritekst())
                        .withNavIdent(xmlMeldingTilBruker.getNavident());
            } else if (xmlMetadata instanceof XMLMeldingFraBruker) {
                XMLMeldingFraBruker xmlMeldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
                melding
                        .withTemagruppe(xmlMeldingFraBruker.getTemagruppe())
                        .withFritekst(xmlMeldingFraBruker.getFritekst());
            } else {
                throw new RuntimeException("XMLMetadata er av en ukjent type: " + xmlMetadata);
            }

            return melding;
        }
    };

    private static void fyllInnJournalforingsInformasjon(XMLHenvendelse xmlHenvendelse, Melding melding) {
        XMLJournalfortInformasjon journalfortInformasjon = xmlHenvendelse.getJournalfortInformasjon();
        if (journalfortInformasjon != null) {
            melding
                    .withJournalfortTema(journalfortInformasjon.getJournalfortTema())
                    .withJournalfortSaksId(journalfortInformasjon.getJournalfortSaksId())
                    .withJournalfortAvNavIdent(journalfortInformasjon.getJournalforerNavIdent())
                    .withJournalfortDato(journalfortInformasjon.getJournalfortDato());
        }
    }

    public static XMLHenvendelse createXMLHenvendelseMedMeldingTilBruker(Melding henvendelse, XMLHenvendelseType type) {
        return new XMLHenvendelse()
                .withHenvendelseType(type.name())
                .withFnr(henvendelse.fnrBruker)
                .withOpprettetDato(now())
                .withAvsluttetDato(now())
                .withTema(KONTAKT_NAV_SAKSTEMA)
                .withBehandlingskjedeId(henvendelse.traadId)
                .withKontorsperreEnhet(henvendelse.kontorsperretEnhet)
                .withEksternAktor(henvendelse.eksternAktor)
                .withTilknyttetEnhet(henvendelse.tilknyttetEnhet)
                .withMetadataListe(new XMLMetadataListe().withMetadata(
                        new XMLMeldingTilBruker()
                                .withTemagruppe(henvendelse.temagruppe)
                                .withKanal(henvendelse.kanal)
                                .withFritekst(henvendelse.fritekst)
                                .withNavident(henvendelse.navIdent)
                ));
    }

    public static final Map<XMLHenvendelseType, Meldingstype> MELDINGSTYPE_MAP = new HashMap<XMLHenvendelseType, Meldingstype>() {
        {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG);
            put(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, SPORSMAL_MODIA_UTGAAENDE);
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, SVAR_TELEFON);
            put(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, SVAR_SBL_INNGAAENDE);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, SAMTALEREFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, SAMTALEREFERAT_TELEFON);
        }
    };

    public static XMLHenvendelseType getXMLHenvendelseTypeBasertPaaMeldingstype(Meldingstype type){
        for(Map.Entry<XMLHenvendelseType, Meldingstype> entry : MELDINGSTYPE_MAP.entrySet()){
            if(entry.getValue().name().equals(type.name())){
                return entry.getKey();
            }
        }
        throw new RuntimeException("Det finnes ingen XMLHenvendelseType som korresponderer til Meldingstypen " + type.name());
    }

}
