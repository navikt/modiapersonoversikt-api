package no.nav.sbl.dialogarena.sak.config;

import no.nav.modig.core.exception.SystemException;
import no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling;
import no.nav.sbl.dialogarena.sak.viewdomain.widget.TemaVM;
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
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.AVSLUTTET;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsStatus.OPPRETTET;
import static no.nav.sbl.dialogarena.sak.viewdomain.lamell.GenerellBehandling.BehandlingsType.BEHANDLING;


public class SaksoversiktService {

    @Inject
    private AktoerPortType fodselnummerAktorService;

    @Inject
    private SakOgBehandlingPortType sakOgBehandlingPortType;

    @Inject
    private HenvendelseSoknaderPortType henvendelseSoknaderPortType;

    public List<TemaVM> hentTemaer(String fnr) {
        HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest();
        request.setIdent(fnr);
        String aktorId;
        try {
            aktorId = fodselnummerAktorService.hentAktoerIdForIdent(request).getAktoerId();
        } catch (HentAktoerIdForIdentPersonIkkeFunnet hentAktoerIdForIdentPersonIkkeFunnet) {
            throw new SystemException("Feil ved kall til hentaktoertjeneste", hentAktoerIdForIdentPersonIkkeFunnet);
        }
        Comparator<TemaVM> nyesteOppdatertSammenligner = new Comparator<TemaVM>() {
            @Override
            public int compare(TemaVM o1, TemaVM o2) {
                return o2.sistoppdaterteBehandling.behandlingDato.compareTo(o1.sistoppdaterteBehandling.behandlingDato);
            }
        };
        return on(hentSakerForAktor(aktorId)).map(TEMA).collect(nyesteOppdatertSammenligner);
    }

    public static final Transformer<WSSak, TemaVM> TEMA = new Transformer<WSSak, TemaVM>() {
        @Override
        public TemaVM transform(WSSak wsSak) {
            TemaVM tema = new TemaVM().withTemaKode(wsSak.getSakstema().getValue());
            return behandlingskjedeFinnes(wsSak) ? tema.withSistOppdaterteBehandling(hentForsteBehandlingskjede(wsSak)) : tema;
        }
    };

    private static GenerellBehandling hentForsteBehandlingskjede(WSSak wsSak) {
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

    public static final Transformer<WSBehandlingskjede, GenerellBehandling> BEHANDLINGSKJEDE_TIL_BEHANDLING =
            new Transformer<WSBehandlingskjede, GenerellBehandling>() {

                @Override
                public GenerellBehandling transform(WSBehandlingskjede wsBehandlingskjede) {
                    return new GenerellBehandling()
                            .withBehandlingsType(BEHANDLING)
                            .withBehandlingsDato(behandlingsDato(wsBehandlingskjede))
                            .withOpprettetDato(wsBehandlingskjede.getStart())
                            .withBehandlingStatus(behandlingsStatus(wsBehandlingskjede))
                            .withBehandlingsTema(wsBehandlingskjede.getBehandlingskjedetype().getValue());
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
