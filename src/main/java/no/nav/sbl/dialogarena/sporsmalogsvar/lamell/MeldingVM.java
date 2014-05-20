package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Status;
import no.nav.sbl.dialogarena.time.Datoformat;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding.ELDSTE_FORST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype.INNGAENDE;


public class MeldingVM implements Serializable {

    private String id;
    private String traadId;
    private String tema;
    private String avsender;
    private String fritekst;

    private Meldingstype type;
    public final DateTime opprettetDato, lestDato;
    private boolean lest;
    private int antallMeldingerITraad;
    private Status status;

    public MeldingVM(List<Melding> tilhorendeTraad, Melding melding) {
        this.id = melding.id;
        traadId = melding.traadId;
        tema = melding.tema;
        type = melding.meldingstype;
        avsender = (melding.equals(on(tilhorendeTraad).collect(ELDSTE_FORST).get(0)) ? "Melding" : "Svar") + " fra " +
                (type == INNGAENDE ? "Bruker" : "NAV");
        fritekst = melding.fritekst;
        opprettetDato = melding.opprettetDato;
        lestDato = melding.lestDato;
        lest = lestDato != null;
        antallMeldingerITraad = tilhorendeTraad.size();
        status = melding.status;
    }

    public String getId() {
        return id;
    }

    public int getAntallMeldingerITraad() {
        return antallMeldingerITraad;
    }

    public String getOpprettetDato() {
        return Datoformat.langMedTid(opprettetDato);
    }

    public String getLestDato() {
        return Datoformat.kortMedTid(lestDato);
    }

    public String getTraadId() {
        return traadId;
    }

    public String getTema() {
        return tema;
    }

    public String getAvsender() {
        return avsender;
    }

    public String getFritekst() {
        return fritekst;
    }

    public Meldingstype getType() {
        return type;
    }

    public IModel<Boolean> erLest() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return lest;
            }
        };
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusKlasse() {
        return "indikator-dot " + status.toString().toLowerCase().replace("_", "-");
    }

    public static final Comparator<MeldingVM> NYESTE_OVERST = new Comparator<MeldingVM>() {
        @Override
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o2.opprettetDato.compareTo(o1.opprettetDato);
        }
    };

    public static final Transformer<MeldingVM, String> ID = new Transformer<MeldingVM, String>() {
        @Override
        public String transform(MeldingVM meldingVM) {
            return meldingVM.getId();
        }
    };
}
