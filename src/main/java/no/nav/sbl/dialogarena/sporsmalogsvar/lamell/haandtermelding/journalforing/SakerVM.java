package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.ArenaService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.kodeverk.GsakKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.injection.Injector;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Arrays.asList;
import static java.util.Map.Entry;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GENERELL_SAK;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GODKJENT_TEMA_FOR_GENERELLE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.TEMA;

public class SakerVM implements Serializable {

    // TODO: Kodeverk - Dette er en midlertidig mapping mellom temagruppe og tema, mens vi venter på kodeverk.
    public static final Map<String, List<String>> TEMA_MAPPING = new HashMap<String, List<String>>() {
        {
            put("ARBD", asList("DAG", "AAP", "FOS", "IND", "OPP", "SYK", "SYM", "VEN", "YRK"));
            put("FMLI", asList("FOR", "BAR", "BID", "ENF", "GRA", "GRU", "KON", "OMS"));
            put("HJLPM", asList("BIL", "HEL", "HJE", "MOB"));
            put("OVRG", asList("FUL", "MED", "SER", "TRK"));
            put("PENS", asList("PEN", "UFO"));
        }
    };
    public static final String TEMA_UTEN_TEMAGRUPPE = "Ukjent";

    private TemaSakerListe temaSakerListeFagsak;
    private TemaSakerListe temaSakerListeGenerelle;
    private InnboksVM innboksVM;

    @Inject
    private GsakService gsakService;
    @Inject
    private ArenaService arenaService;
    @Inject
    private GsakKodeverk gsakKodeverk;

    public SakerVM(InnboksVM innboksVM) {
        this.innboksVM = innboksVM;
        Injector.get().inject(this);
    }

    public final void oppdater() {
        List<Sak> sakerForBruker = gsakService.hentSakerForBruker(innboksVM.getFnr());
        supplerMedOppfolgingssakDersomRelevant(sakerForBruker);
        on(sakerForBruker).forEach(new Closure<Sak>() {
            @Override
            public void execute(Sak sak) {
                String fagsystemnavn = gsakKodeverk.hentFagsystemMapping().get(sak.fagsystemKode);
                sak.fagsystemNavn = fagsystemnavn != null ? fagsystemnavn : sak.fagsystemKode;
            }
        });

        Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker = splittIGenerelleSakerOgIkkeGenerelleSaker(sakerForBruker);
        temaSakerListeFagsak = getFagsaker(generelleOgIkkeGenerelleSaker);
        temaSakerListeGenerelle = getGenerelleSaker(generelleOgIkkeGenerelleSaker);
    }

    private void supplerMedOppfolgingssakDersomRelevant(List<Sak> saker) {
        List<Sak> oppfolgingssaker = on(saker).filter(Sak.IS_OPPFOLGINGSFAGSAK).collect();
        if (oppfolgingssaker.isEmpty()) {
            Optional<Sak> oppfolgingssak = arenaService.hentOppfolgingssak(innboksVM.getFnr());
            if (oppfolgingssak.isSome()) {
                saker.add(oppfolgingssak.get());
            }
        }
    }

    private Map<Boolean, List<Sak>> splittIGenerelleSakerOgIkkeGenerelleSaker(List<Sak> saker) {
        return on(saker).reduce(indexBy(IS_GENERELL_SAK));
    }

    private TemaSakerListe getFagsaker(Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker) {
        List<Sak> fagsakerFraGodkjenteFagsystemer = on(generelleOgIkkeGenerelleSaker.get(false))
                .filter(IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK)
                .collect();
        return new TemaSakerListe(grupperSakerPaaTema(fagsakerFraGodkjenteFagsystemer));
    }

    private TemaSakerListe getGenerelleSaker(Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker) {
        List<Sak> generelleSakerFraGodkjentFagsystemMedKunGodkjenteTemaer = on(generelleOgIkkeGenerelleSaker.get(true))
                .filter(IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE)
                .filter(IS_GODKJENT_TEMA_FOR_GENERELLE)
                .collect();
        return new TemaSakerListe(grupperSakerPaaTema(generelleSakerFraGodkjentFagsystemMedKunGodkjenteTemaer));
    }

    private List<TemaSaker> grupperSakerPaaTema(List<Sak> saker) {
        Map<String, List<Sak>> sakerGruppertPaaTema = on(saker).reduce(indexBy(TEMA, new TreeMap<String, List<Sak>>()));
        return new ArrayList<>(on(sakerGruppertPaaTema.entrySet()).map(TIL_TEMASAKER).collect());
    }

    public List<TemaSaker> getFagsakerGruppertPaaTema() {
        return temaSakerListeFagsak.sorter(innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
    }

    public List<TemaSaker> getGenerelleSakerGruppertPaaTema() {
        return temaSakerListeGenerelle.sorter(innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
    }

    private static final Transformer<Entry<String, List<Sak>>, TemaSaker> TIL_TEMASAKER = new Transformer<Entry<String, List<Sak>>, TemaSaker>() {
        @Override
        public TemaSaker transform(Entry<String, List<Sak>> entry) {
            return new TemaSaker(entry.getKey(), finnTemaetsGruppe(entry.getKey()), entry.getValue());
        }
    };

    private static String finnTemaetsGruppe(String tema) {
        for (Entry<String, List<String>> temaEntry : TEMA_MAPPING.entrySet()) {
            if (temaEntry.getValue().contains(tema)) {
                return temaEntry.getKey();
            }
        }
        return TEMA_UTEN_TEMAGRUPPE;
    }

}
