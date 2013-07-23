package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.wicket.model.LoadableDetachableModel;

import java.util.List;

public class AlleMeldingerModel extends LoadableDetachableModel<List<Melding>> {

    private String fodselsnummer;

    private MeldingService meldingService;

    public AlleMeldingerModel(final String fodselsnummer, final MeldingService meldingService) {
        this.fodselsnummer = fodselsnummer;
        this.meldingService = meldingService;
    }

    public AlleMeldingerModel(final List<Melding> meldinger, String fodselsnummer, final MeldingService meldingService) {
        super(meldinger);
        this.fodselsnummer = fodselsnummer;
        this.meldingService = meldingService;
    }

    @Override
    protected List<Melding> load() {
        return meldingService.hentAlleMeldinger(fodselsnummer);
    }
}
