package no.nav.sbl.dialogarena.modiabrukerdialog.sak.transformers;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.*;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.filter.FilterUtils;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.Java8Utils;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.BehandlingsStatus.*;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.BehandlingsType.BEHANDLING;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.BehandlingsType.KVITTERING;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.HenvendelseType.valueOf;
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

    public static final Function<WSBehandlingskjede, Behandling> TIL_BEHANDLING = (WSBehandlingskjede wsBehandlingskjede) -> {
        Behandling behandling = new Behandling()
                .withBehandlingsType(wsBehandlingskjede.getSisteBehandlingstype().getValue())
                .withBehandlingsDato(FilterUtils.behandlingsDato(wsBehandlingskjede))
                .withOpprettetDato(wsBehandlingskjede.getStart())
                .withPrefix(wsBehandlingskjede.getSisteBehandlingREF().substring(0, 2))
                .withBehandlingsId(wsBehandlingskjede.getSisteBehandlingREF())
                .withBehandlingStatus(behandlingsStatus(wsBehandlingskjede))
                .withBehandlingKvittering(kvitteringstype(wsBehandlingskjede.getSisteBehandlingstype()));
        WSBehandlingstemaer behandlingstema = wsBehandlingskjede.getBehandlingstema();
        if (behandlingstema != null) {
            behandling = behandling.withBehandlingsTema(behandlingstema.getValue());
        }
        return behandling;
    };

    private static BehandlingsType kvitteringstype(WSBehandlingstyper sisteBehandlingstype) {
        return FilterUtils.erKvitteringstype(sisteBehandlingstype.getValue()) ? KVITTERING : BEHANDLING;
    }

    private static BehandlingsStatus behandlingsStatus(WSBehandlingskjede wsBehandlingskjede) {
        if (wsBehandlingskjede.getSisteBehandlingsstatus() != null) {
            if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.AVSLUTTET)) {
                return FERDIG_BEHANDLET;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.OPPRETTET)) {
                return UNDER_BEHANDLING;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(FilterUtils.AVBRUTT)) {
                return AVBRUTT;
            } else {
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
                .withDokumenter(Java8Utils.optional(wsSoknad.getDokumentforventninger())
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
