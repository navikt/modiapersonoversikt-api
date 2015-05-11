package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Saker;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.SakerForTema;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.SakerListe;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.kodeverk.StandardKodeverk;
import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.gsak.Sak.*;

public class SakerUtils {

    public static void leggTilFagsystemnavnOgTemanavn(List<Sak> sakerForBruker, final Map<String, String> fagsystemMapping, final StandardKodeverk standardKodeverk) {
        on(sakerForBruker).forEach(new Closure<Sak>() {
            @Override
            public void execute(Sak sak) {
                String fagsystemnavn = fagsystemMapping.get(sak.fagsystemKode);
                sak.fagsystemNavn = fagsystemnavn != null ? fagsystemnavn : sak.fagsystemKode;

                String temaNavn = standardKodeverk.getArkivtemaNavn(sak.temaKode);
                sak.temaNavn = temaNavn != null ? temaNavn : sak.temaKode;
            }
        });
    }

    public static Saker hentGenerelleOgIkkeGenerelleSaker(List<Sak> sakerForBruker) {
        Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker = separerGenerelleOgIkkeGenerelleSaker(sakerForBruker);
        SakerListe sakerListeFagsak = getFagsaker(generelleOgIkkeGenerelleSaker);
        SakerListe sakerListeGenerelle = getGenerelleSaker(generelleOgIkkeGenerelleSaker);
        return new Saker(sakerListeFagsak, sakerListeGenerelle);
    }

    private static Map<Boolean, List<Sak>> separerGenerelleOgIkkeGenerelleSaker(List<Sak> saker) {
        return on(saker).reduce(indexBy(IS_GENERELL_SAK));
    }

    private static SakerListe getFagsaker(Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker) {
        List<Sak> fagsakerFraGodkjenteFagsystemer = on(generelleOgIkkeGenerelleSaker.get(false))
                .filter(IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK)
                .collect();
        return new SakerListe(grupperSakerPaaTema(fagsakerFraGodkjenteFagsystemer));
    }

    private static SakerListe getGenerelleSaker(Map<Boolean, List<Sak>> generelleOgIkkeGenerelleSaker) {
        List<Sak> generelleSakerFraGodkjentFagsystemMedKunGodkjenteTemaer = on(generelleOgIkkeGenerelleSaker.get(true))
                .filter(IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE)
                .filter(IS_GODKJENT_TEMA_FOR_GENERELLE)
                .collect();
        return new SakerListe(grupperSakerPaaTema(generelleSakerFraGodkjentFagsystemMedKunGodkjenteTemaer));
    }

    private static List<SakerForTema> grupperSakerPaaTema(List<Sak> saker) {
        Map<String, List<Sak>> sakerGruppertPaaTema = on(saker).reduce(indexBy(TEMAKODE, new TreeMap<String, List<Sak>>()));
        return on(sakerGruppertPaaTema.entrySet()).map(new Transformer<Map.Entry<String, List<Sak>>, SakerForTema>() {
            @Override
            public SakerForTema transform(Map.Entry<String, List<Sak>> entry) {
                return new SakerForTema()
                        .withTemaKode(entry.getKey())
                        .withTemaNavn(entry.getValue().get(0).temaNavn)
                        .withSaksliste(entry.getValue());
            }
        }).collectIn(new ArrayList<SakerForTema>());
    }

}
