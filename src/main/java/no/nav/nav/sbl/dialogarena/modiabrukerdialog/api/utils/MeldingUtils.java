package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.content.PropertyResolver;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Temagruppe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding.TRAAD_ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Meldingstype.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Status.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.FRA_BRUKER;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.VisningUtils.FRA_NAV;

public class MeldingUtils {

    public static Map<String, List<Melding>> skillUtTraader(List<Melding> meldinger) {

        Map<String, List<Melding>> ufiltrertTraaderMap = on(meldinger).reduce(indexBy(TRAAD_ID), new HashMap<String, List<Melding>>());

        List<Map.Entry<String, List<Melding>>> filtrertEntryList = on(ufiltrertTraaderMap).filter(DET_FINNES_EN_ROTMELDING).collect();

        return lagMap(filtrertEntryList);
    }

    private static Map<String, List<Melding>> lagMap(List<Map.Entry<String, List<Melding>>> entries) {
        HashMap<String, List<Melding>> map = new HashMap<>();
        for (Map.Entry<String, List<Melding>> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }

    private static final Predicate<Map.Entry<String, List<Melding>>> DET_FINNES_EN_ROTMELDING = new Predicate<Map.Entry<String, List<Melding>>>() {
        @Override
        public boolean evaluate(Map.Entry<String, List<Melding>> traad) {
            return on(traad.getValue()).exists(where(ID, equalTo(traad.getKey())));
        }
    };

    public static Transformer<XMLHenvendelse, Melding> tilMelding(final PropertyResolver propertyResolver) {
        return new Transformer<XMLHenvendelse, Melding>() {
            @Override
            public Melding transform(XMLHenvendelse xmlHenvendelse) {
                Melding melding = new Melding();
                melding.id = xmlHenvendelse.getBehandlingsId();
                melding.meldingstype = MELDINGSTYPE_MAP.get(XMLHenvendelseType.fromValue(xmlHenvendelse.getHenvendelseType()));
                melding.opprettetDato = xmlHenvendelse.getOpprettetDato();
                melding.lestDato = xmlHenvendelse.getLestDato();
                melding.fnrBruker = xmlHenvendelse.getFnr();
                melding.traadId = xmlHenvendelse.getBehandlingskjedeId();
                melding.status = STATUS.transform(xmlHenvendelse);
                melding.statusTekst = propertyResolver.getProperty(VisningUtils.lagMeldingStatusTekstKey(melding));
                melding.lestStatus = lagLestStatus(melding);
                melding.statusKlasse = VisningUtils.lagStatusIkonKlasse(melding);
                melding.kontorsperretEnhet = xmlHenvendelse.getKontorsperreEnhet();
                melding.oppgaveId = xmlHenvendelse.getOppgaveIdGsak();
                melding.markertSomFeilsendtAv = xmlHenvendelse.getMarkertSomFeilsendtAv();
                melding.eksternAktor = xmlHenvendelse.getEksternAktor();
                melding.tilknyttetEnhet = xmlHenvendelse.getTilknyttetEnhet();
                melding.brukersEnhet = xmlHenvendelse.getBrukersEnhet();
                melding.erTilknyttetAnsatt = xmlHenvendelse.isErTilknyttetAnsatt();
                melding.gjeldendeTemagruppe = xmlHenvendelse.getGjeldendeTemagruppe() != null ? Temagruppe.valueOf(xmlHenvendelse.getGjeldendeTemagruppe()) : null;

                XMLJournalfortInformasjon journalfortInformasjon = xmlHenvendelse.getJournalfortInformasjon();
                if (journalfortInformasjon != null) {
                    melding.journalfortDato = journalfortInformasjon.getJournalfortDato();
                    melding.journalfortTema = journalfortInformasjon.getJournalfortTema();
                    melding.journalfortSaksId = journalfortInformasjon.getJournalfortSaksId();
                    melding.journalfortAvNavIdent = journalfortInformasjon.getJournalforerNavIdent();
                }

                if (innholdErKassert(xmlHenvendelse)) {
                    settTemagruppe(melding, null, propertyResolver);
                    melding.fritekst = propertyResolver.getProperty("innhold.kassert");
                    melding.kanal = null;
                    melding.navIdent = null;
                    melding.kassert = true;
                    return melding;
                }

                XMLMetadata xmlMetadata = xmlHenvendelse.getMetadataListe().getMetadata().get(0);
                if (xmlMetadata instanceof XMLMeldingFraBruker) {
                    XMLMeldingFraBruker meldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
                    settTemagruppe(melding, meldingFraBruker.getTemagruppe(), propertyResolver);
                    melding.fritekst = meldingFraBruker.getFritekst();
                } else if (xmlMetadata instanceof XMLMeldingTilBruker) {
                    XMLMeldingTilBruker meldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
                    settTemagruppe(melding, meldingTilBruker.getTemagruppe(), propertyResolver);
                    melding.fritekst = meldingTilBruker.getFritekst();
                    melding.kanal = meldingTilBruker.getKanal();
                    melding.navIdent = meldingTilBruker.getNavident();
                } else {
                    throw new RuntimeException("XMLMetadata er av en ukjent type: " + xmlMetadata);
                }

                return melding;
            }
        };
    }

    private static String lagLestStatus(Melding melding) {
        if (VisningUtils.FRA_BRUKER.contains(melding.meldingstype)) {
            return "";
        } else if (melding.status == Status.LEST_AV_BRUKER) {
            return "Lest,";
        } else {
            return "Ulest,";
        }
    }

    private static void settTemagruppe(Melding melding, String temagruppe, PropertyResolver propertyResolver) {
        melding.temagruppe = temagruppe;
        if (temagruppe == null) {
            melding.temagruppeNavn = propertyResolver.getProperty("temagruppe.kassert");
        } else {
            melding.temagruppeNavn = propertyResolver.getProperty(temagruppe);
        }
    }

    public static boolean innholdErKassert(XMLHenvendelse xmlHenvendelse) {
        return xmlHenvendelse.getMetadataListe() == null;
    }

    public static final Transformer<XMLHenvendelse, Status> STATUS = new Transformer<XMLHenvendelse, Status>() {
        @Override
        public Status transform(XMLHenvendelse info) {
            Meldingstype meldingstype = MELDINGSTYPE_MAP.get(XMLHenvendelseType.fromValue(info.getHenvendelseType()));

            if (FRA_BRUKER.contains(meldingstype)) {
                return IKKE_BESVART;
            } else if (FRA_NAV.contains(meldingstype)) {
                DateTime lestDato = info.getLestDato();
                if (lestDato != null) {
                    return LEST_AV_BRUKER;
                } else {
                    return IKKE_LEST_AV_BRUKER;
                }
            } else {
                throw new RuntimeException("Ukjent henvendelsestype: " + meldingstype);
            }
        }
    };

    public static final Map<XMLHenvendelseType, Meldingstype> MELDINGSTYPE_MAP = new HashMap<XMLHenvendelseType, Meldingstype>() {
        {
            put(XMLHenvendelseType.SPORSMAL_SKRIFTLIG, SPORSMAL_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_SKRIFTLIG, SVAR_SKRIFTLIG);
            put(XMLHenvendelseType.SVAR_OPPMOTE, SVAR_OPPMOTE);
            put(XMLHenvendelseType.SVAR_TELEFON, SVAR_TELEFON);
            put(XMLHenvendelseType.REFERAT_OPPMOTE, SAMTALEREFERAT_OPPMOTE);
            put(XMLHenvendelseType.REFERAT_TELEFON, SAMTALEREFERAT_TELEFON);
            put(XMLHenvendelseType.SPORSMAL_MODIA_UTGAAENDE, SPORSMAL_MODIA_UTGAAENDE);
            put(XMLHenvendelseType.SVAR_SBL_INNGAAENDE, SVAR_SBL_INNGAAENDE);
        }
    };
}
