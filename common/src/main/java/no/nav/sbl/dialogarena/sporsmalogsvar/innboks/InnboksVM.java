package no.nav.sbl.dialogarena.sporsmalogsvar.innboks;

import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM.TIL_MELDING_VM;
import static no.nav.sbl.dialogarena.sporsmalogsvar.melding.MeldingVM.harTraadId;

public class InnboksVM implements Serializable {

    private List<MeldingVM> meldinger;

    private MeldingVM valgtMelding;

    public InnboksVM(List<Melding> nyeMeldinger) {
        oppdaterMeldingerFra(nyeMeldinger);
    }

    public List<MeldingVM> getMeldinger() {
        return meldinger;
    }

    public List<MeldingVM> getTraad() {
        return on(meldinger).filter(harTraadId(valgtMelding.getTraadId())).collect(MeldingVM.NYESTE_NEDERST);
    }

    public final void oppdaterMeldingerFra(List<Melding> meldinger) {
        this.meldinger = on(meldinger).map(TIL_MELDING_VM).collect(nyesteOverst);
        this.valgtMelding = meldinger.isEmpty() ? new MeldingVM(new Melding()) : this.meldinger.get(0);
    }

    public MeldingVM getValgtMelding() {
        return valgtMelding;
    }

    public void setValgtMelding(MeldingVM valgtMelding) {
        this.valgtMelding = valgtMelding;
    }
    private static Comparator<MeldingVM> nyesteOverst = new Comparator<MeldingVM>() {
        public int compare(MeldingVM m1, MeldingVM m2) {
            return m2.getOpprettet().compareTo(m1.getOpprettet());
        }
    };
}
