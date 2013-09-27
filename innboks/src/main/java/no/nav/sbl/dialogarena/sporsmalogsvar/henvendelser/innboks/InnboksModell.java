package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelsetype;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class InnboksModell extends CompoundPropertyModel<InnboksVM> {

    public InnboksModell(InnboksVM innboks) {
        super(innboks);
    }

    public InnboksVM getInnboksVM() {
        return getObject();
    }

    public IModel<Boolean> ingenHenvendelser() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return InnboksModell.this.getObject().getHenvendelser().size() == 0;
            }
        };
    }

    public final IModel<Boolean> erValgtHenvendelse(final HenvendelseVM henvendelse) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<HenvendelseVM> valgtHenvendelse = getInnboksVM().getValgtHenvendelse();
                return valgtHenvendelse.isSome() && valgtHenvendelse.get() == henvendelse;
            }
        };
    }

    public IModel<Boolean> ingenHenvendelseValgt() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<HenvendelseVM> valgtHenvendelse = getInnboksVM().getValgtHenvendelse();
                return !valgtHenvendelse.isSome();
            }
        };
    }

    public IModel<Boolean> valgtHenvendelseAvType(final Henvendelsetype type) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                Optional<HenvendelseVM> valgtHenvendelse = getInnboksVM().getValgtHenvendelse();
                return valgtHenvendelse.isSome() && valgtHenvendelse.get().henvendelse.type == type;
            }
        };
    }

    public CompoundPropertyModel<Boolean> alleHenvendelserSkalSkjulesHvisLitenSkjerm = new CompoundPropertyModel<>(false);
}
