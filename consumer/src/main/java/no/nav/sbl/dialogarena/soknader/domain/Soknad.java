package no.nav.sbl.dialogarena.soknader.domain;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.Calendar;

import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.getInstance;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.GAMMEL_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.MOTTATT;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.NYLIG_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.UNDER_BEHANDLING;

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

    public static Soknad transformToSoeknad(Behandlingskjede behandlingskjede) {
        return soeknadTransformer.transform(behandlingskjede);
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

    private static Transformer<Behandlingskjede, Soknad> soeknadTransformer = new Transformer<Behandlingskjede, Soknad>() {

        @Override
        public Soknad transform(Behandlingskjede behandlingskjede) {
            Soknad soknad = new Soknad();
            soknad.innsendtDato = dateTimeTransformer().transform(behandlingskjede.getStart());
            soknad.tittel =  behandlingskjede.getBehandlingskjedetype().getKodeverksRef();
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
        if(behandlingskjede != null){
            if(behandlingskjede.getNormertBehandlingstid() != null){
                if(behandlingskjede.getNormertBehandlingstid().getTid() != null && behandlingskjede.getNormertBehandlingstid().getType() != null){
                    return behandlingskjede.getNormertBehandlingstid().getTid() + " " + behandlingskjede.getNormertBehandlingstid().getType().getValue();
                }
            }
        }
        return "";
    }

    private static boolean isNyligFerdig(Soknad soeknad) {
        Calendar timeFromSoeknad = soeknad.getFerdigDato().toGregorianCalendar();
        timeFromSoeknad.add(DAY_OF_YEAR, AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED);
        return timeFromSoeknad.after(getInstance());
    }
}
