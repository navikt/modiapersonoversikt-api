package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.haandtermelding.journalforing;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.GsakKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.LokaltKodeverk;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.service.StandardKodeverk;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.GsakService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.TemaSaker;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.commons.collections15.Closure;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Map.Entry;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GENERELL_SAK;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GODKJENT_FAGSYSTEM_FOR_FAGSAK;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GODKJENT_FAGSYSTEM_FOR_GENERELLE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.IS_GODKJENT_TEMA_FOR_GENERELLE;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.TEMAKODE;

public class SakerVM implements Serializable {

    public static final String TEMA_UTEN_TEMAGRUPPE = "Ukjent";

    public final IModel<Boolean> visFagsaker = Model.of(true);
    public final IModel<Boolean> visGenerelleSaker = Model.of(false);

    private TemaSakerListe temaSakerListeFagsak;
    private TemaSakerListe temaSakerListeGenerelle;
    private InnboksVM innboksVM;

    @Inject
    private GsakService gsakService;
    @Inject
    private GsakKodeverk gsakKodeverk;
    @Inject
    private StandardKodeverk standardKodeverk;
    @Inject
    private LokaltKodeverk lokaltKodeverk;

    public SakerVM(InnboksVM innboksVM) {
        this.innboksVM = innboksVM;
        Injector.get().inject(this);
    }

    public final void oppdater() {
        List<Sak> sakerForBruker = gsakService.hentSakerForBruker(innboksVM.getFnr());
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
        temaSakerListeFagsak = getFagsaker(generelleOgIkkeGenerelleSaker);
        temaSakerListeGenerelle = getGenerelleSaker(generelleOgIkkeGenerelleSaker);

        visFagsaker.setObject(true);
        visGenerelleSaker.setObject(false);
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
        Map<String, List<Sak>> sakerGruppertPaaTema = on(saker).reduce(indexBy(TEMAKODE, new TreeMap<String, List<Sak>>()));
        return new ArrayList<>(on(sakerGruppertPaaTema.entrySet()).map(new Transformer<Entry<String, List<Sak>>, TemaSaker>() {
            @Override
            public TemaSaker transform(Entry<String, List<Sak>> entry) {
                return new TemaSaker(entry.getKey(), entry.getValue().get(0).temaNavn, finnTemaetsGruppe(entry.getKey()), entry.getValue());
            }
        }).collect());
    }

    public List<TemaSaker> getFagsakerGruppertPaaTema() {
        return temaSakerListeFagsak.sorter(innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
    }

    public List<TemaSaker> getGenerelleSakerGruppertPaaTema() {
        return temaSakerListeGenerelle.sorter(innboksVM.getValgtTraad().getEldsteMelding().melding.temagruppe);
    }

    private String finnTemaetsGruppe(String tema) {
        for (Entry<String, List<String>> temaEntry : lokaltKodeverk.hentTemagruppeTemaMapping().entrySet()) {
            if (temaEntry.getValue().contains(tema)) {
                return temaEntry.getKey();
            }
        }
        return TEMA_UTEN_TEMAGRUPPE;
    }
}
