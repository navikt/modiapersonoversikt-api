package no.nav.modiapersonoversikt.legacy.sak.transformers;

import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.FilterUtils;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Behandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Behandlingstyper;
import org.joda.time.DateTime;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.BehandlingsStatus.*;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.BehandlingsType.BEHANDLING;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.BehandlingsType.KVITTERING;

public class Transformers {

    public static final Function<Soknad, Behandling> SOKNAD_TIL_KVITTERING = soknad -> {
        BehandlingsStatus status = soknad.getInnsendtDato() != null ? FERDIG_BEHANDLET : UNDER_BEHANDLING;
        return new Behandling()
                .withBehandlingsId(soknad.getBehandlingsId())
                .withBehandlingskjedeId(soknad.getBehandlingskjedeId())
                .withKvitteringType(soknad.getType())
                .withBehandlingsDato(soknad.getInnsendtDato())
                .withSkjemanummerRef(soknad.getSkjemanummerRef())
                .withBehandlingStatus(status)
                .withBehandlingKvittering(KVITTERING)
                .withEttersending(soknad.getEttersending())
                .withInnsendteDokumenter(filtrerVedlegg(soknad, DokumentFraHenvendelse.INNSENDT))
                .withManglendeDokumenter(filtrerVedlegg(soknad, manglendeDokumenter()));
    };

    private static Predicate<DokumentFraHenvendelse> manglendeDokumenter() {
        return dokumentFraHenvendelse -> !DokumentFraHenvendelse.INNSENDT.test(dokumentFraHenvendelse) && !dokumentFraHenvendelse.erHovedskjema();
    }

    public static final Function<Behandlingskjede, Behandling> TIL_BEHANDLING = (Behandlingskjede wsBehandlingskjede) -> {
        Behandling behandling = new Behandling()
                .withBehandlingsType(wsBehandlingskjede.getSisteBehandlingstype().getValue())
                .withBehandlingsDato(FilterUtils.behandlingsDato(wsBehandlingskjede))
                .withOpprettetDato(new DateTime(wsBehandlingskjede.getStart().toGregorianCalendar().getTime()))
                .withPrefix(wsBehandlingskjede.getSisteBehandlingREF().substring(0, 2))
                .withBehandlingsId(wsBehandlingskjede.getSisteBehandlingREF())
                .withBehandlingStatus(behandlingsStatus(wsBehandlingskjede))
                .withBehandlingKvittering(kvitteringstype(wsBehandlingskjede.getSisteBehandlingstype()));
        Behandlingstemaer behandlingstema = wsBehandlingskjede.getBehandlingstema();
        if (behandlingstema != null) {
            behandling = behandling.withBehandlingsTema(behandlingstema.getValue());
        }
        return behandling;
    };

    private static BehandlingsType kvitteringstype(Behandlingstyper sisteBehandlingstype) {
        return FilterUtils.erKvitteringstype(sisteBehandlingstype.getValue()) ? KVITTERING : BEHANDLING;
    }

    private static BehandlingsStatus behandlingsStatus(Behandlingskjede wsBehandlingskjede) {
        if (wsBehandlingskjede.getSisteBehandlingsstatus() != null) {
            switch (wsBehandlingskjede.getSisteBehandlingsstatus().getValue()) {
                case FilterUtils.AVSLUTTET:
                    return FERDIG_BEHANDLET;
                case FilterUtils.OPPRETTET:
                    return UNDER_BEHANDLING;
                case FilterUtils.AVBRUTT:
                    return AVBRUTT;
                default:
                    throw new RuntimeException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
            }
        }
        throw new RuntimeException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
    }

    private static List<DokumentFraHenvendelse> filtrerVedlegg(Soknad soknad, Predicate<DokumentFraHenvendelse> betingelse) {
        return soknad.getDokumenter().stream()
                .filter(betingelse)
                .filter(DokumentFraHenvendelse.ER_KVITTERING.negate())
                .collect(toList());
    }
}
