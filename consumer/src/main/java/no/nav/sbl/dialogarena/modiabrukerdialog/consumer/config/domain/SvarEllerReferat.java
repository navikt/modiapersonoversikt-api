package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.config.domain;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Comparator;

public class SvarEllerReferat implements Serializable {

    public String sporsmalsId, fnr, navIdent, temagruppe, kanal, fritekst;
    public DateTime opprettetDato;
    public Henvendelsetype type;

    public enum Henvendelsetype {
        SVAR, REFERAT
    }

    public SvarEllerReferat withSporsmalsId(String sporsmalsId) {
        this.sporsmalsId = sporsmalsId;
        return this;
    }

    public SvarEllerReferat withType(Henvendelsetype type) {
        this.type = type;
        return this;
    }

    public SvarEllerReferat withFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public SvarEllerReferat withNavIdent(String navIdent) {
        this.navIdent = navIdent;
        return this;
    }

    public SvarEllerReferat withTemagruppe(String temagruppe) {
        this.temagruppe = temagruppe;
        return this;
    }

    public SvarEllerReferat withKanal(String kanal) {
        this.kanal = kanal;
        return this;
    }

    public SvarEllerReferat withFritekst(String fritekst) {
        this.fritekst = fritekst;
        return this;
    }

    public SvarEllerReferat withOpprettetDato(DateTime opprettetDato) {
        this.opprettetDato = opprettetDato;
        return this;
    }

    public static final Comparator<SvarEllerReferat> ELDSTE_FORST = new Comparator<SvarEllerReferat>() {
        @Override
        public int compare(SvarEllerReferat o1, SvarEllerReferat o2) {
            return o1.opprettetDato.compareTo(o2.opprettetDato);
        }
    };

}
