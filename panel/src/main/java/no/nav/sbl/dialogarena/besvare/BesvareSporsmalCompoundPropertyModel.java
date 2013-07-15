package no.nav.sbl.dialogarena.besvare;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class BesvareSporsmalCompoundPropertyModel extends CompoundPropertyModel<BesvareSporsmalVM> {

    public BesvareSporsmalCompoundPropertyModel(BesvareSporsmalVM besvareSporsmal) {
        super(besvareSporsmal);
    }

    private BesvareSporsmalVM besvareSporsmal() {
        return getObject();
    }

    public final IModel<Boolean> erSynlig() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return besvareSporsmal().erSynlig();
            }
        };
    }
}
