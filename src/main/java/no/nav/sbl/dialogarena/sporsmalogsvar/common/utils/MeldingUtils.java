package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.*;
import no.nav.modig.core.exception.ApplicationException;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.*;

import static java.util.Arrays.asList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding.ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding.TRAAD_ID;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Meldingstype.*;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Status.IKKE_BESVART;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Status.LEST_AV_BRUKER;

public class MeldingUtils {

    /**
     * Deler inn en liste henvendelser i tråder
     *
     * @param meldinger liste med henvendelser
     * @return map med key: trådid og value: alle henvendelser som tilhører tråden
     */
    public static Map<String, List<Melding>> skillUtTraader(List<Melding> meldinger) {

        Map<String, List<Melding>> ufiltrertTraaderMap = on(meldinger).reduce(indexBy(TRAAD_ID), new HashMap<String, List<Melding>>());

        List<Map.Entry<String, List<Melding>>> filtrertEntryList = on(ufiltrertTraaderMap).filter(DET_FINNES_EN_ROTMELDING).collect();

        return lagMap(filtrertEntryList);
    }

    private static final Predicate<Map.Entry<String, List<Melding>>> DET_FINNES_EN_ROTMELDING = new Predicate<Map.Entry<String, List<Melding>>>() {
        @Override
        public boolean evaluate(Map.Entry<String, List<Melding>> traad) {
            return on(traad.getValue()).exists(where(ID, equalTo(traad.getKey())));
        }
    };

    public static final Transformer<Object, Melding> TIL_MELDING = new Transformer<Object, Melding>() {
        @Override
        public Melding transform(Object o) {
            XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) o;

            Meldingstype meldingstype = MELDINGSTYPE_MAP.get(XMLHenvendelseType.fromValue(xmlHenvendelse.getHenvendelseType()));

            Melding melding = new Melding(xmlHenvendelse.getBehandlingsId(), meldingstype, xmlHenvendelse.getOpprettetDato());
            melding.lestDato = xmlHenvendelse.getLestDato();
            melding.fnrBruker = xmlHenvendelse.getFnr();
            melding.traadId = xmlHenvendelse.getBehandlingskjedeId();
            melding.status = STATUS.transform(xmlHenvendelse);
            melding.kontorsperretEnhet = xmlHenvendelse.getKontorsperreEnhet();
            melding.markertSomFeilsendtAv = xmlHenvendelse.getMarkertSomFeilsendtAv();
            melding.eksternAktor = xmlHenvendelse.getEksternAktor();
            fyllInnJournalforingsInformasjon(xmlHenvendelse, melding);

            if (innholdErKassert(xmlHenvendelse)) {
                melding.temagruppe = null;
                melding.fritekst = null;
                melding.kanal = null;
                melding.navIdent = null;
                return melding;
            }

            XMLMetadata xmlMetadata = xmlHenvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLMeldingFraBruker) {
                XMLMeldingFraBruker meldingFraBruker = (XMLMeldingFraBruker) xmlMetadata;
                melding.temagruppe = meldingFraBruker.getTemagruppe();
                melding.fritekst = meldingFraBruker.getFritekst();
            } else if (xmlMetadata instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker meldingTilBruker = (XMLMeldingTilBruker) xmlMetadata;
                melding.temagruppe = meldingTilBruker.getTemagruppe();
                melding.fritekst = meldingTilBruker.getFritekst();
                melding.kanal = meldingTilBruker.getKanal();
                melding.navIdent = meldingTilBruker.getNavident();
            }
            return melding;
        }
    };

    private static boolean innholdErKassert(XMLHenvendelse xmlHenvendelse) {
        return xmlHenvendelse.getMetadataListe() == null;
    }

    private static void fyllInnJournalforingsInformasjon(XMLHenvendelse info, Melding melding) {
        XMLJournalfortInformasjon journalfortInformasjon = info.getJournalfortInformasjon();
        if (info.getJournalfortInformasjon() != null) {
            melding.journalfortDato = journalfortInformasjon.getJournalfortDato();
            melding.journalfortTema = journalfortInformasjon.getJournalfortTema();
            melding.journalfortSaksId = journalfortInformasjon.getJournalfortSaksId();
            melding.journalfortAvNavIdent = journalfortInformasjon.getJournalforerNavIdent();
        }
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
                throw new ApplicationException("Ukjent henvendelsestype: " + meldingstype);
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

    public static final List<Meldingstype> FRA_BRUKER = asList(SPORSMAL_SKRIFTLIG, SVAR_SBL_INNGAAENDE);
    public static final List<Meldingstype> FRA_NAV = asList(SVAR_SKRIFTLIG, SVAR_OPPMOTE, SVAR_TELEFON, SAMTALEREFERAT_OPPMOTE, SAMTALEREFERAT_TELEFON, SPORSMAL_MODIA_UTGAAENDE);
    public static final List<Meldingstype> SPORSMAL = asList(SPORSMAL_SKRIFTLIG, SPORSMAL_MODIA_UTGAAENDE);

    private static Map<String, List<Melding>> lagMap(List<Map.Entry<String, List<Melding>>> entries) {
        HashMap<String, List<Melding>> map = new HashMap<>();
        for (Map.Entry<String, List<Melding>> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
