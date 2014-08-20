package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

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
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.TEMA;

public class SakerVM implements Serializable {

    public static final Map<String, List<String>> TEMA_MAPPING = opprettTemaMapping();
    public static final String TEMA_UTEN_TEMAGRUPPE = "Ukjent";
    static final List<String> GODKJENTE_TEMA_FOR_GENERELLE = asList("AGR", "FUL", "GEN", "KTR", "STO", "SER", "SIK", "SYM", "TRK", "TRY", "VEN");
    static final List<String> GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER = asList("AO01", "IT01", "OEBS", "V2", "AO11");
    static final String GODKJENT_FAGSYSTEM_FOR_GENERELLE = "FS22";

    private TemaSakerListe temaSakerListeFagsak;
    private TemaSakerListe temaSakerListeGenerelle;

    private InnboksVM innboksVM;
    private GsakService gsakService;

    // TODO: Dette er en midlertidig mapping mellom temagruppe og tema, mens vi venter på kodeverk.
    private static Map<String, List<String>> opprettTemaMapping() {
        Map<String, List<String>> temaMapping = new HashMap<>();
        temaMapping.put("ARBD", asList("DAG", "AAP", "FOS", "IND", "OPP", "SYK", "SYM", "VEN", "YRK"));
        temaMapping.put("FMLI", asList("FOR", "BAR", "BID", "ENF", "GRA", "GRU", "KON", "OMS"));
        temaMapping.put("HJLPM", asList("BIL", "HEL", "HJE", "MOB"));
        temaMapping.put("OVRG", asList("FUL", "MED", "SER", "TRK"));
        temaMapping.put("PENS", asList("PEN", "UFO"));
        return temaMapping;
    }

    public SakerVM(InnboksVM innboksVM, GsakService gsakService) {
        this.innboksVM = innboksVM;
        this.gsakService = gsakService;
    }

    public final void oppdater() {
        List<Sak> sakerForBruker = gsakService.hentSakerForBruker(innboksVM.getFnr());
        Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker = splittIGenerelleSakerOgIkkeGenerelleSaker(sakerForBruker);
        List<Sak> fagsakerFraGodkjenteFagsystemer = on(generelleOgIkkeGenerelleSaker.get(false))
                .filter(IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK)
                .collect();
        temaSakerListeFagsak = new TemaSakerListe(grupperSakerPaaTema(fagsakerFraGodkjenteFagsystemer));

        List<Sak> generelleSakerFraGodkjentFagsystemMedKunGodkjenteTemaer = on(generelleOgIkkeGenerelleSaker.get(true))
                .filter(IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE)
                .filter(IS_GODKJENT_TEMA_FOR_GENERELLE)
                .collect();
        temaSakerListeGenerelle = new TemaSakerListe(grupperSakerPaaTema(generelleSakerFraGodkjentFagsystemMedKunGodkjenteTemaer));
    }

    private static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENTE_FAGSYSTEMER_FOR_FAGSAKER.contains(sak.fagsystem);
        }
    };

    private static final Predicate<Sak> IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENT_FAGSYSTEM_FOR_GENERELLE.equals(sak.fagsystem);
        }
    };

    private static final Predicate<Sak> IS_GODKJENT_TEMA_FOR_GENERELLE = new Predicate<Sak>() {
        @Override
        public boolean evaluate(Sak sak) {
            return GODKJENTE_TEMA_FOR_GENERELLE.contains(sak.tema);
        }
    };

    private Map<Boolean, List<Sak>> splittIGenerelleSakerOgIkkeGenerelleSaker(List<Sak> saker) {
        return on(saker).reduce(indexBy(IS_GENERELL_SAK));
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
