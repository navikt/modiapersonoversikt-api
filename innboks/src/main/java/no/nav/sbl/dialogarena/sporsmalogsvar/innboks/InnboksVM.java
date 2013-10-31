package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.nav.modig.lang.collections.TransformerUtils;
import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.melding.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.common.records.Record;
import no.nav.tjeneste.domene.brukerdialog.henvendelsemeldinger.v1.informasjon.WSMelding;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.NYESTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.WSMeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.innboks.MeldingVM.NYESTE_OVERST;

public class InnboksVM implements Serializable {

    private Map<String, List<MeldingVM>> traader = new HashMap<>();

    private List<MeldingVM> nyesteMeldingerITraad = new ArrayList<>();

    private Optional<MeldingVM> valgtMelding;

    public InnboksVM(List<WSMelding> meldinger) {
        oppdaterMeldinger(meldinger);
        valgtMelding = optional(nyesteMeldingerITraad.isEmpty() ? null : nyesteMeldingerITraad.get(0));
    }

    public final void oppdaterMeldinger(List<WSMelding> meldinger) {
        Map<String, List<WSMelding>> wsMeldingTraader = skillUtTraader(meldinger);
        for (Map.Entry<String, List<WSMelding>> wsMeldingTraad : wsMeldingTraader.entrySet()) {
            traader.put(wsMeldingTraad.getKey(), TIL_MELDINGVM_TRAAD.transform(wsMeldingTraad.getValue()));
        }
        nyesteMeldingerITraad = on(traader.values()).map(TransformerUtils.<MeldingVM>elementAt(0)).collect(NYESTE_OVERST);
    }

    public List<MeldingVM> getNyesteMeldingerITraad() {
        return nyesteMeldingerITraad;
    }

    public List<MeldingVM> getValgtTraad() {
        return valgtMelding.isSome() ? traader.get(valgtMelding.get().getTraadId()) : new ArrayList<MeldingVM>();
    }

    public String getValgtTraadTema() {
        return valgtMelding.isSome() ? valgtMelding.get().getTema() : null;
    }

    public int getTraadLengde(String id) {
        return traader.get(id).size();
    }

    public List<MeldingVM> getTidligereMeldinger() {
        List<MeldingVM> traad = getValgtTraad();
        return traad.isEmpty() ? new ArrayList<MeldingVM>() : traad.subList(1, traad.size());
    }

    public MeldingVM getNyesteMelding() {
        List<MeldingVM> traad = getValgtTraad();
        return traad.isEmpty() ? null : traad.get(0);
    }

    public Optional<MeldingVM> getValgtMelding() {
        return valgtMelding;
    }

    public void setValgtMelding(String id) {
        valgtMelding = on(nyesteMeldingerITraad).filter(where(ID, equalTo(id))).head();
    }

    public void setValgtMelding(MeldingVM melding) {
        valgtMelding = optional(melding);
    }

    public final IModel<Boolean> erValgtMelding(final MeldingVM meldingVM) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return valgtMelding.isSome() && valgtMelding.get() == meldingVM;
            }
        };
    }

    private static final Transformer<MeldingVM, String> ID = new Transformer<MeldingVM, String>() {
        @Override
        public String transform(MeldingVM meldingVM) {
            return meldingVM.getTraadId();
        }
    };

    private static final Transformer<List<WSMelding>, List<MeldingVM>> TIL_MELDINGVM_TRAAD = new Transformer<List<WSMelding>, List<MeldingVM>>() {
        @Override
        public List<MeldingVM> transform(List<WSMelding> wsMeldinger) {
            List<Record<Melding>> meldingerITraad = on(wsMeldinger).map(TIL_MELDING).collect(NYESTE_FORST);
            List<MeldingVM> meldingVMTraad = new ArrayList<>();
            for (Record<Melding> melding : meldingerITraad) {
                meldingVMTraad.add(new MeldingVM(meldingerITraad, melding.get(Melding.id)));
            }
            return meldingVMTraad;
        }
    };

}
