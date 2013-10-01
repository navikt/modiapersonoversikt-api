package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelse;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelsetype;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelsetype.SPORSMAL;
import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelsetype.SVAR;


public class HenvendelseVM implements Serializable {

    public final Henvendelse henvendelse;

    public HenvendelseVM(Henvendelse henvendelse) {
        this.henvendelse = henvendelse;
    }

    public String getOpprettetDato() {
        return formatertDato(henvendelse.opprettet, "EEEEE dd.MM.yyyy 'kl' HH:mm").getObject();
    }

    public String getLestDato() {
        return avType(SVAR).getObject() ? formatertDato(henvendelse.lestDato, "'Lest:' dd.MM.yyyy 'kl' HH:mm").getObject() : null;
    }

    public IModel<String> formatertDato(final DateTime dato, final String format) {
        return new AbstractReadOnlyModel<String>() {
            @Override
            public String getObject() {
                return dato == null ? null :
                        DateTimeFormat.forPattern(format)
                                .withLocale(new Locale("nb"))
                                .print(dato);
            }
        };
    }

    public IModel<Boolean> avType(final Henvendelsetype type) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return henvendelse.type == type;
            }
        };
    }

    public IModel<Boolean> erLest() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return henvendelse.erLest();
            }
        };
    }

    public IModel<Boolean> erIkkeBesvart() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return henvendelse.erLest() && avType(SPORSMAL).getObject();
            }
        };
    }

    public static final Transformer<Henvendelse, HenvendelseVM> TIL_HENVENDELSE_VM = new Transformer<Henvendelse, HenvendelseVM>() {
        @Override
        public HenvendelseVM transform(Henvendelse henvendelse) {
            return new HenvendelseVM(henvendelse);
        }
    };

    public static final Transformer<HenvendelseVM, String> TRAAD_ID = new Transformer<HenvendelseVM, String>() {
        @Override
        public String transform(HenvendelseVM henvendelseVM) {
            return henvendelseVM.henvendelse.traadId;
        }
    };

    public static final Transformer<HenvendelseVM, String> ID = new Transformer<HenvendelseVM, String>() {
        @Override
        public String transform(HenvendelseVM henvendelseVM) {
            return henvendelseVM.henvendelse.id;
        }
    };

    public static final Comparator<HenvendelseVM> NYESTE_OVERST = new Comparator<HenvendelseVM>() {
        public int compare(HenvendelseVM m1, HenvendelseVM m2) {
            return m2.henvendelse.opprettet.compareTo(m1.henvendelse.opprettet);
        }
    };

}
