package no.nav.sbl.dialogarena.sporsmalogsvar.melding;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Meldingstype;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class MeldingVM implements Serializable {
    private final Melding melding;

    public MeldingVM(Melding melding) {
        this.melding = melding;
    }

    public String getId() {
        return melding.id;
    }

    public String getFritekst() {
        return melding.fritekst;
    }

    public String getTraadId() {
        return melding.traadId;
    }

    public String getOverskrift() {
        String tekst;
        if (isBlank(melding.overskrift)) {
            tekst = melding.erSporsmal() ? "Spørsmål om " + melding.tema : "Svar fra NAV";
        } else {
            tekst = melding.overskrift;
        }
        return tekst;
    }

    public String getTema() {
        return melding.tema;
    }

    public boolean erSvar() {
        return melding.type == Meldingstype.SVAR;
    }

    public boolean erSporsmal() {
        return melding.type == Meldingstype.SPORSMAL;
    }

    public String getOpprettetDato() {
        return DateTimeFormat.forPattern("dd.MM.yyyy")
                .withLocale(Locale.getDefault())
                .print(melding.opprettet);
    }

    public DateTime getOpprettet() {
        return melding.opprettet;
    }

    public void setFritekst(String fritekst) {
        melding.fritekst = fritekst;
    }

    public static final Transformer<Melding, MeldingVM> TIL_MELDING_VM = new Transformer<Melding, MeldingVM>() {
        @Override
        public MeldingVM transform(Melding melding) {
            return new MeldingVM(melding);
        }
    };

    public static Predicate<MeldingVM> harTraadId(final String traadId) {
        return new Predicate<MeldingVM>() {
            @Override
            public boolean evaluate(MeldingVM melding) {
                return traadId.equals(melding.getTraadId());
            }
        };
    }

    public static final Comparator<MeldingVM> NYESTE_NEDERST = new Comparator<MeldingVM>() {
        public int compare(MeldingVM o1, MeldingVM o2) {
            return o1.getOpprettet().compareTo(o2.getOpprettet());
        }
    };
}
