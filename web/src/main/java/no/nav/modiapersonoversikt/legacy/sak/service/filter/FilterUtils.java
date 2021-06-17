package no.nav.modiapersonoversikt.legacy.sak.service.filter;

import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import javax.xml.datatype.XMLGregorianCalendar;

import static org.slf4j.LoggerFactory.getLogger;

public class FilterUtils {
    public static final String OPPRETTET = "opprettet";
    public static final String AVBRUTT = "avbrutt";
    public static final String AVSLUTTET = "avsluttet";
    public static final String SEND_SOKNAD_KVITTERINGSTYPE = "ae0002";
    public static final String DOKUMENTINNSENDING_KVITTERINGSTYPE = "ae0001";
    public static final String BEHANDLINGSTATUS_AVSLUTTET = "avsluttet";

    private static final Logger LOGGER = getLogger(FilterUtils.class);

    public static DateTime behandlingsDato(Behandlingskjede wsBehandlingskjede) {
        XMLGregorianCalendar calendar = erAvsluttet(wsBehandlingskjede) ? wsBehandlingskjede.getSlutt() : wsBehandlingskjede.getStart();
        return new DateTime(calendar.toGregorianCalendar().getTime());
    }

    public static boolean erKvitteringstype(String type) {
        return SEND_SOKNAD_KVITTERINGSTYPE.equals(type) || DOKUMENTINNSENDING_KVITTERINGSTYPE.equals(type);
    }

    public static boolean erAvsluttet(Behandlingskjede kjede) {
        boolean erAvsluttet = kjede.getSisteBehandlingsstatus() != null && BEHANDLINGSTATUS_AVSLUTTET.equals(kjede.getSisteBehandlingsstatus().getValue());
        if (erAvsluttet && kjede.getSlutt() == null) {
            LOGGER.warn("Inkonsistent data fra sak og behandling: Behandling rapporteres som avsluttet uten at kjede har slutt-tid satt. " +
                    "Behandlingsid: " + kjede.getSisteBehandlingREF() +
                    "Behandlingskjedeid: " + kjede.getBehandlingskjedeId());
        }
        return erAvsluttet;
    }
}
