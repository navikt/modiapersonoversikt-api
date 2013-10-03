package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.records.Record;
import no.nav.tjeneste.domene.brukerdialog.henvendelsefelles.v1.informasjon.WSHenvendelse;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.sort;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingUtils.NYESTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingUtils.TIL_MELDING;
import static no.nav.sbl.dialogarena.sporsmalogsvar.utils.WSHenvendelseUtils.skillUtTraader;

public class InnboksVM implements Serializable {

    private Map<String, List<MeldingVM>> traader = new HashMap<>();

    private List<MeldingVM> nyesteMeldingerITraad = new ArrayList<>();

    private Optional<MeldingVM> valgtMelding;

    public InnboksVM(List<WSHenvendelse> henvendelser) {
        oppdaterMeldinger(henvendelser);
        valgtMelding = optional(nyesteMeldingerITraad.isEmpty() ? null : nyesteMeldingerITraad.get(0));
    }

    public void oppdaterMeldinger(List<WSHenvendelse> henvendelser) {
        Map<String, List<WSHenvendelse>> wsHenvendelseTraader = skillUtTraader(henvendelser);
        for (Map.Entry<String, List<WSHenvendelse>> wsHenvendelseTraad : wsHenvendelseTraader.entrySet()) {
            traader.put(wsHenvendelseTraad.getKey(), TIL_MELDINGVM_TRAAD.transform(wsHenvendelseTraad.getValue()));
        }

        for (List<MeldingVM> traad : traader.values()) {
            nyesteMeldingerITraad.add(traad.get(0));
        }
        sort(nyesteMeldingerITraad, MeldingVM.NYESTE_OVERST);
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
            return meldingVM.getId();
        }
    };

    private static final Transformer<List<WSHenvendelse>, List<MeldingVM>> TIL_MELDINGVM_TRAAD = new Transformer<List<WSHenvendelse>, List<MeldingVM>>() {
        @Override
        public List<MeldingVM> transform(List<WSHenvendelse> wsHenvendelser) {
            List<Record<Melding>> meldingerITraad = on(wsHenvendelser).map(TIL_MELDING).collect(NYESTE_FORST);
            List<MeldingVM> meldingVMTraad = new ArrayList<>();
            for (Record<Melding> melding : meldingerITraad) {
                meldingVMTraad.add(new MeldingVM(meldingerITraad, melding.get(Melding.id)));
            }
            return meldingVMTraad;
        }
    };

}
