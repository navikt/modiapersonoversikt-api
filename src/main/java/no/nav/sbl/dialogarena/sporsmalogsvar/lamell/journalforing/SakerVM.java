package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaMedSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.TEMA;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaMedSaker.SAMMENLIGN_TEMA;

public class SakerVM implements Serializable {

    public final static Map<String, List<String>> temaMapping = opprettTemaMapping();

    public final static String TEMA_UTEN_TEMAGRUPPE = "Generelle";

    private InnboksVM innboksVM;

    private MeldingService meldingService;

    private Map<String, List<Sak>> sakerGruppertPaaTema;

    private List<TemaMedSaker> temaMedSakerListe;

    private static Map<String, List<String>> opprettTemaMapping() {
        Map<String, List<String>> temaMapping = new HashMap<>();
        temaMapping.put("ARBEIDSSOKER_ARBEIDSAVKLARING_SYKEMELDT", new ArrayList<>(
                Arrays.asList("Arbeidsavklaring", "Dagpenger")));
        temaMapping.put("FAMILIE_OG_BARN", new ArrayList<>(
                Arrays.asList("Foreldrepenger")));
        temaMapping.put("HJELPEMIDLER", new ArrayList<>(
                Arrays.asList("Hjelpemiddel", "Bilsøknad")));
        temaMapping.put("OVRIGE_HENVENDELSER", new ArrayList<>(
                Arrays.asList("Øvrige henvendelser")));
        return temaMapping;
    }

    public SakerVM(InnboksVM innboksVM, MeldingService meldingService) {
        this.innboksVM = innboksVM;
        this.meldingService = meldingService;
        oppdater();
    }

    public void oppdater() {
        sakerGruppertPaaTema = grupperSakerPaaTema(meldingService.hentSakerForBruker(innboksVM.getFnr()));
        temaMedSakerListe = new ArrayList<>(on(sakerGruppertPaaTema.keySet()).map(TIL_TEMA_MED_SAKER()).collect());
    }

    private Map<String, List<Sak>> grupperSakerPaaTema(List<Sak> saker) {
        return on(saker).reduce(indexBy(TEMA, new TreeMap<String, List<Sak>>()));
    }

    public List<TemaMedSaker> getSaksgruppeliste() {
        return sorterTemaMedSakerListe(temaMedSakerListe);
    }

    private List<TemaMedSaker> sorterTemaMedSakerListe(List<TemaMedSaker> alleTemaMedSaker) {
        String valgtTraadSinTemagruppe = innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe;
        List<TemaMedSaker> valgteTemaMedSaker = hentUtValgteTemaMedSaker(alleTemaMedSaker, valgtTraadSinTemagruppe);
        Collections.sort(valgteTemaMedSaker, SAMMENLIGN_TEMA);
        Collections.sort(alleTemaMedSaker, SAMMENLIGN_TEMA);
        valgteTemaMedSaker.addAll(alleTemaMedSaker);

        return valgteTemaMedSaker;
    }

    private List<TemaMedSaker> hentUtValgteTemaMedSaker(List<TemaMedSaker> alleTemaMedSaker, String valgtTraadSinTemagruppe) {
        List<TemaMedSaker> valgteTemaMedSaker = new ArrayList<>();
        for(TemaMedSaker temaMedSaker : alleTemaMedSaker){
            if(temaMedSaker.temagruppe.equals(valgtTraadSinTemagruppe)){
                valgteTemaMedSaker.add(temaMedSaker);
            }
        }
        for(TemaMedSaker temaMedSaker : valgteTemaMedSaker){
            alleTemaMedSaker.remove(temaMedSaker);
        }
        return valgteTemaMedSaker;
    }

    private Transformer<String, TemaMedSaker> TIL_TEMA_MED_SAKER() {
        return new Transformer<String, TemaMedSaker>() {
            @Override
            public TemaMedSaker transform(String tema) {
                return new TemaMedSaker(tema, finnTemaetsGruppe(tema), sakerGruppertPaaTema.get(tema));
            }
        };
    }

    private static String finnTemaetsGruppe(String tema){
        for(String key : temaMapping.keySet()){
            if(temaMapping.get(key).contains(tema)){
                return key;
            }
        }
        return TEMA_UTEN_TEMAGRUPPE;
    }

}
