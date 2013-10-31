package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import java.util.Comparator;
import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.records.Record;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status.IKKE_BESVART_INNEN_FRIST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status.LEST_AV_BRUKER;

public class MeldingUtils {

    private static final int BESVARINGSFRIST_TIMER = 48;

    public static final Transformer<WSMelding, Record<Melding>> TIL_MELDING = new Transformer<WSMelding, Record<Melding>>() {
        @Override
        public Record<Melding> transform(WSMelding wsMelding) {
            return new Record<Melding>()
                    .with(Melding.id, wsMelding.getBehandlingsId())
                    .with(Melding.traadId, wsMelding.getTraad())
                    .with(Melding.tema, wsMelding.getTemastruktur())
                    .with(Melding.fritekst, wsMelding.getTekst())
                    .with(Melding.opprettetDato, wsMelding.getOpprettetDato())
                    .with(Melding.lestDato, wsMelding.getLestDato())
                    .with(Melding.status, STATUS.transform(wsMelding))
                    .with(Melding.type, MELDINGSTYPE.transform(wsMelding));
        }
    };

    public static final Comparator<Record<Melding>> NYESTE_FORST = new Comparator<Record<Melding>>() {
        @Override
        public int compare(Record<Melding> o1, Record<Melding> o2) {
            return o2.get(Melding.opprettetDato).compareTo(o1.get(Melding.opprettetDato));
        }
    };

    public static final Comparator<Record<Melding>> ELDSTE_FORST = new Comparator<Record<Melding>>() {
        @Override
        public int compare(Record<Melding> o1, Record<Melding> o2) {
            return o1.get(Melding.opprettetDato).compareTo(o2.get(Melding.opprettetDato));
        }
    };

    private static final Transformer<WSMelding, Status> STATUS = new Transformer<WSMelding, Status>() {
        @Override
        public Status transform(WSMelding wsMelding) {
            switch (wsMelding.getMeldingsType()) {
                case INNGAENDE:
                    if (DateTime.now().isAfter(wsMelding.getOpprettetDato().plusHours(BESVARINGSFRIST_TIMER))) {
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

    private static final Transformer<WSMelding, Meldingstype> MELDINGSTYPE = new Transformer<WSMelding, Meldingstype>() {
        @Override
        public Meldingstype transform(WSMelding wsMelding) {
            return Meldingstype.valueOf(wsMelding.getMeldingsType().toString());
        }
    };

    private static class UkjentMeldingstypeException extends ApplicationException {

        public UkjentMeldingstypeException(WSMeldingstype type) {
            super("Ukjent henvendelsestype: " + type);
        }
    }
}
