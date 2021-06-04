package no.nav.modiapersonoversikt.legacy.sak.transformers;

import no.nav.modiapersonoversikt.infrastructure.core.exception.ApplicationException;
import no.nav.modiapersonoversikt.legacy.sak.providerdomain.*;
import no.nav.modiapersonoversikt.legacy.sak.service.filter.FilterUtils;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.Behandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Behandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.Behandlingstyper;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.BehandlingsStatus.*;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.BehandlingsType.BEHANDLING;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.BehandlingsType.KVITTERING;
import static no.nav.modiapersonoversikt.legacy.sak.providerdomain.HenvendelseType.valueOf;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad.Dokumentforventninger;

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
                    throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
            }
        }
        throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
    }

    public static Soknad transformTilSoknad(WSSoknad wsSoknad) {
        String behandlingskjedeId = wsSoknad.getBehandlingsId();

        if (wsSoknad.getBehandlingsKjedeId() != null && !wsSoknad.getBehandlingsKjedeId().isEmpty()) {
            behandlingskjedeId = wsSoknad.getBehandlingsKjedeId();
        }
        return new Soknad()
                .withBehandlingsId(wsSoknad.getBehandlingsId())
                .withBehandlingskjedeId(behandlingskjedeId)
                .withJournalpostId(wsSoknad.getJournalpostId())
                .withStatus(Soknad.HenvendelseStatus.valueOf(wsSoknad.getHenvendelseStatus()))
                .withOpprettetDato(wsSoknad.getOpprettetDato())
                .withInnsendtDato(wsSoknad.getInnsendtDato())
                .withSistEndretDato(wsSoknad.getSistEndretDato())
                .withSkjemanummerRef(wsSoknad.getHovedskjemaKodeverkId())
                .withEttersending(wsSoknad.isEttersending())
                .withHenvendelseType(valueOf(WSHenvendelseType.valueOf(wsSoknad.getHenvendelseType()).name()))
                .withDokumenter(Optional.ofNullable(wsSoknad.getDokumentforventninger())
                        .orElse(new Dokumentforventninger())
                        .getDokumentforventning()
                        .stream()
                        .map(wsDokumentforventning -> transformTilDokument(wsDokumentforventning, wsSoknad.getHovedskjemaKodeverkId())).collect(toList()));
    }

    public static DokumentFraHenvendelse transformTilDokument(WSDokumentforventning wsDokumentforventning, String hovedskjemaId) {
        return new DokumentFraHenvendelse()
                .withKodeverkRef(wsDokumentforventning.getKodeverkId())
                .withTilleggstittel(wsDokumentforventning.getTilleggsTittel())
                .withUuid(wsDokumentforventning.getUuid())
                .withArkivreferanse(wsDokumentforventning.getArkivreferanse())
                .withInnsendingsvalg(DokumentFraHenvendelse.Innsendingsvalg.valueOf(wsDokumentforventning.getInnsendingsvalg()))
                .withErHovedskjema(hovedskjemaId.equals(wsDokumentforventning.getKodeverkId()));
    }

    private static List<DokumentFraHenvendelse> filtrerVedlegg(Soknad soknad, Predicate<DokumentFraHenvendelse> betingelse) {
        return soknad.getDokumenter().stream()
                .filter(betingelse)
                .filter(DokumentFraHenvendelse.ER_KVITTERING.negate())
                .collect(toList());
    }
}
