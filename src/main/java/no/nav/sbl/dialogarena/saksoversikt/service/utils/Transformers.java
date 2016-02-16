package no.nav.sbl.dialogarena.saksoversikt.service.utils;

import no.nav.modig.core.exception.ApplicationException;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.saksoversikt.service.service.Filter;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.GenerellBehandling;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering;
import no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSDokumentforventning;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSHenvendelseType;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstemaer;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.sakogbehandling.WSBehandlingstyper;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.both;
import static no.nav.modig.lang.collections.PredicateUtils.not;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.saksoversikt.service.service.Filter.erAvsluttet;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.HenvendelseType.valueOf;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Dokument.Innsendingsvalg;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering.BehandlingsStatus;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.detalj.Kvittering.BehandlingsType;
import static no.nav.sbl.dialogarena.saksoversikt.service.viewdomain.oversikt.Soknad.HenvendelseStatus;
import static no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.informasjon.WSSoknad.Dokumentforventninger;

public class Transformers {

    public static final Transformer<Record<Soknad>, Record<Kvittering>> SOKNAD_TIL_KVITTERING = new Transformer<Record<Soknad>, Record<Kvittering>>() {
        @Override
        public Record<Kvittering> transform(Record<Soknad> soknadRecord) {
            GenerellBehandling.BehandlingsStatus status = soknadRecord.get(Soknad.INNSENDT_DATO) != null ? BehandlingsStatus.AVSLUTTET : BehandlingsStatus.OPPRETTET;
            return new Record<Kvittering>()
                    .with(Kvittering.BEHANDLINGS_ID, soknadRecord.get(Soknad.BEHANDLINGS_ID))
                    .with(Kvittering.BEHANDLINGSKJEDE_ID, soknadRecord.get(Soknad.BEHANDLINGSKJEDE_ID))
                    .with(Kvittering.JOURNALPOST_ID, soknadRecord.get(Soknad.JOURNALPOST_ID))
                    .with(Kvittering.KVITTERINGSTYPE, soknadRecord.get(Soknad.TYPE))
                    .with(GenerellBehandling.BEHANDLING_DATO, soknadRecord.get(Soknad.INNSENDT_DATO))
                    .with(Soknad.SKJEMANUMMER_REF, soknadRecord.get(Soknad.SKJEMANUMMER_REF))
                    .with(GenerellBehandling.BEHANDLING_STATUS, status)
                    .with(GenerellBehandling.BEHANDLINGKVITTERING, BehandlingsType.KVITTERING)
                    .with(Kvittering.ETTERSENDING, soknadRecord.get(Soknad.ETTERSENDING))
                    .with(Kvittering.ARKIVREFERANSE_ORIGINALKVITTERING, arkivreferanseOriginalkvittering(soknadRecord))
                    .with(Kvittering.INNSENDTE_DOKUMENTER, filtrerVedlegg(soknadRecord, Dokument.INNSENDT))
                    .with(Kvittering.MANGLENDE_DOKUMENTER, filtrerVedlegg(soknadRecord, both(not(Dokument.INNSENDT)).and(not(Dokument.ER_HOVEDSKJEMA))));
        }

        private Optional<String> arkivreferanseOriginalkvittering(Record<Soknad> soknad) {
            return on(soknad.get(Soknad.DOKUMENTER))
                    .filter(Dokument.ER_KVITTERING)
                    .map(arkivreveranse())
                    .head();
        }

        private Transformer<Record<Dokument>, String> arkivreveranse() {
            return dokument -> dokument.get(Dokument.ARKIVREFERANSE);
        }

    };

