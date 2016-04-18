package no.nav.sbl.dialogarena.saksoversikt.service.service.filter;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class FilterUtils {
    public static final String OPPRETTET = "opprettet";
    public static final String AVBRUTT = "avbrutt";
    public static final String AVSLUTTET = "avsluttet";
    public static final String SEND_SOKNAD_KVITTERINGSTYPE = "ae0002";
    public static final String DOKUMENTINNSENDING_KVITTERINGSTYPE = "ae0001";
    public static final String BEHANDLINGSTATUS_AVSLUTTET = "avsluttet";

    private static final Logger LOGGER = getLogger(FilterUtils.class);

    public static DateTime behandlingsDato(WSBehandlingskjede wsBehandlingskjede) {
        return erAvsluttet(wsBehandlingskjede) ? wsBehandlingskjede.getSlutt() : wsBehandlingskjede.getStart();
    }

    public static boolean erKvitteringstype(String type) {
        return SEND_SOKNAD_KVITTERINGSTYPE.equals(type) || DOKUMENTINNSENDING_KVITTERINGSTYPE.equals(type);
    }

    public static boolean erAvsluttet(WSBehandlingskjede kjede) {
        boolean erAvsluttet = kjede.getSisteBehandlingsstatus() != null && BEHANDLINGSTATUS_AVSLUTTET.equals(kjede.getSisteBehandlingsstatus().getValue());
        if (erAvsluttet && kjede.getSlutt() == null) {
            LOGGER.warn("Inkonsistent data fra sak og behandling: Behandling rapporteres som avsluttet uten at kjede har slutt-tid satt. " +
                    "Behandlingsid: " + kjede.getSisteBehandlingREF() +
                    "Behandlingskjedeid: " + kjede.getBehandlingskjedeId());
        }
        return erAvsluttet;
    }
}
