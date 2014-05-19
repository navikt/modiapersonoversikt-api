package no.nav.sbl.dialogarena.sporsmalogsvar.common.utils;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.MeldingRecord;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMeldingstype;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.Comparator;

import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status.IKKE_BESVART_INNEN_FRIST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Status.LEST_AV_BRUKER;

public class MeldingUtils {

    private static final int BESVARINGSFRIST_TIMER = 48;

    public static final Transformer<WSMelding, Record<MeldingRecord>> TIL_MELDING = new Transformer<WSMelding, Record<MeldingRecord>>() {
        @Override
        public Record<MeldingRecord> transform(WSMelding wsMelding) {
            return new Record<MeldingRecord>()
                    .with(MeldingRecord.id, wsMelding.getBehandlingsId())
                    .with(MeldingRecord.traadId, wsMelding.getTraad())
                    .with(MeldingRecord.tema, wsMelding.getTemastruktur())
                    .with(MeldingRecord.fritekst, wsMelding.getTekst())
                    .with(MeldingRecord.opprettetDato, wsMelding.getOpprettetDato())
                    .with(MeldingRecord.lestDato, wsMelding.getLestDato())
                    .with(MeldingRecord.status, STATUS.transform(wsMelding))
                    .with(MeldingRecord.type, MELDINGSTYPE.transform(wsMelding))
                    .with(MeldingRecord.journalfortDato, wsMelding.getJournalfortDato())
                    .with(MeldingRecord.journalfortSaksid, wsMelding.getJournalfortSaksId())
                    .with(MeldingRecord.journalfortTema, wsMelding.getJournalfortTema());
        }
    };

    public static final Comparator<Record<MeldingRecord>> NYESTE_FORST = new Comparator<Record<MeldingRecord>>() {
        @Override
        public int compare(Record<MeldingRecord> o1, Record<MeldingRecord> o2) {
            return o2.get(MeldingRecord.opprettetDato).compareTo(o1.get(MeldingRecord.opprettetDato));
        }
    };

    public static final Comparator<Record<MeldingRecord>> ELDSTE_FORST = new Comparator<Record<MeldingRecord>>() {
        @Override
        public int compare(Record<MeldingRecord> o1, Record<MeldingRecord> o2) {
            return o1.get(MeldingRecord.opprettetDato).compareTo(o2.get(MeldingRecord.opprettetDato));
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
