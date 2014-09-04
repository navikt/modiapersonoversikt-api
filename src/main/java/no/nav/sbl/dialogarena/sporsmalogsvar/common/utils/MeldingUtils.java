package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelse;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingFraBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMeldingTilBruker;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLHenvendelseType.SVAR;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding.TRAAD_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Status.LEST_AV_BRUKER;

public class MeldingUtils {

    /**
     * Deler inn en liste henvendelser i tråder
     *
     * @param meldinger liste med henvendelser
     * @return map med key: trådid og value: alle henvendelser som tilhører tråden
     */
    public static Map<String, List<Melding>> skillUtTraader(List<Melding> meldinger) {
        Map<String, List<Melding>> traaderMap = new HashMap<>();
        for (String traadId : on(meldinger).map(TRAAD_ID).collectIn(new HashSet<String>())) {
            traaderMap.put(traadId, on(meldinger).filter(where(TRAAD_ID, equalTo(traadId))).collect());
        }
        return traaderMap;
    }

    public static final Transformer<Object, Melding> TIL_MELDING = new Transformer<Object, Melding>() {
        @Override
        public Melding transform(Object o) {
            XMLHenvendelse xmlHenvendelse = (XMLHenvendelse) o;

            Meldingstype meldingstype = xmlHenvendelse.getHenvendelseType().equals(SPORSMAL.name()) ?
                    Meldingstype.SPORSMAL : xmlHenvendelse.getHenvendelseType().equals(SVAR.name()) ?
                    Meldingstype.SVAR :
                    Meldingstype.SAMTALEREFERAT;

            Melding melding = new Melding(xmlHenvendelse.getBehandlingsId(), meldingstype, xmlHenvendelse.getOpprettetDato());
            melding.fnrBruker = xmlHenvendelse.getFnr();
            melding.traadId = xmlHenvendelse.getBehandlingsId();
            melding.status = STATUS.transform(xmlHenvendelse);
            melding.kontorsperretEnhet = xmlHenvendelse.getKontorsperreEnhet();
            melding.markertSomFeilsendtAv = xmlHenvendelse.getMarkertSomFeilsendtAv();
            fyllInnJournalforingsInformasjon(xmlHenvendelse, melding);

            XMLMetadata xmlMetadata = xmlHenvendelse.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLMeldingFraBruker) {
                melding.temagruppe = ((XMLMeldingFraBruker) xmlMetadata).getTemagruppe();
                melding.fritekst = ((XMLMeldingFraBruker) xmlMetadata).getFritekst();
            } else if (xmlMetadata instanceof XMLMeldingTilBruker) {
                XMLMeldingTilBruker svarEllerReferat = (XMLMeldingTilBruker) xmlMetadata;
                if(svarEllerReferat.getSporsmalsId() != null){
                    melding.traadId = svarEllerReferat.getSporsmalsId();
                }
                melding.temagruppe = svarEllerReferat.getTemagruppe();
                melding.fritekst = svarEllerReferat.getFritekst();
                melding.kanal = svarEllerReferat.getKanal();
                melding.lestDato = svarEllerReferat.getLestDato();
                melding.navIdent = svarEllerReferat.getNavident();
            }
            return melding;
        }
    };

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
            String henvendelseType = info.getHenvendelseType();
            if (henvendelseType.equals(SPORSMAL.name())) {
                return IKKE_BESVART;
            } else if (henvendelseType.equals(SVAR.name()) || henvendelseType.equals(REFERAT.name())) {
                XMLMetadata xmlMetadata = info.getMetadataListe().getMetadata().get(0);
                DateTime lestDato = null;
                if (xmlMetadata instanceof XMLMeldingTilBruker) {
                    lestDato = ((XMLMeldingTilBruker) xmlMetadata).getLestDato();
                }
                if (lestDato != null) {
                    return LEST_AV_BRUKER;
                } else {
                    return IKKE_LEST_AV_BRUKER;
                }
            } else {
                throw new ApplicationException("Ukjent henvendelsestype: " + henvendelseType);
            }
        }
    };

}
