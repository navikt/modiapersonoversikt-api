package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

    public static final Transformer<WSMelding, Melding> TIL_MELDING = new Transformer<WSMelding, Melding>() {
        @Override
        public Melding transform(WSMelding wsMelding) {
            Meldingstype meldingstype = wsMelding.getMeldingsType() == WSMeldingstype.INNGAENDE ? Meldingstype.INNGAENDE : Meldingstype.UTGAENDE;
            Melding melding = new Melding(wsMelding.getBehandlingsId(), wsMelding.getTraad(), meldingstype, wsMelding.getOpprettetDato(), wsMelding.getTekst());
            melding.tema = wsMelding.getTemastruktur();
            melding.lestDato = wsMelding.getLestDato();
            melding.status = STATUS.transform(wsMelding);
            return melding;
        }
    };

    public static final Transformer<WSMelding, Status> STATUS = new Transformer<WSMelding, Status>() {
        @Override
        public Status transform(WSMelding wsMelding) {
            switch (wsMelding.getMeldingsType()) {
                case INNGAENDE:
                    if (now().isAfter(wsMelding.getOpprettetDato().plusHours(BESVARINGSFRIST_TIMER))) {
                        return IKKE_BESVART_INNEN_FRIST;
                    } else {
                        return IKKE_BESVART;
                    }
                case UTGAENDE:
                    if (wsMelding.getLestDato() != null) {
                        return LEST_AV_BRUKER;
                    } else {
                        return IKKE_LEST_AV_BRUKER;
                    }
                default:
                    throw new UkjentMeldingstypeException(wsMelding.getMeldingsType());
            }
        }
    };

    private static class UkjentMeldingstypeException extends ApplicationException {

        public UkjentMeldingstypeException(WSMeldingstype type) {
            super("Ukjent henvendelsestype: " + type);
        }
    }
}
