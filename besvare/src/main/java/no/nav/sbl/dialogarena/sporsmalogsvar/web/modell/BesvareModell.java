package no.nav.sbl.dialogarena.sporsmalogsvar.web.modell;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class BesvareModell extends CompoundPropertyModel<BesvareVM> {
	
    public BesvareModell() {
        this(new BesvareVM());
    }
    
    public BesvareModell(BesvareVM sos) {
        super(sos);
    }
    
    public SvarVM getSvar() {
        return getObject().svar;
    }
    
    public void nullstill() {
        setObject(new BesvareVM());
    }

    public final IModel<Boolean> besvarerSporsmal() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return getSvar().behandlingsId != null;
            }
        };
    }
}
