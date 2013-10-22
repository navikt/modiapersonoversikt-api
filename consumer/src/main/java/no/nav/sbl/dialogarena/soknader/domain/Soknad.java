package no.nav.sbl.dialogarena.soknader.domain;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

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

    private DateTime innsendtDato;
    private String tittelKodeverk;
    private SoknadStatus soknadStatus;
    private DateTime underBehandlingStartDato;
    private DateTime ferdigDato;
    private String normertBehandlingsTid;

    public static Soknad transformToSoknad(WSBehandlingskjede behandlingskjede) {
        return BEHANDLINGSKJEDE_TO_SOKNAD_TRANSFORMER.transform(behandlingskjede);
    }

    public String getTittelKodeverk() {
        return tittelKodeverk;
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

    public static final Transformer<WSBehandlingskjede, Soknad> BEHANDLINGSKJEDE_TO_SOKNAD_TRANSFORMER = new Transformer<WSBehandlingskjede, Soknad>() {
        @Override
        public Soknad transform(WSBehandlingskjede behandlingskjede) {
            Soknad soknad = new Soknad();
            soknad.innsendtDato = behandlingskjede.getStart();
            soknad.tittelKodeverk = behandlingskjede.getBehandlingskjedetype().getKodeverksRef();
            soknad.underBehandlingStartDato = optional(behandlingskjede.getStartNAVtid()).getOrElse(null);
            soknad.ferdigDato = evaluateFerdigDato(behandlingskjede);
            soknad.normertBehandlingsTid = getNormertTidString(behandlingskjede);
            soknad.soknadStatus = evaluateStatus(soknad);
            return soknad;
        }
    };

    private static SoknadStatus evaluateStatus(Soknad soknad) {
        SoknadStatus status = MOTTATT;
        if (soeknadHasFerdigDato(soknad)) {
            return getFerdigSoknadStatus(soknad);
        }
        if (soeknadHasUnderBehandlingDato(soknad)) {
            status = UNDER_BEHANDLING;
        }
        return status;
    }

    private static SoknadStatus getFerdigSoknadStatus(Soknad soknad) {
        SoknadStatus status = GAMMEL_FERDIG;
        if (isNyligFerdig(soknad)) {
            status = NYLIG_FERDIG;
        }
        return status;
    }

    private static boolean soeknadHasFerdigDato(Soknad soknad) {
        return soknad.getFerdigDato() != null;
    }

    private static boolean soeknadHasUnderBehandlingDato(Soknad soknad) {
        return soknad.getUnderBehandlingStartDato() != null;
    }

    private static DateTime evaluateFerdigDato(WSBehandlingskjede behandlingskjede) {
        return optional(behandlingskjede.getSluttNAVtid())
                   .getOrElse(optional(behandlingskjede.getSlutt())
                   .getOrElse(null));
    }

    private static String getNormertTidString(WSBehandlingskjede behandlingskjede) {
        if (behandlingsTidInfoExists(behandlingskjede)) {
            return behandlingskjede.getNormertBehandlingstid().getTid() + " " + behandlingskjede.getNormertBehandlingstid().getType().getValue();
        }
        return "";
    }

    private static boolean behandlingsTidInfoExists(WSBehandlingskjede behandlingskjede) {
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
