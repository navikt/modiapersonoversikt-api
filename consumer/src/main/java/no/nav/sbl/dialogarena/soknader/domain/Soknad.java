package no.nav.sbl.dialogarena.soknader.domain;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;

import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.GAMMEL_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.MOTTATT;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.NYLIG_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.UNDER_BEHANDLING;
import static org.joda.time.DateTime.now;

public class Soknad implements Serializable {

    public static final int AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED = 28;

    public enum SoknadStatus {MOTTATT, UNDER_BEHANDLING, NYLIG_FERDIG, GAMMEL_FERDIG}

    public static final Transformer<Soknad, SoknadStatus> SOKNAD_STATUS_TRANSFORMER = new Transformer<Soknad, SoknadStatus>() {
        @Override
        public SoknadStatus transform(Soknad soknad) {
            return soknad.getSoknadStatus();
        }
    };

    private DateTime innsendtDato;
    private String tittel;
    private SoknadStatus soknadStatus;
    private DateTime underBehandlingStartDato;
    private DateTime ferdigDato;
    private String normertBehandlingsTid;

    public static Soknad transformToSoknad(Behandlingskjede behandlingskjede) {
        return BEHANDLINGSKJEDE_TO_SOKNAD_TRANSFORMER.transform(behandlingskjede);
    }

    public String getTittel() {
        return tittel;
    }

    public SoknadStatus getSoknadStatus() {
        return soknadStatus;
    }

    public String getNormertBehandlingsTid() {
        return normertBehandlingsTid;
    }

    public DateTime getUnderBehandlingStartDato() {
        return underBehandlingStartDato;
    }

    public DateTime getFerdigDato() {
        return ferdigDato;
    }

    public DateTime getInnsendtDato() {
        return innsendtDato;
    }

    public static final Transformer<Behandlingskjede, Soknad> BEHANDLINGSKJEDE_TO_SOKNAD_TRANSFORMER = new Transformer<Behandlingskjede, Soknad>() {

        @Override
        public Soknad transform(Behandlingskjede behandlingskjede) {
            Soknad soknad = new Soknad();
            soknad.innsendtDato = dateTimeTransformer().transform(behandlingskjede.getStart());
            soknad.tittel = behandlingskjede.getBehandlingskjedetype().getKodeRef();
            soknad.underBehandlingStartDato = optional(behandlingskjede.getStartNAVtid()).map(dateTimeTransformer()).getOrElse(null);
            soknad.ferdigDato = evaluateFerdigDato(behandlingskjede);
            soknad.normertBehandlingsTid = getNormertTidString(behandlingskjede);
            evaluateAndSetStatus(soknad);
            return soknad;
        }

    };

    private static void evaluateAndSetStatus(Soknad soknad) {
        if (soeknadHasFerdigDato(soknad)) {
            evaluateAndSetFerdigTypeStatus(soknad);
        } else if (soeknadHasUnderBehandlingDato(soknad)) {
            soknad.soknadStatus = UNDER_BEHANDLING;
        } else {
            soknad.soknadStatus = MOTTATT;
        }
    }

    private static void evaluateAndSetFerdigTypeStatus(Soknad soknad) {
        if (isNyligFerdig(soknad)) {
            soknad.soknadStatus = NYLIG_FERDIG;
        } else {
            soknad.soknadStatus = GAMMEL_FERDIG;
        }
    }

    private static boolean soeknadHasFerdigDato(Soknad soknad) {
        return soknad.getFerdigDato() != null;
    }

    private static boolean soeknadHasUnderBehandlingDato(Soknad soknad) {
        return soknad.getUnderBehandlingStartDato() != null;
    }

    private static DateTime evaluateFerdigDato(Behandlingskjede behandlingskjede) {
        return optional(behandlingskjede.getSluttNAVtid())
                .map(dateTimeTransformer()).getOrElse(
                        optional(behandlingskjede.getSlutt())
                                .map(dateTimeTransformer()).getOrElse(null));
    }

    private static Transformer<XMLGregorianCalendar, DateTime> dateTimeTransformer() {
        return new Transformer<XMLGregorianCalendar, DateTime>() {
            @Override
            public DateTime transform(XMLGregorianCalendar xmlGregorianCalendar) {
                return new DateTime(xmlGregorianCalendar.toGregorianCalendar().getTime());
            }
        };
    }

    private static String getNormertTidString(Behandlingskjede behandlingskjede) {
        if (behandlingsTidInfoExists(behandlingskjede)) {
            return behandlingskjede.getNormertBehandlingstid().getTid() + " " + behandlingskjede.getNormertBehandlingstid().getType().getValue();
        }
        return "";
    }

    private static boolean behandlingsTidInfoExists(Behandlingskjede behandlingskjede) {
        return behandlingskjede != null &&
                behandlingskjede.getNormertBehandlingstid() != null &&
                behandlingskjede.getNormertBehandlingstid().getTid() != null &&
                behandlingskjede.getNormertBehandlingstid().getType() != null;
    }

    private static boolean isNyligFerdig(Soknad soknad) {
        DateTime ferdigDato = soknad.getFerdigDato();
        DateTime expiredDate = ferdigDato.plusDays(AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED);
        return now().isBefore(expiredDate);
    }
}
