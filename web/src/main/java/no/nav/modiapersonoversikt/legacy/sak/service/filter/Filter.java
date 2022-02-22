package no.nav.modiapersonoversikt.legacy.sak.service.filter;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.Behandling;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.BehandlingsStatus;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;

import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Filter {
    public final static String ULOVLIG_PREFIX = "17"; //ukjent årsak til dette ulovlige prefixet

    private static final List<String> ulovligeSakstema = Arrays.asList("FEI,SAK,SAP,OPP,YRA,GEN,AAR,KLA,HEL".split("\\s*,\\s*"));
    private static final List<String> lovligeBehandlingstyper = Arrays.asList("ae0047,ae0034,ae0014,ae0020,ae0019,ae0011,ae0045".split("\\s*,\\s*"));

    public synchronized List<Sak> filtrerSaker(List<Sak> saker) {
        return saker.stream()
                .filter(HAR_LOVLIG_SAKSTEMA)
                .filter(HAR_BEHANDLINGER)
                .filter(HAR_MINST_EN_LOVLIG_BEHANDLING).collect(toList());
    }

    public synchronized List<Behandling> filtrerBehandlinger(List<Behandling> behandlinger) {
        Stream<Behandling> lovligeBehandlinger = behandlinger.stream().filter(HAR_LOVLIG_BEHANDLINGSTYPE);

        return lovligeBehandlinger
                .filter(HAR_LOVLIG_BEHANDLINGSSTATUS)
                .filter(HAR_LOVLIG_PREFIX)
                .filter(SISTEBEHANDLING_ER_IKKE_ELDRE_ENN_1_MAANED)
                .sorted((o1, o2) -> o2.getBehandlingDato().compareTo(o1.getBehandlingDato()))
                .collect(toList());
    }

    private static final Predicate<Behandling> HAR_LOVLIG_BEHANDLINGSSTATUS = behandling -> !behandling.getBehandlingsStatus().equals(BehandlingsStatus.AVBRUTT); //alle statuser utenom avbrutt er tillatt

    private static final Predicate<Behandlingskjede> HAR_LOVLIG_PREFIX_PAA_BEHANDLING = kjede -> !kjede.getSisteBehandlingREF().startsWith(ULOVLIG_PREFIX); //Prefix uviss grunn til at 17 er forbudt

    private static final Predicate<Behandling> SISTEBEHANDLING_ER_IKKE_ELDRE_ENN_1_MAANED = kjede -> lovligMenUtgaattEllerUnderBehandling(kjede); // filterning av behandlingskjeder mappet til behandling som er ferdig/avsluttet og er over 1 måned sidan den blie avslutta

    private static boolean lovligMenUtgaattEllerUnderBehandling(Behandling kjede) {
        if (kjede.behandlingsStatus.equals(BehandlingsStatus.FERDIG_BEHANDLET)) {
            org.joda.time.DateTime behandlingDato = kjede.behandlingDato;
            DateTime nowMinus1Mnd = DateTime.now().minusMonths(1);
            return (behandlingDato.getMillis() > nowMinus1Mnd.getMillis());
        } else return true;

    }

    private static final Predicate<Behandlingskjede> HAR_LOVLIG_STATUS_PAA_BEHANDLING = kjede ->
            kjede.getSisteBehandlingsstatus().getValue() != null &&
                    ((kjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.OPPRETTET) && !FilterUtils.erKvitteringstype(kjede.getSisteBehandlingstype().getValue()))
                            || kjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.AVSLUTTET));

    private static final Predicate<Behandlingskjede> HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING = kjede -> {
        String type = kjede.getSisteBehandlingstype().getValue();

        return erFerdigsUnder1MndSidanEllerInnsendtSoknad(type, kjede) || lovligMenUtgaatStatusEllerUnderBehandling(type, kjede);

    };

    private static boolean erFerdigsUnder1MndSidanEllerInnsendtSoknad(String type, Behandlingskjede kjede) {
        if (FilterUtils.erKvitteringstype(type)) {
            return (FilterUtils.erAvsluttet(kjede) && under1MndSidenFerdigstillelse(kjede));
        }
        if (FilterUtils.erAvsluttet(kjede)) {
            return under1MndSidenFerdigstillelse(kjede) && lovligeBehandlingstyper.contains(type);
        }
        return false;
    }

    private static boolean lovligMenUtgaatStatusEllerUnderBehandling(String type, Behandlingskjede kjede) {

        if (FilterUtils.erAvsluttet(kjede) && !under1MndSidenFerdigstillelse(kjede)) {
            return false;
        }
        if (lovligeBehandlingstyper.contains(type) && !FilterUtils.erAvsluttet(kjede)) {
            return true;
        }
        return false;
    }

    private static boolean under1MndSidenFerdigstillelse(Behandlingskjede kjede) {
        if (kjede.getSisteBehandlingsoppdatering() != (null)) {

            try {
                XMLGregorianCalendar sisteDato = kjede.getSisteBehandlingsoppdatering();
                LocalDate now = LocalDate.now().minus(1, ChronoUnit.MONTHS);
                XMLGregorianCalendar xgcMonthAgo = DatatypeFactory
                        .newInstance()
                        .newXMLGregorianCalendarDate(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0);
                double sisteDatoIMilliSec = (double) sisteDato.toGregorianCalendar().getTimeInMillis();
                double mndSidanIMilliSec = (double) xgcMonthAgo.toGregorianCalendar().getTimeInMillis();
                return (sisteDatoIMilliSec >= mndSidanIMilliSec);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        return false; //om nødvendig element mangler, skal ikkje behandlingstypen vises, logikken fiterer sakstemaet vekk   alle beahndlinger er ugyldig/utgått (se kode for filterbehandler for uttak av det visbare behandlingssettet).
    }

    private static final Predicate<Behandlingskjede> LOVLIG_BEHANDLING = wsBehandlingskjede -> HAR_LOVLIG_STATUS_PAA_BEHANDLING
            .and(HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING)
            .and(HAR_LOVLIG_PREFIX_PAA_BEHANDLING).test(wsBehandlingskjede);

    private static final Predicate<Sak> HAR_MINST_EN_LOVLIG_BEHANDLING = wsSak -> wsSak.getBehandlingskjede().stream().anyMatch(LOVLIG_BEHANDLING); //vil retunere alle behandlinger om en i kjeden er lovlig

    private static final Predicate<Sak> HAR_LOVLIG_SAKSTEMA = wsSak -> !ulovligeSakstema.contains(wsSak.getSakstema().getValue()); //filterer ut ulovlige sakstema basert på blacklist

    private static final Predicate<Sak> HAR_BEHANDLINGER = wsSak -> !wsSak.getBehandlingskjede().isEmpty(); //sak uten behandlinger skal ikke vises (sak med dokumenter skal)

    private static final Predicate<Behandling> HAR_LOVLIG_PREFIX = behandling -> !ULOVLIG_PREFIX.equals(behandling.getPrefix()); //prefix er to første tall av sisteBehandlingREF, og 17 er ulovlig av uviss grunn

    private static final Predicate<Behandling> HAR_LOVLIG_BEHANDLINGSTYPE = behandling -> lovligeBehandlingstyper.contains(behandling.getBehandlingsType()); //Er ikke alle behandlingstyper (XXyyyy) som skal taes med
}
