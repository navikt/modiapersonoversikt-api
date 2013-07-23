package no.nav.sbl.dialogarena.sporsmalogsvar.panel;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import org.apache.wicket.model.AbstractWrapModel;
import org.apache.wicket.model.IModel;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding.harTraadId;

public class MeldingstraadModel extends AbstractWrapModel<List<Melding>> {
    private final IModel<? extends List<Melding>> model;

    private final IModel<Melding> valgtMeldingModel;

    @Override
    public List<Melding> getObject() {
        String valgtTraad = getTraadId();
        if (valgtTraad != null) {
            return on(model.getObject()).filter(harTraadId(valgtTraad)).collect(nyesteNederst);
        }
        return Arrays.asList();
    }

    public MeldingstraadModel(final IModel<Melding> valgtMeldingModel, final IModel<? extends List<Melding>> model) {
        this.model = model;
        this.valgtMeldingModel = valgtMeldingModel;
    }

    @Override
    public void detach() {
        model.detach();
    }

    private static Comparator<Melding> nyesteNederst = new Comparator<Melding>() {
        public int compare(Melding o1, Melding o2) {
            return Long.valueOf(o1.id, Character.MAX_RADIX).compareTo(Long.valueOf(o2.id, Character.MAX_RADIX));
        }
    };

    @Override
    public IModel<?> getWrappedModel() {
        return model;
    }

    private String getTraadId() {
        if (valgtMeldingModel.getObject() != null) {
            return valgtMeldingModel.getObject().traadId;
        }
        return null;
    }
}
