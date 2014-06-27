package no.nav.sbl.dialogarena.sporsmalogsvar.lamell.journalforing;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Saksgruppe;
import no.nav.sbl.dialogarena.sporsmalogsvar.lamell.InnboksVM;
import org.apache.commons.collections15.Transformer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.ReduceUtils.indexBy;
import static no.nav.sbl.dialogarena.sporsmalogsvar.domain.Sak.FAGOMRADE;

public class SakerVM implements Serializable {

    private InnboksVM innboksVM;

    private Map<String, List<Sak>> saksgrupper;

    private MeldingService meldingService;

    public SakerVM(InnboksVM innboksVM, MeldingService meldingService) {
        this.innboksVM = innboksVM;
        this.meldingService = meldingService;
        oppdater();
    }

    public void oppdater() {
        saksgrupper = grupperSakerPaaTema(meldingService.hentSakerForBruker(innboksVM.getFnr()));
    }

    private Map<String, List<Sak>> grupperSakerPaaTema(List<Sak> saker) {
        return on(saker).reduce(indexBy(FAGOMRADE, new TreeMap<String, List<Sak>>()));
    }

    public List<Saksgruppe> getSaksgruppeliste() {
        return on(saksgrupper.keySet()).map(new Transformer<String, Saksgruppe>() {
            @Override
            public Saksgruppe transform(String tema) {
                return new Saksgruppe(tema, saksgrupper.get(tema));
            }
        }).collect();
    }

}
