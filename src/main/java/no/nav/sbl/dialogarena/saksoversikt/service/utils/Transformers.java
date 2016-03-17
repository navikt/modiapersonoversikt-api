package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsType;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse;
import no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.Behandling;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsStatus.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse.ER_KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.DokumentFraHenvendelse.Innsendingsvalg;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsType.BEHANDLING;
import static no.nav.sbl.dialogarena.saksoversikt.service.providerdomain.BehandlingsType.KVITTERING;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.erAvsluttet;
import static no.nav.sbl.dialogarena.saksoversikt.service.utils.Java8Utils.*;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType.valueOf;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.HenvendelseStatus;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad.Dokumentforventninger;

public class Transformers {

    public static final Transformer<Soknad, Behandling> SOKNAD_TIL_KVITTERING = new Transformer<Soknad, Behandling>() {
        @Override
        public Behandling transform(Soknad soknad) {
            BehandlingsStatus status = soknad.getInnsendtDato() != null ? FERDIG_BEHANDLET : UNDER_BEHANDLING;
            return new Behandling()
                    .withBehandlingsId(soknad.getBehandlingsId())
                    .withBehandlingskjedeId(soknad.getBehandlingskjedeId())
                    .withJournalPostId(soknad.getJournalpostId())
                    .withKvitteringType(soknad.getType())
                    .withBehandlingsDato(soknad.getInnsendtDato())
                    .withSkjemanummerRef(soknad.getSkjemanummerRef())
                    .withBehandlingStatus(status)
                    .withBehandlingKvittering(KVITTERING)
                    .withEttersending(soknad.getEttersending())
                    .withArkivreferanseOriginalkvittering(arkivreferanseOriginalkvittering(soknad))
                    .withInnsendteDokumenter(filtrerVedlegg(soknad, DokumentFraHenvendelse.INNSENDT))
                    .withManglendeDokumenter(filtrerVedlegg(soknad, manglendeDokumenter()));
        }

        private Optional<String> arkivreferanseOriginalkvittering(Soknad soknad) {
            return soknad.getDokumenter().stream()
                    .filter(ER_KVITTERING)
                    .map(dokumentFraHenvendelse -> dokumentFraHenvendelse.getArkivreferanse())
                    .findFirst();
        }
    };

    private static Predicate<DokumentFraHenvendelse> manglendeDokumenter() {
        return dokumentFraHenvendelse -> !DokumentFraHenvendelse.INNSENDT.test(dokumentFraHenvendelse) && !dokumentFraHenvendelse.erHovedskjema();
    }

    public static final Behandling transformTilBehandling(WSBehandlingskjede wsBehandlingskjede) {
        Behandling behandling = new Behandling()
                .withBehandlingsType(wsBehandlingskjede.getSisteBehandlingstype().getValue())
                .withBehandlingsDato(behandlingsDato(wsBehandlingskjede))
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
    }

    private static BehandlingsType kvitteringstype(WSBehandlingstyper sisteBehandlingstype) {
        return sisteBehandlingstype.getValue().equals(KVITTERING) ? KVITTERING : BEHANDLING;
    }

    private static DateTime behandlingsDato(WSBehandlingskjede wsBehandlingskjede) {
        return erAvsluttet(wsBehandlingskjede) ? wsBehandlingskjede.getSlutt() : wsBehandlingskjede.getStart();
    }

    private static BehandlingsStatus behandlingsStatus(WSBehandlingskjede wsBehandlingskjede) {
        if (wsBehandlingskjede.getSisteBehandlingsstatus() != null) {
            if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.AVSLUTTET)) {
                return FERDIG_BEHANDLET;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.OPPRETTET)) {
                return UNDER_BEHANDLING;
            } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.AVBRUTT)) {
                return AVBRUTT;
            } else {
                throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
            }
        }
        throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
    }

    public static final Soknad transformTilSoknad(WSSoknad wsSoknad) {
        return new Soknad()
                .withBehandlingsId(wsSoknad.getBehandlingsId())
                .withBehandlingskjedeId(wsSoknad.getBehandlingsKjedeId())
                .withJournalpostId(wsSoknad.getJournalpostId())
                .withStatus(HenvendelseStatus.valueOf(wsSoknad.getHenvendelseStatus()))
                .withOpprettetDato(wsSoknad.getOpprettetDato())
                .withInnsendtDato(wsSoknad.getInnsendtDato())
                .withSistEndretDato(wsSoknad.getSistEndretDato())
                .withSkjemanummerRef(wsSoknad.getHovedskjemaKodeverkId())
                .withEttersending(wsSoknad.isEttersending())
                .withHenvendelseType(valueOf(WSHenvendelseType.valueOf(wsSoknad.getHenvendelseType()).name()))
                .withDokumenter(optional(wsSoknad.getDokumentforventninger())
                        .orElse(new Dokumentforventninger())
                        .getDokumentforventning()
                        .stream()
                        .map(wsDokumentforventning -> transformTilDokument(wsDokumentforventning, wsSoknad.getHovedskjemaKodeverkId())).collect(toList()));
    }

    public static final DokumentFraHenvendelse transformTilDokument(WSDokumentforventning wsDokumentforventning, String hovedskjemaId) {
        return new DokumentFraHenvendelse()
                .withKodeverkRef(wsDokumentforventning.getKodeverkId())
                .withTilleggstittel(wsDokumentforventning.getTilleggsTittel())
                .withUuid(wsDokumentforventning.getUuid())
                .withArkivreferanse(wsDokumentforventning.getArkivreferanse())
                .withInnsendingsvalg(Innsendingsvalg.valueOf(wsDokumentforventning.getInnsendingsvalg()))
                .withErHovedskjema(hovedskjemaId.equals(wsDokumentforventning.getKodeverkId()));
    }

    private static List<DokumentFraHenvendelse> filtrerVedlegg(Soknad soknad, Predicate<DokumentFraHenvendelse> betingelse) {
        return soknad.getDokumenter().stream()
                .filter(betingelse)
                .filter(ER_KVITTERING.negate())
                .collect(toList());
    }
}
