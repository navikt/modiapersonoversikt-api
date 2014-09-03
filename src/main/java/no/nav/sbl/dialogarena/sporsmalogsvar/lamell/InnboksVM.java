package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.domain.Melding;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import javax.inject.Inject;
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
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.TRAAD_ID;

public class InnboksVM implements Serializable {

    @Inject
    private HenvendelseBehandlingService henvendelseBehandlingService;

    private Map<String, TraadVM> traader = new HashMap<>();
    private List<MeldingVM> nyesteMeldingerITraad = new ArrayList<>();
    private Optional<MeldingVM> valgtMelding;
    private String fnr;

    public InnboksVM(String fnr) {
        Injector.get().inject(this);
        this.fnr = fnr;
        oppdaterMeldinger();
        valgtMelding = optional(nyesteMeldingerITraad.isEmpty() ? null : nyesteMeldingerITraad.get(0));
    }

    public String getFnr() {
        return fnr;
    }

    public final void oppdaterMeldinger() {
        traader.clear();
        List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(fnr);

        Map<String, List<Melding>> meldingTraader = skillUtTraader(meldinger);
        for (Map.Entry<String, List<Melding>> meldingTraad : meldingTraader.entrySet()) {
            traader.put(meldingTraad.getKey(), new TraadVM(TIL_MELDINGVM_TRAAD.transform(meldingTraad.getValue())));
        }
        nyesteMeldingerITraad = on(traader.values()).map(new Transformer<TraadVM, MeldingVM>() {
            @Override
            public MeldingVM transform(TraadVM traadVM) {
                return traadVM.getNyesteMelding();
            }
        }).collect(MeldingVM.NYESTE_FORST);
    }

    public int getTraadLengde(String id) {
        return traader.get(id).getTraadLengde();
    }

    public void setValgtMelding(String id) {
        setValgtMelding(on(nyesteMeldingerITraad).filter(where(ID, equalTo(id))).head().get());
    }

    public Optional<MeldingVM> getNyesteMeldingITraad(String traadId) {
        return on(nyesteMeldingerITraad).filter(where(TRAAD_ID, equalTo(traadId))).head();
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

    public TraadVM getValgtTraad() {
        return valgtMelding.isSome() ? traader.get(valgtMelding.get().melding.traadId) : new TraadVM(new ArrayList<MeldingVM>());
    }

    public MeldingVM getNyesteMeldingINyesteTraad() {
        return nyesteMeldingerITraad.get(0);
    }

    public Map<String, TraadVM> getTraader() {
        return traader;
    }

    private static final Transformer<List<Melding>, List<MeldingVM>> TIL_MELDINGVM_TRAAD = new Transformer<List<Melding>, List<MeldingVM>>() {
        @Override
        public List<MeldingVM> transform(List<Melding> meldinger) {
            List<Melding> meldingerITraad = on(meldinger).collect(Melding.NYESTE_FORST);
            List<MeldingVM> meldingVMTraad = new ArrayList<>();
            for (Melding melding : meldingerITraad) {
                meldingVMTraad.add(new MeldingVM(melding, meldingerITraad.size()));
            }
            return meldingVMTraad;
        }
    };

}