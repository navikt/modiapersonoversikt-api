package no.nav.sbl.dialogarena.sporsmalogsvar.besvare;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class BesvareModell extends CompoundPropertyModel<SporsmalOgSvarVM> {
	
    public BesvareModell() {
        this(new SporsmalOgSvarVM());
    }
    
    public BesvareModell(SporsmalOgSvarVM sos) {
        super(sos);
    }
    
    public SvarMeldingVM getSvar() {
        return getObject().svar;
    }
    
    public void nullstill() {
        setObject(new SporsmalOgSvarVM());
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
