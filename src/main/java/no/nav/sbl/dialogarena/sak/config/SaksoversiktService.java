package no.nav.sbl.dialogarena.sak.config;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.common.records.Record;
import no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.oversikt.Tema;
import no.nav.tjeneste.domene.brukerdialog.henvendelsesoknader.v1.HenvendelseSoknaderPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.AktoerPortType;
import no.nav.tjeneste.virksomhet.aktoer.v1.HentAktoerIdForIdentPersonIkkeFunnet;
import no.nav.tjeneste.virksomhet.aktoer.v1.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.SakOgBehandlingPortType;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSBehandlingskjede;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.informasjon.finnsakogbehandlingskjedeliste.WSSak;
import no.nav.tjeneste.virksomhet.sakogbehandling.v1.meldinger.FinnSakOgBehandlingskjedeListeRequest;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.BEHANDLINGSTEMA;
import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.BEHANDLING_DATO;
import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.BEHANDLING_STATUS;
import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.BEHANDLING_TYPE;
import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.BehandlingsStatus.OPPRETTET;
import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.BehandlingsType.BEHANDLING;
import static no.nav.sbl.dialogarena.sak.viewdomain.detalj.GenerellBehandling.OPPRETTET_DATO;
import static no.nav.sbl.dialogarena.sak.viewdomain.oversikt.Tema.SISTOPPDATERTEBEHANDLING;
import static no.nav.sbl.dialogarena.sak.viewdomain.oversikt.Tema.TEMAKODE;


public class SaksoversiktService {

    @Inject
    private AktoerPortType fodselnummerAktorService;

    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;

    @Inject
    private HenvendelseSoknaderPortType henvendelseSoknaderPortType;

    public List<Record<Tema>> hentTemaer(String fnr) {
        HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
        request.setIdent(fnr);
        String aktorId;
        try {
            aktorId = fodselnummerAktorService.hentAktoerIdForIdent(request).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            throw new SystemException("Feil ved kall til hentaktoertjeneste", hentAktoerIdForIdentPersonIkkeFunnet);
        }
        Comparator<Record<Tema>> nyesteOppdatertSammenligner = new Comparator<Record<Tema>>() {
            @Override
            public int compare(Record<Tema> o1, Record<Tema> o2) {
                return o2.get(SISTOPPDATERTEBEHANDLING).get(BEHANDLING_DATO).compareTo(o1.get(SISTOPPDATERTEBEHANDLING).get(BEHANDLING_DATO));
            }
        };
        return on(hentSakerForAktor(aktorId)).map(TEMA).collect(nyesteOppdatertSammenligner);
    }

    public static final Transformer<WSSak, Record<Tema>> TEMA = new Transformer<WSSak, Record<Tema>>() {
        @Override
        public Record<Tema> transform(WSSak wsSak) {
            Record<Tema> tema = new Record<Tema>().with(TEMAKODE, wsSak.getSakstema().getValue());
            return behandlingskjedeFinnes(wsSak) ? tema.with(SISTOPPDATERTEBEHANDLING, hentForsteBehandlingskjede(wsSak)) : tema;
        }
    };

    private static Record<? extends GenerellBehandling> hentForsteBehandlingskjede(WSSak wsSak) {
        return on(wsSak.getBehandlingskjede()).map(BEHANDLINGSKJEDE_TIL_BEHANDLING).collect(new OmvendtKronologiskHendelseComparator()).get(0);
    }

    private static boolean behandlingskjedeFinnes(WSSak wsSak) {
        return optional(wsSak.getBehandlingskjede()).isSome() && !wsSak.getBehandlingskjede().isEmpty();
    }

    protected List<WSSak> hentSakerForAktor(String aktorId) {
        try {
            return sakOgBehandlingPortType.finnSakOgBehandlingskjedeListe(new FinnSakOgBehandlingskjedeListeRequest().withAktoerREF(aktorId)).getSak();
        } catch (RuntimeException ex) {
            throw new SystemException("Feil ved kall til sakogbehandling", ex);
        }
    }

    public static final Transformer<WSBehandlingskjede, Record<GenerellBehandling>> BEHANDLINGSKJEDE_TIL_BEHANDLING =
            new Transformer<WSBehandlingskjede, Record<GenerellBehandling>>() {

                @Override
                public Record<GenerellBehandling> transform(WSBehandlingskjede wsBehandlingskjede) {
                    return new Record<GenerellBehandling>()
                            .with(BEHANDLING_TYPE, BEHANDLING)
                            .with(BEHANDLING_DATO, behandlingsDato(wsBehandlingskjede))
                            .with(OPPRETTET_DATO, wsBehandlingskjede.getStart())
                            .with(BEHANDLING_STATUS, behandlingsStatus(wsBehandlingskjede))
                            .with(BEHANDLINGSTEMA, wsBehandlingskjede.getBehandlingskjedetype().getValue());
                }

                private GenerellBehandling.BehandlingsStatus behandlingsStatus(WSBehandlingskjede wsBehandlingskjede) {
                    return erAvsluttet(wsBehandlingskjede) ? AVSLUTTET : OPPRETTET;
                }

                private DateTime behandlingsDato(WSBehandlingskjede wsBehandlingskjede) {
                    return erAvsluttet(wsBehandlingskjede) ? wsBehandlingskjede.getSlutt() : wsBehandlingskjede.getStart();
                }

                private boolean erAvsluttet(WSBehandlingskjede wsBehandlingskjede) {
                    return wsBehandlingskjede.getSlutt() != null;
                }
            };

}
