package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
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
        Map<String, List<Sak>> sakerGruppertPaaSakstype = hentUtAlleSakerMedRiktigSakstype(sakerForBruker);
        temaSakerListeFagsak = new TemaSakerListe(grupperSakerPaaTema(sakerGruppertPaaSakstype.get(SAKSTYPE_FAG)));
        temaSakerListeGenerelle = new TemaSakerListe(grupperSakerPaaTema(sakerGruppertPaaSakstype.get(SAKSTYPE_GENERELL)));
    }

    private Map<String, List<Sak>> hentUtAlleSakerMedRiktigSakstype(List<Sak> saker) {
        Map<String, List<Sak>> sakerGruppertPaaSakstype = new HashMap<>();
        List<Sak> fagsaker = new ArrayList<>();
        List<Sak> generelleSaker = new ArrayList<>();
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

    private List<TemaSaker> grupperSakerPaaTema(List<Sak> saker) {
        List<TemaSaker> temaSakerListe = new ArrayList<>();
        Map<String, List<Sak>> sakerGruppertPaaTema = on(saker).reduce(indexBy(TEMA, new TreeMap<String, List<Sak>>()));
        for(String key : sakerGruppertPaaTema.keySet()){
            temaSakerListe.add(new TemaSaker(key, finnTemaetsGruppe(key), sakerGruppertPaaTema.get(key)));
        }
        return temaSakerListe;
    }

    private static String finnTemaetsGruppe(String tema){
        for(String key : temaMapping.keySet()){
            if(temaMapping.get(key).contains(tema)){
                return key;
            }
        }
        return TEMA_UTEN_TEMAGRUPPE;
    }

    public List<TemaSaker> getFagsakerGruppertPaaTema() {
        return temaSakerListeFagsak.sorter(innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
    }

    public List<TemaSaker> getGenerelleSakerGruppertPaaTema() {
        return temaSakerListeGenerelle.sorter(innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
    }

}
