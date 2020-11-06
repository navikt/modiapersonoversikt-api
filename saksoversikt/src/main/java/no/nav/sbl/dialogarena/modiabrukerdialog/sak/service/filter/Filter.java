package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter;

import no.nav.modig.content.ContentRetriever;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Behandling;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.BehandlingsStatus;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Sak;

import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Filter {
    public final static String ULOVLIG_PREFIX = "17";

    @Autowired
    private ContentRetriever cms;

    private static List<String> ulovligeSakstema;
    private static List<String> lovligeBehandlingstyper;

    public synchronized List<Sak> filtrerSaker(List<Sak> saker) {
        lovligeBehandlingstyper = Arrays.asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        ulovligeSakstema = Arrays.asList(cms.hentTekst("filter.ulovligesakstema").trim().split("\\s*,\\s*"));
        return saker.stream()
                .filter(HAR_LOVLIG_SAKSTEMA)
                .filter(HAR_BEHANDLINGER)
                .filter(HAR_MINST_EN_LOVLIG_BEHANDLING).collect(toList());
    }

    public synchronized List<Behandling> filtrerBehandlinger(List<Behandling> behandlinger) {
        lovligeBehandlingstyper = Arrays.asList(cms.hentTekst("filter.lovligebehandlingstyper").trim().split("\\s*,\\s*"));
        Stream<Behandling> lovligeBehandlinger = behandlinger.stream().filter(HAR_LOVLIG_BEHANDLINGSTYPE);

        return lovligeBehandlinger
                .filter(HAR_LOVLIG_BEHANDLINGSSTATUS)
                .filter(HAR_LOVLIG_PREFIX)
                .sorted((o1, o2) -> o2.getBehandlingDato().compareTo(o1.getBehandlingDato()))
                .collect(toList());
    }

    private static final Predicate<Behandling> HAR_LOVLIG_BEHANDLINGSSTATUS = behandling -> !behandling.getBehandlingsStatus().equals(BehandlingsStatus.AVBRUTT);

    private static final Predicate<Behandlingskjede> HAR_LOVLIG_PREFIX_PAA_BEHANDLING = kjede -> !kjede.getSisteBehandlingREF().startsWith(ULOVLIG_PREFIX);

    private static final Predicate<Behandlingskjede> HAR_LOVLIG_STATUS_PAA_BEHANDLING = kjede ->
            kjede.getSisteBehandlingsstatus().getValue() != null &&
                    ((kjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.OPPRETTET) && !FilterUtils.erKvitteringstype(kjede.getSisteBehandlingstype().getValue()))
                            || kjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.AVSLUTTET));

    private static final Predicate<Behandlingskjede> HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING = kjede -> {
        String type = kjede.getSisteBehandlingstype().getValue();

        return (FilterUtils.erKvitteringstype(type) && FilterUtils.erAvsluttet(kjede) && Under1MndSidenFerdigstillelse(kjede)) || lovligeBehandlingstyper.contains(type);

    };

    private static boolean Under1MndSidenFerdigstillelse(Behandlingskjede kjede) {
        if (kjede.getSisteBehandlingsoppdatering() != (null)) {

            try {
                XMLGregorianCalendar sisteDato = kjede.getSisteBehandlingsoppdatering();
                XMLGregorianCalendar xgcMonthAgo = DatatypeFactory.newInstance().newXMLGregorianCalendar();
                GregorianCalendar now = new GregorianCalendar();
                xgcMonthAgo.setYear(now.get(Calendar.YEAR));
                xgcMonthAgo.setMonth(now.get(Calendar.MONTH) - 1);
                xgcMonthAgo.setDay(now.get(Calendar.DAY_OF_MONTH));
                return (sisteDato.getMillisecond() > xgcMonthAgo.getMillisecond());
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        System.out.println("mangler behandlingstatus");
        return false; //mangler nødvendig element, skal ikkje behandlingstypen vises.
    }


    private static final Predicate<Behandlingskjede> LOVLIG_BEHANDLING = wsBehandlingskjede -> HAR_LOVLIG_STATUS_PAA_BEHANDLING
            .and(HAR_LOVLIG_BEHANDLINGSTYPE_ELLER_AVSLUTTET_KVITTERING)
            .and(HAR_LOVLIG_PREFIX_PAA_BEHANDLING).test(wsBehandlingskjede);

    private static final Predicate<Sak> HAR_MINST_EN_LOVLIG_BEHANDLING = wsSak -> wsSak.getBehandlingskjede().stream().anyMatch(LOVLIG_BEHANDLING);

    private static final Predicate<Sak> HAR_LOVLIG_SAKSTEMA = wsSak -> !ulovligeSakstema.contains(wsSak.getSakstema().getValue());

    private static final Predicate<Sak> HAR_BEHANDLINGER = wsSak -> !wsSak.getBehandlingskjede().isEmpty();

    private static final Predicate<Behandling> HAR_LOVLIG_PREFIX = behandling -> !ULOVLIG_PREFIX.equals(behandling.getPrefix());

    private static final Predicate<Behandling> HAR_LOVLIG_BEHANDLINGSTYPE = behandling -> lovligeBehandlingstyper.contains(behandling.getBehandlingsType());
}
