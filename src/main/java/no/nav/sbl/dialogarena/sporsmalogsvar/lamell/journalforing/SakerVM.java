package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.*;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GENERELL_SAK;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.TEMA;

public class SakerVM implements Serializable {

    public final static Map<String, List<String>> temaMapping = opprettTemaMapping();

    public final static String TEMA_UTEN_TEMAGRUPPE = "Ukjent";

    private InnboksVM innboksVM;

    private MeldingService meldingService;

    private TemaSakerListe temaSakerListeFagsak;

    private TemaSakerListe temaSakerListeGenerelle;

    // TODO: Dette er en midlertidig mapping mellom temagruppe og tema, mens vi venter på kodeverk.
    private static Map<String, List<String>> opprettTemaMapping() {
        Map<String, List<String>> temaMapping = new HashMap<>();
        temaMapping.put("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", new ArrayList<>(
                Arrays.asList("Dagpenger", "Arbeidsavklaring")));
        temaMapping.put("FAMILIE_OG_BARN", new ArrayList<>(
                Arrays.asList("Foreldrepenger","Barnebidrag")));
        temaMapping.put("HJELPEMIDLER", new ArrayList<>(
                Arrays.asList("Hjelpemiddel", "Bilsøknad")));
        temaMapping.put("OVRIGE_HENVENDELSER", new ArrayList<>(
                Arrays.asList("Øvrige henvendelser","Annen øvrig hendelse")));
        return temaMapping;
    }

    public SakerVM(InnboksVM innboksVM, MeldingService meldingService) {
        this.innboksVM = innboksVM;
        this.meldingService = meldingService;
        oppdater();
    }

    public void oppdater() {
        List<Sak> sakerForBruker = meldingService.hentSakerForBruker(innboksVM.getFnr());
        Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker = splittIGenerelleSakerOgIkkeGenerelleSaker(sakerForBruker);
        temaSakerListeFagsak = new TemaSakerListe(grupperSakerPaaTema(generelleOgIkkeGenerelleSaker.get(false)));
        temaSakerListeGenerelle = new TemaSakerListe(grupperSakerPaaTema(generelleOgIkkeGenerelleSaker.get(true)));
    }

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

    private static final Transformer<Map.Entry<String, List<Sak>>, TemaSaker> TIL_TEMASAKER = new Transformer<Map.Entry<String, List<Sak>>, TemaSaker>(){
        @Override
        public TemaSaker transform(Map.Entry<String, List<Sak>> entry) {
            return new TemaSaker(entry.getKey(), finnTemaetsGruppe(entry.getKey()), entry.getValue());
        }
    };

    private static String finnTemaetsGruppe(String tema){
        for(String key : temaMapping.keySet()){
            if(temaMapping.get(key).contains(tema)){
                return key;
            }
        }
        return TEMA_UTEN_TEMAGRUPPE;
    }

}
