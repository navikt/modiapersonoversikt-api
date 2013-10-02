package no.nav.sbl.dialogarena.soknader.domain;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.xml.datatype.XMLGregorianCalendar;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.getInstance;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.GAMMEL_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.NYLIG_FERDIG;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.UKJENT;
import static no.nav.sbl.dialogarena.soknader.domain.Soknad.SoknadStatus.UNDER_BEHANDLING;

public class Soknad implements Serializable {

    public static final int AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED = 28;

    public enum SoknadStatus {UKJENT, MOTTATT, UNDER_BEHANDLING, NYLIG_FERDIG, GAMMEL_FERDIG}

    private DateTime mottattDato;
    private DateTime underBehandlingDato;
    private DateTime ferdigDato;
    private String normertBehandlingsTid;
    private String tittel;
    private SoknadStatus soknadStatus;
    private String behandlingskjedeId;

    private Soknad() { }

    public static Soknad transformToSoeknad(Behandlingskjede behandlingskjede) {
        return soeknadTransformer.transform(behandlingskjede);
    }

    public static final Transformer<Soknad, SoknadStatus> SOEKNADSTATUS_TRANSFORMER = new Transformer<Soknad, SoknadStatus>() {

        @Override
        public SoknadStatus transform(Soknad soeknad) {
            return soeknad.getSoknadStatus();
        }
    };

    public static final Transformer<List<Behandlingskjede>, List<Soknad>> SOEKNAD_LIST_TRANSFORMER = new Transformer<List<Behandlingskjede>, List<Soknad>>() {

        @Override
        public List<Soknad> transform(List<Behandlingskjede> behandlingskjedeList) {
            List<Soknad> soeknadList = new ArrayList<>();
            for (Behandlingskjede behandlingskjede : behandlingskjedeList) {
                soeknadList.add(soeknadTransformer.transform(behandlingskjede));
            }
            return soeknadList;
        }
    };

    public String getTittel() {
        return tittel;
    }

    public SoknadStatus getSoknadStatus() {
        return soknadStatus;
    }

    public String getNormertBehandlingsTid() {
        return normertBehandlingsTid;
    }

    public DateTime getUnderBehandlingDato() {
        return underBehandlingDato;
    }

    public DateTime getFerdigDato() {
        return ferdigDato;
    }

    public String getBehandlingskjedeId() {
        return behandlingskjedeId;
    }

    public DateTime getMottattDato() {
        return mottattDato;
    }

    private static Transformer<Behandlingskjede, Soknad> soeknadTransformer = new Transformer<Behandlingskjede, Soknad>() {

        @Override
        public Soknad transform(Behandlingskjede behandlingskjede) {
            Soknad soknad = new Soknad();
            //TODO: Gjør oppslag for å finne tema!
            soknad.tittel = behandlingskjede.getBehandlingskjedetype().getKodeverksRef();
            soknad.normertBehandlingsTid = getNormertTidString(behandlingskjede);
            soknad.mottattDato = dateTimeTransformer().transform(behandlingskjede.getStart());
            soknad.underBehandlingDato = optional(behandlingskjede.getStartNAVtid()).map(dateTimeTransformer()).getOrElse(null);
            soknad.ferdigDato = evaluateFerdigDato(behandlingskjede);
            soknad.behandlingskjedeId = behandlingskjede.getBehandlingskjedeId();
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
            soknad.soknadStatus = UKJENT;
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
        return soknad.getUnderBehandlingDato() != null;
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
        return behandlingskjede.getNormertBehandlingstid().getTid() + " " + behandlingskjede.getNormertBehandlingstid().getType().getValue();
    }

    private static boolean isNyligFerdig(Soknad soeknad) {
        Calendar timeFromSoeknad = soeknad.getFerdigDato().toGregorianCalendar();
        timeFromSoeknad.add(DAY_OF_YEAR, AMOUNT_OF_DAYS_BEFORE_SOEKNAD_IS_OUTDATED);
        return timeFromSoeknad.after(getInstance());
    }
}
