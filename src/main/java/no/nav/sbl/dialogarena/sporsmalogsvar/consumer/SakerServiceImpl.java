package no.nav.sbl.dialogarena.sporsmalogsvar.consumer;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.*;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.*;
import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Transformer;

import javax.inject.Inject;
import java.util.*;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.*;

public class SakerServiceImpl implements SakerService {

    public static final String TEMA_UTEN_TEMAGRUPPE = "Ukjent";

    @Inject
    private GsakService gsakService;
    @Inject
    private GsakKodeverk gsakKodeverk;
    @Inject
    private StandardKodeverk standardKodeverk;
    @Inject
    private LokaltKodeverk lokaltKodeverk;

    public SakerServiceImpl() {
    }

    @Override
    public Saker hentSaker(String fnr) {
        List<Sak> sakerForBruker = gsakService.hentSakerForBruker(fnr);
        on(sakerForBruker).forEach(new Closure<Sak>() {
            @Override
            public void execute(Sak sak) {
                String fagsystemnavn = gsakKodeverk.hentFagsystemMapping().get(sak.fagsystemKode);
                sak.fagsystemNavn = fagsystemnavn != null ? fagsystemnavn : sak.fagsystemKode;

                String temaNavn = standardKodeverk.getArkivtemaNavn(sak.temaKode);
                sak.temaNavn = temaNavn != null ? temaNavn : sak.temaKode;
            }
        });

        Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker = splittIGenerelleSakerOgIkkeGenerelleSaker(sakerForBruker);
        SakerListe sakerListeFagsak = getFagsaker(generelleOgIkkeGenerelleSaker);
        SakerListe sakerListeGenerelle = getGenerelleSaker(generelleOgIkkeGenerelleSaker);
        return new Saker(sakerListeFagsak, sakerListeGenerelle);
    }

    private Map<Boolean, List<Sak>> splittIGenerelleSakerOgIkkeGenerelleSaker(List<Sak> saker) {
        return on(saker).reduce(indexBy(IS_GENERELL_SAK));
    }

    private SakerListe getFagsaker(Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker) {
        List<Sak> fagsakerFraGodkjenteFagsystemer = on(generelleOgIkkeGenerelleSaker.get(false))
                .filter(IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK)
                .collect();
        return new SakerListe(grupperSakerPaaTema(fagsakerFraGodkjenteFagsystemer));
    }

    private SakerListe getGenerelleSaker(Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker) {
        List<Sak> generelleSakerFraGodkjentFagsystemMedKunGodkjenteTemaer = on(generelleOgIkkeGenerelleSaker.get(true))
                .filter(IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE)
                .filter(IS_GODKJENT_TEMA_FOR_GENERELLE)
                .collect();
        return new SakerListe(grupperSakerPaaTema(generelleSakerFraGodkjentFagsystemMedKunGodkjenteTemaer));
    }

    private List<SakerForTema> grupperSakerPaaTema(List<Sak> saker) {
        Map<String, List<Sak>> sakerGruppertPaaTema = on(saker).reduce(indexBy(TEMAKODE, new TreeMap<String, List<Sak>>()));
        return new ArrayList<>(on(sakerGruppertPaaTema.entrySet()).map(new Transformer<Map.Entry<String, List<Sak>>, SakerForTema>() {
            @Override
            public SakerForTema transform(Map.Entry<String, List<Sak>> entry) {
                return new SakerForTema(entry.getKey(), entry.getValue().get(0).temaNavn, finnTemaetsGruppe(entry.getKey()), entry.getValue());
            }
        }).collect());
    }

    private String finnTemaetsGruppe(String tema) {
        for (Map.Entry<String, List<String>> temaEntry : lokaltKodeverk.hentTemagruppeTemaMapping().entrySet()) {
            if (temaEntry.getValue().contains(tema)) {
                return temaEntry.getKey();
            }
        }
        return TEMA_UTEN_TEMAGRUPPE;
    }
}
