package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLBehandlingsinformasjonV2;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLMetadata;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLReferat;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSporsmal;
import no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLSvar;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.REFERAT;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.SPORSMAL;
import static no.nav.melding.domene.brukerdialog.behandlingsinformasjon.v2.XMLHenvendelseType.SVAR;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding.TRAAD_ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.IKKE_BESVART_INNEN_FRIST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.LEST_AV_BRUKER;
import static org.joda.time.DateTime.now;

public class MeldingUtils {

    public static final int BESVARINGSFRIST_TIMER = 48;

    /**
     * Deler inn en liste henvendelser i tråder
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
            XMLBehandlingsinformasjonV2 info = (XMLBehandlingsinformasjonV2) o;

            Meldingstype meldingstype = info.getHenvendelseType().equals(SPORSMAL.name()) ? Meldingstype.INNGAENDE : Meldingstype.UTGAENDE;

            Melding melding = new Melding(info.getBehandlingsId(), meldingstype, info.getOpprettetDato());
            melding.traadId = info.getBehandlingsId();
            melding.status = STATUS.transform(info);

            XMLMetadata xmlMetadata = info.getMetadataListe().getMetadata().get(0);
            if (xmlMetadata instanceof XMLSporsmal) {
                melding.tema = ((XMLSporsmal) xmlMetadata).getTemagruppe();
                melding.fritekst = ((XMLSporsmal) xmlMetadata).getFritekst();
            } else if (xmlMetadata instanceof XMLSvar) {
                melding.traadId = ((XMLSvar) xmlMetadata).getSporsmalsId();
                melding.tema = ((XMLSvar) xmlMetadata).getTemagruppe();
                melding.fritekst = ((XMLSvar) xmlMetadata).getFritekst();
                melding.lestDato = ((XMLSvar) xmlMetadata).getLestDato();
            } else if (xmlMetadata instanceof XMLReferat) {
                melding.tema = ((XMLReferat) xmlMetadata).getTemagruppe();
                melding.fritekst = ((XMLReferat) xmlMetadata).getFritekst();
                melding.kanal = ((XMLReferat) xmlMetadata).getKanal();
                melding.lestDato = ((XMLReferat) xmlMetadata).getLestDato();
            }


            return melding;
        }
    };

    public static final Transformer<XMLBehandlingsinformasjonV2, Status> STATUS = new Transformer<XMLBehandlingsinformasjonV2, Status>() {
        @Override
        public Status transform(XMLBehandlingsinformasjonV2 info) {
            String henvendelseType = info.getHenvendelseType();
            if (henvendelseType.equals(SPORSMAL.name())) {
                if (now().isAfter(info.getOpprettetDato().plusHours(BESVARINGSFRIST_TIMER))) {
                    return IKKE_BESVART_INNEN_FRIST;
                } else {
                    return IKKE_BESVART;
                }
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
