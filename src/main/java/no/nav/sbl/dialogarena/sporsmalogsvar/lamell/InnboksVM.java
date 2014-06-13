package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.collections.TransformerUtils;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.MeldingService;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status.IKKE_BESVART;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.ID;

public class InnboksVM implements Serializable {

    private MeldingService meldingService;

    private Map<String, List<MeldingVM>> traader = new HashMap<>();

    private List<MeldingVM> nyesteMeldingerITraad = new ArrayList<>();

    private Optional<MeldingVM> valgtMelding;

    private String fnr;

    public InnboksVM(MeldingService meldingService, String fnr) {
        this.meldingService = meldingService;
        this.fnr = fnr;
        oppdaterMeldinger();
        valgtMelding = optional(nyesteMeldingerITraad.isEmpty() ? null : nyesteMeldingerITraad.get(0));
    }

    public final void oppdaterMeldinger() {
        List<Melding> meldinger = meldingService.hentMeldinger(fnr);
        Map<String, List<Melding>> meldingTraader = skillUtTraader(meldinger);
        for (Map.Entry<String, List<Melding>> meldingTraad : meldingTraader.entrySet()) {
            traader.put(meldingTraad.getKey(), TIL_MELDINGVM_TRAAD.transform(meldingTraad.getValue()));
        }
        nyesteMeldingerITraad = on(traader.values()).map(TransformerUtils.<MeldingVM>elementAt(0)).collect(MeldingVM.NYESTE_FORST);
    }

    public final String getValgtTraadTema() {
        return valgtMelding.isSome() ? valgtMelding.get().melding.tema : null;
    }

    public int getTraadLengde(String id) {
        return traader.get(id).size();
    }

    public MeldingVM getNyesteMelding() {
        List<MeldingVM> valgtTraad = getValgtTraad();
        return valgtTraad.isEmpty() ? null : valgtTraad.get(0);
    }

    public Boolean valgtMeldingErEtUbesvartSporsmaal() {
        MeldingVM nyesteMelding = getNyesteMelding();
        return nyesteMelding != null && nyesteMelding.melding.status == IKKE_BESVART;
    }

    public List<MeldingVM> getTidligereMeldinger() {
        List<MeldingVM> valgtTraad = getValgtTraad();
        return valgtTraad.isEmpty() ? new ArrayList<MeldingVM>() : valgtTraad.subList(1, valgtTraad.size());
    }

    public void setValgtMelding(String id) {
        setValgtMelding(on(nyesteMeldingerITraad).filter(where(ID, equalTo(id))).head().get());
    }

    public void setValgtMelding(MeldingVM meldingVM) {
        valgtMelding = optional(meldingVM);
    }

    public final IModel<Boolean> erValgtMelding(final MeldingVM meldingVM) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return valgtMelding.isSome() && valgtMelding.get().equals(meldingVM);
            }
        };
    }

    private List<MeldingVM> getValgtTraad() {
        return valgtMelding.isSome() ? traader.get(valgtMelding.get().melding.traadId) : new ArrayList<MeldingVM>();
    }

    private static final Transformer<List<Melding>, List<MeldingVM>> TIL_MELDINGVM_TRAAD = new Transformer<List<Melding>, List<MeldingVM>>() {
        @Override
        public List<MeldingVM> transform(List<Melding> meldinger) {
            List<Melding> meldingerITraad = on(meldinger).collect(Melding.NYESTE_FORST);
            List<MeldingVM> meldingVMTraad = new ArrayList<>();
            for (Melding melding : meldingerITraad) {
                meldingVMTraad.add(new MeldingVM(meldingerITraad, melding));
            }
            return meldingVMTraad;
        }
    };
}
