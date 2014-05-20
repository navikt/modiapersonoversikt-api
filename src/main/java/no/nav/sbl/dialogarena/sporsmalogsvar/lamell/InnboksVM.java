package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.collections.TransformerUtils;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
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
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding.NYESTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.NYESTE_OVERST;

public class InnboksVM implements Serializable {

    private TraadVM traadVM;
    private Map<String, List<MeldingVM>> traader = new HashMap<>();

    private List<MeldingVM> nyesteMeldingerITraad = new ArrayList<>();

    private Optional<MeldingVM> valgtMelding;

    public InnboksVM(List<Melding> meldinger) {
        oppdaterMeldinger(meldinger);
        valgtMelding = optional(nyesteMeldingerITraad.isEmpty() ? null : nyesteMeldingerITraad.get(0));
        traadVM = new TraadVM(getValgtTraadTema(), null);
        for (MeldingVM meldingVm : getValgtTraad()) {
            Melding melding = new Melding(meldingVm.getId(), meldingVm.getTraadId(), meldingVm.getType(), meldingVm.opprettetDato, meldingVm.getFritekst());
            traadVM.leggTil(melding);
        }
    }

    public final void oppdaterMeldinger(List<Melding> meldinger) {
        Map<String, List<Melding>> meldingTraader = skillUtTraader(meldinger);
        for (Map.Entry<String, List<Melding>> meldingTraad : meldingTraader.entrySet()) {
            traader.put(meldingTraad.getKey(), TIL_MELDINGVM_TRAAD.transform(meldingTraad.getValue()));
        }
        nyesteMeldingerITraad = on(traader.values()).map(TransformerUtils.<MeldingVM>elementAt(0)).collect(NYESTE_OVERST);
    }

    public List<MeldingVM> getNyesteMeldingerITraad() {
        return nyesteMeldingerITraad;
    }

    public final List<MeldingVM> getValgtTraad() {
        return valgtMelding.isSome() ? traader.get(valgtMelding.get().getTraadId()) : new ArrayList<MeldingVM>();
    }


    public final String getValgtTraadTema() {
        return valgtMelding.isSome() ? valgtMelding.get().getTema() : null;
    }

    public int getTraadLengde(String id) {
        return traader.get(id).size();
    }

    public List<MeldingVM> getTidligereMeldinger() {
        List<MeldingVM> valgtTraad = getValgtTraad();
        return valgtTraad.isEmpty() ? new ArrayList<MeldingVM>() : valgtTraad.subList(1, valgtTraad.size());
    }

    public MeldingVM getNyesteMelding() {
        List<MeldingVM> valgtTraad = getValgtTraad();
        return valgtTraad.isEmpty() ? null : valgtTraad.get(0);
    }

    public Optional<MeldingVM> getValgtMelding() {
        return valgtMelding;
    }

    public void setValgtMelding(String id) {
        setValgtMelding(on(nyesteMeldingerITraad).filter(where(ID, equalTo(id))).head().get());
    }

    public void setValgtMelding(MeldingVM meldingVM) {
        valgtMelding = optional(meldingVM);
        traadVM = new TraadVM(getValgtTraadTema(), null);
        for (MeldingVM valgtMeldingVM : getValgtTraad()) {
            Melding melding = new Melding(valgtMeldingVM.getId(), valgtMeldingVM.getTraadId(), valgtMeldingVM.getType(), valgtMeldingVM.opprettetDato, valgtMeldingVM.getFritekst());
            traadVM.leggTil(melding);
        }
    }

    public final IModel<Boolean> erValgtMelding(final MeldingVM meldingVM) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return valgtMelding.isSome() && valgtMelding.get() == meldingVM;
            }
        };
    }

    private static final Transformer<List<Melding>, List<MeldingVM>> TIL_MELDINGVM_TRAAD = new Transformer<List<Melding>, List<MeldingVM>>() {
        @Override
        public List<MeldingVM> transform(List<Melding> meldinger) {
            List<Melding> meldingerITraad = on(meldinger).collect(NYESTE_FORST);
            List<MeldingVM> meldingVMTraad = new ArrayList<>();
            for (Melding melding : meldingerITraad) {
                meldingVMTraad.add(new MeldingVM(meldingerITraad, melding));
            }
            return meldingVMTraad;
        }
    };

}
