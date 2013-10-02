package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.common.integrasjonsutils.JSON;
import no.nav.sbl.dialogarena.sporsmalogsvar.records.Record;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.Comparator;

import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.Meldingstype.INNGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.Meldingstype.UTGAENDE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.Status.IKKE_BESVART_INNEN_FRIST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.Status.IKKE_LEST_AV_BRUKER;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.Status.LEST_AV_BRUKER;

public class MeldingUtils {

    private static final int BESVARINGSFRIST_TIMER = 48;
    private static final String SPORSMAL = "SPORSMAL";
    private static final String SVAR = "SVAR";

    public static final Transformer<WSHenvendelse, Record<Melding>> TIL_MELDING = new Transformer<WSHenvendelse, Record<Melding>>() {
        @Override
        public Record<Melding> transform(WSHenvendelse wsHenvendelse) {
            return new Record<Melding>()
                    .with(Melding.id, wsHenvendelse.getBehandlingsId())
                    .with(Melding.traadId, wsHenvendelse.getTraad())
                    .with(Melding.avsenderId, wsHenvendelse.getAktor())
                    .with(Melding.tema, wsHenvendelse.getTema())
                    .with(Melding.fritekst, JSON.unmarshal(wsHenvendelse.getBehandlingsresultat()).get("fritekst").toString())
                    .with(Melding.opprettetDato, wsHenvendelse.getOpprettetDato())
                    .with(Melding.lestDato, wsHenvendelse.getLestDato())
                    .with(Melding.status, STATUS.transform(wsHenvendelse))
                    .with(Melding.type, MELDINGSTYPE.transform(wsHenvendelse));
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

    private static final Transformer<WSHenvendelse, Status> STATUS = new Transformer<WSHenvendelse, Status>() {
        @Override
        public Status transform(WSHenvendelse wsHenvendelse) {
            switch (wsHenvendelse.getHenvendelseType()) {
                case SPORSMAL:
                    if (DateTime.now().isAfter(wsHenvendelse.getOpprettetDato().plusHours(BESVARINGSFRIST_TIMER))) {
                        return IKKE_BESVART_INNEN_FRIST;
                    } else {
                        return IKKE_BESVART;
                    }
                case SVAR:
                    if (wsHenvendelse.getLestDato() != null) {
                        return LEST_AV_BRUKER;
                    } else {
                        return IKKE_LEST_AV_BRUKER;
                    }
                default:
                    throw new UkjentHenvendelsestypeException(wsHenvendelse.getHenvendelseType());
            }
        }
    };

    private static final Transformer<WSHenvendelse, Meldingstype> MELDINGSTYPE = new Transformer<WSHenvendelse, Meldingstype>() {
        @Override
        public Meldingstype transform(WSHenvendelse wsHenvendelse) {
             switch (wsHenvendelse.getHenvendelseType()) {
                 case SPORSMAL:
                     return INNGAENDE;
                 case SVAR:
                     return UTGAENDE;
                 default:
                     throw new UkjentHenvendelsestypeException(wsHenvendelse.getHenvendelseType());
             }
        }
    };

    private static class UkjentHenvendelsestypeException extends ApplicationException {

        public UkjentHenvendelsestypeException(String type) {
            super("Ukjent henvendelsestype: " + type);
        }
    }
}