    public static final Transformer<WSBehandlingskjede, Record<GenerellBehandling>> BEHANDLINGSKJEDE_TIL_BEHANDLING =
            new Transformer<WSBehandlingskjede, Record<GenerellBehandling>>() {

                @Override
                public Record<GenerellBehandling> transform(WSBehandlingskjede wsBehandlingskjede) {
                    GenerellBehandling.BehandlingsType type = erKvitteringstype(wsBehandlingskjede.getSisteBehandlingstype()) ? BehandlingsType.KVITTERING : BehandlingsType.BEHANDLING;
                    Record<GenerellBehandling> generellBehandling = new Record<GenerellBehandling>()
                            .with(GenerellBehandling.BEHANDLINGS_TYPE, wsBehandlingskjede.getSisteBehandlingstype().getValue())
                            .with(GenerellBehandling.BEHANDLING_DATO, behandlingsDato(wsBehandlingskjede))
                            .with(GenerellBehandling.OPPRETTET_DATO, wsBehandlingskjede.getStart())
                            .with(GenerellBehandling.PREFIX, wsBehandlingskjede.getSisteBehandlingREF().substring(0, 2))
                            .with(GenerellBehandling.BEHANDLINGKVITTERING, type)
                            .with(GenerellBehandling.BEHANDLINGS_ID, wsBehandlingskjede.getSisteBehandlingREF())
                            .with(GenerellBehandling.BEHANDLING_STATUS, behandlingsStatus(wsBehandlingskjede));
                    WSBehandlingstemaer behandlingstema = wsBehandlingskjede.getBehandlingstema();
                    if (behandlingstema != null) {
                        generellBehandling = generellBehandling.with(GenerellBehandling.BEHANDLINGSTEMA, behandlingstema.getValue());
                    }
                    return generellBehandling;
                }

                private boolean erKvitteringstype(WSBehandlingstyper sisteBehandlingstype) {
                    return equals(sisteBehandlingstype.getValue()) || equals((sisteBehandlingstype.getValue()));
                }

                private GenerellBehandling.BehandlingsStatus behandlingsStatus(WSBehandlingskjede wsBehandlingskjede) {
                    if (wsBehandlingskjede.getSisteBehandlingsstatus() != null) {
                        if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.AVSLUTTET)) {
                            return BehandlingsStatus.AVSLUTTET;
                        } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.OPPRETTET)) {
                            return BehandlingsStatus.OPPRETTET;
                        } else if (wsBehandlingskjede.getSisteBehandlingsstatus().getValue().equals(Filter.AVBRUTT)) {
                            return BehandlingsStatus.AVBRUTT;
                        } else {
                            throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
                        }
                    }
                    throw new ApplicationException("Ukjent behandlingsstatus mottatt: " + wsBehandlingskjede.getSisteBehandlingsstatus().getValue());
                }

                private DateTime behandlingsDato(WSBehandlingskjede wsBehandlingskjede) {
                    return erAvsluttet(wsBehandlingskjede) ? wsBehandlingskjede.getSlutt() : wsBehandlingskjede.getStart();
                }
            };



    public static final Transformer<WSSoknad, Record<Soknad>> SOKNAD = wsSoknad -> {
        String behandlingskjedeId = wsSoknad.getBehandlingsId();

        if (wsSoknad.getBehandlingsKjedeId() != null && !wsSoknad.getBehandlingsKjedeId().isEmpty()) {
            behandlingskjedeId = wsSoknad.getBehandlingsKjedeId();
        }
        return new Record<Soknad>()
                .with(Soknad.BEHANDLINGS_ID, wsSoknad.getBehandlingsId())
                .with(Soknad.BEHANDLINGSKJEDE_ID, behandlingskjedeId)
                .with(Soknad.JOURNALPOST_ID, wsSoknad.getJournalpostId())
                .with(Soknad.STATUS, HenvendelseStatus.valueOf(wsSoknad.getHenvendelseStatus()))
                .with(Soknad.OPPRETTET_DATO, wsSoknad.getOpprettetDato())
                .with(Soknad.INNSENDT_DATO, wsSoknad.getInnsendtDato())
                .with(Soknad.SISTENDRET_DATO, wsSoknad.getSistEndretDato())
                .with(Soknad.SKJEMANUMMER_REF, wsSoknad.getHovedskjemaKodeverkId())
                .with(Soknad.ETTERSENDING, wsSoknad.isEttersending())
                .with(Soknad.TYPE, valueOf(WSHenvendelseType.valueOf(wsSoknad.getHenvendelseType()).name()))
                .with(Soknad.DOKUMENTER, on(optional(wsSoknad.getDokumentforventninger())
                        .getOrElse(new Dokumentforventninger())
                        .getDokumentforventning())
                        .map(tilDokument(wsSoknad.getHovedskjemaKodeverkId())).collect());
    };

    public static Transformer<WSDokumentforventning, Record<Dokument>> tilDokument(final String hovedskjemaId) {
        return wsDokumentforventning -> new Record<Dokument>()
                .with(Dokument.KODEVERK_REF, wsDokumentforventning.getKodeverkId())
                .with(Dokument.TILLEGGSTITTEL, wsDokumentforventning.getTilleggsTittel())
                .with(Dokument.UUID, wsDokumentforventning.getUuid())
                .with(Dokument.ARKIVREFERANSE, wsDokumentforventning.getArkivreferanse())
                .with(Dokument.INNSENDINGSVALG, Innsendingsvalg.valueOf(wsDokumentforventning.getInnsendingsvalg()))
                .with(Dokument.HOVEDSKJEMA, hovedskjemaId.equals(wsDokumentforventning.getKodeverkId()));
    }

    public static final Transformer<WSSak, String> TEMAKODE_FOR_SAK = wsSak -> wsSak.getSakstema().getValue();
    
    private static List<Record<Dokument>> filtrerVedlegg(Record<Soknad> soknad, Predicate<Record<Dokument>> betingelse) {
        return on(soknad.get(Soknad.DOKUMENTER))
                .filter(both(betingelse).and(not(Dokument.ER_KVITTERING)))
                .collect();
    }
}
