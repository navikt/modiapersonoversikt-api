package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaMedSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;

import java.io.Serializable;
import java.util.*;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.TEMA;

public class SakerVM implements Serializable {

    public final static Map<String, List<String>> temaMapping = opprettTemaMapping();

    public final static String TEMA_UTEN_TEMAGRUPPE = "Ukjent";
    public final static String SAKSTYPE_GENERELL = "Generell";
    public final static String SAKSTYPE_FAG = "Fag";

    private InnboksVM innboksVM;

    private MeldingService meldingService;

    private List<TemaMedSaker> fagsakerGruppertPaaTema;

    private List<TemaMedSaker> generelleSakerGruppertPaaTema;

    // Dette er en midlertidig mapping mellom temagruppe og tema, mens vi venter på kodeverk.
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
        Map<String, List<Sak>> sakerGruppertPaaSakstype = hentUtAlleSakerMedRiktigSakstype(sakerForBruker);
        fagsakerGruppertPaaTema = grupperSakerPaaTema(sakerGruppertPaaSakstype.get(SAKSTYPE_FAG));
        generelleSakerGruppertPaaTema = grupperSakerPaaTema(sakerGruppertPaaSakstype.get(SAKSTYPE_GENERELL));
    }

    private Map<String, List<Sak>> hentUtAlleSakerMedRiktigSakstype(List<Sak> saker) {
        Map<String, List<Sak>> sakerGruppertPaaSakstype = new HashMap<>();
        ArrayList<Sak> fagsaker = new ArrayList<>();
        ArrayList<Sak> generelleSaker = new ArrayList<>();
        for (Sak sak : saker) {
            if (sak.isSakstypeForVisingGenerell()) {
                generelleSaker.add(sak);
            }
            else
                fagsaker.add(sak);
        }
        sakerGruppertPaaSakstype.put(SAKSTYPE_GENERELL, generelleSaker);
        sakerGruppertPaaSakstype.put(SAKSTYPE_FAG, fagsaker);
        return sakerGruppertPaaSakstype;
    }

    private List<TemaMedSaker> grupperSakerPaaTema(List<Sak> saker) {
        List<TemaMedSaker> temaMedSakerListe = new ArrayList<>();
        Map<String, List<Sak>> sakerGruppertPaaTema = on(saker).reduce(indexBy(TEMA, new TreeMap<String, List<Sak>>()));
        for(String key : sakerGruppertPaaTema.keySet()){
            temaMedSakerListe.add(new TemaMedSaker(key, finnTemaetsGruppe(key), sakerGruppertPaaTema.get(key)));
        }
        return temaMedSakerListe;
    }

    private static String finnTemaetsGruppe(String tema){
        for(String key : temaMapping.keySet()){
            if(temaMapping.get(key).contains(tema)){
                return key;
            }
        }
        return TEMA_UTEN_TEMAGRUPPE;
    }

    public List<TemaMedSaker> getFagsakerGruppertPaaTema() {
        return sorterTemaMedSakerListe(fagsakerGruppertPaaTema);
    }

    public List<TemaMedSaker> getGenerelleSakerGruppertPaaTema() {
        return sorterTemaMedSakerListe(generelleSakerGruppertPaaTema);
    }

    private List<TemaMedSaker> sorterTemaMedSakerListe(List<TemaMedSaker> alleTemaMedSaker) {
        String valgtTraadSinTemagruppe = innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe;
        List<TemaMedSaker> valgteTemaMedSaker = hentUtValgteTemaMedSaker(alleTemaMedSaker, valgtTraadSinTemagruppe);
        alleTemaMedSaker.removeAll(valgteTemaMedSaker);
        Collections.sort(valgteTemaMedSaker);
        Collections.sort(alleTemaMedSaker);
        valgteTemaMedSaker.addAll(alleTemaMedSaker);

        return sorterDatoInnenforSammeTema(valgteTemaMedSaker);
    }

    private List<TemaMedSaker> hentUtValgteTemaMedSaker(List<TemaMedSaker> alleTemaMedSaker, String valgtTraadSinTemagruppe) {
        List<TemaMedSaker> valgteTemaMedSaker = new ArrayList<>();
        for(TemaMedSaker temaMedSaker : alleTemaMedSaker){
            if(temaMedSaker.temagruppe.equals(valgtTraadSinTemagruppe)){
                valgteTemaMedSaker.add(temaMedSaker);
            }
        }
        return valgteTemaMedSaker;
    }

    private List<TemaMedSaker> sorterDatoInnenforSammeTema(List<TemaMedSaker> alleTemaMedSaker) {
        for (TemaMedSaker temaMedSaker : alleTemaMedSaker ) {
            Collections.sort(temaMedSaker.saksliste);
        }
        return alleTemaMedSaker;
    }

}
