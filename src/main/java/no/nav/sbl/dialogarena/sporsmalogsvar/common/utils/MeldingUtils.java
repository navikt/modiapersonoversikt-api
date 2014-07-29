package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLAktor;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLBehandlingsinformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLJournalfortInformasjon;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v1.XMLSvar;
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
            XMLBehandlingsinformasjon info = (XMLBehandlingsinformasjon) o;

            Meldingstype meldingstype = info.getHenvendelseType().equals(SPORSMAL.name()) ?
                    Meldingstype.SPORSMAL : info.getHenvendelseType().equals(SVAR.name()) ?
                    Meldingstype.SVAR :
                    Meldingstype.SAMTALEREFERAT;

            Melding melding = new Melding(info.getBehandlingsId(), meldingstype, info.getOpprettetDato());
            melding.fnrBruker = info.getAktor().getFodselsnummer();
            melding.traadId = info.getBehandlingsId();
            melding.status = STATUS.transform(info);
            fyllInnJournalforingsInformasjon(info, melding);

            XMLMetadata xmlMetadata = info.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLSporsmal) {
                melding.temagruppe = ((XMLSporsmal) xmlMetadata).getTemagruppe();
                melding.fritekst = ((XMLSporsmal) xmlMetadata).getFritekst();
            } else if (xmlMetadata instanceof XMLSvar) {
                melding.traadId = ((XMLSvar) xmlMetadata).getSporsmalsId();
                melding.temagruppe = ((XMLSvar) xmlMetadata).getTemagruppe();
                melding.fritekst = ((XMLSvar) xmlMetadata).getFritekst();
                melding.kanal = ((XMLSvar) xmlMetadata).getKanal();
                melding.lestDato = ((XMLSvar) xmlMetadata).getLestDato();
                melding.navIdent = getNavIdentFraAktor(info.getAktor());
            } else if (xmlMetadata instanceof XMLReferat) {
                melding.temagruppe = ((XMLReferat) xmlMetadata).getTemagruppe();
                melding.fritekst = ((XMLReferat) xmlMetadata).getFritekst();
                melding.kanal = ((XMLReferat) xmlMetadata).getKanal();
                melding.lestDato = ((XMLReferat) xmlMetadata).getLestDato();
                melding.navIdent = getNavIdentFraAktor(info.getAktor());
            }
            return melding;
        }
    };

    private static void fyllInnJournalforingsInformasjon(XMLBehandlingsinformasjon info, Melding melding) {
        XMLJournalfortInformasjon journalfortInformasjon = info.getJournalfortInformasjon();
        if (info.getJournalfortInformasjon() != null) {
            melding.journalfortDato = journalfortInformasjon.getJournalfortDato();
            melding.journalfortTema = journalfortInformasjon.getJournalfortTema();
            melding.journalfortSaksId = journalfortInformasjon.getJournalfortSaksId();
            melding.journalfortAvNavIdent = journalfortInformasjon.getJournalforerNavIdent();
        }
    }

    private static String getNavIdentFraAktor(XMLAktor aktor) {
        return aktor != null ? aktor.getNavIdent() : null;
    }

    public static final Transformer<XMLBehandlingsinformasjon, Status> STATUS = new Transformer<XMLBehandlingsinformasjon, Status>() {
        @Override
        public Status transform(XMLBehandlingsinformasjon info) {
            String henvendelseType = info.getHenvendelseType();
            if (henvendelseType.equals(SPORSMAL.name())) {
                return IKKE_BESVART;
            } else if (henvendelseType.equals(SVAR.name()) || henvendelseType.equals(REFERAT.name())) {
                XMLMetadata xmlMetadata = info.getMetadataListe().getMetadata().get(0);
                DateTime lestDato = null;
                if (xmlMetadata instanceof XMLSvar) {
                    lestDato = ((XMLSvar) xmlMetadata).getLestDato();
                } else if (xmlMetadata instanceof XMLReferat) {
                    lestDato = ((XMLReferat) xmlMetadata).getLestDato();
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
