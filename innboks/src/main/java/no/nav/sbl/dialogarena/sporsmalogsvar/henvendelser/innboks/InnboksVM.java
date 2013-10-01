package no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks;

import no.nav.modig.lang.option.Optional;
import no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.consumer.Henvendelse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.HenvendelseVM.ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.HenvendelseVM.NYESTE_OVERST;
import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.HenvendelseVM.TIL_HENVENDELSE_VM;
import static no.nav.sbl.dialogarena.sporsmalogsvar.henvendelser.innboks.HenvendelseVM.TRAAD_ID;

public class InnboksVM implements Serializable {

    private List<HenvendelseVM> henvendelser;

    private Optional<HenvendelseVM> valgtHenvendelse;

    public InnboksVM(List<Henvendelse> nyeHenvendelser) {
        oppdaterHenvendelserFra(nyeHenvendelser);
        valgtHenvendelse = optional(henvendelser.isEmpty() ? null : henvendelser.get(0));
    }

    public List<HenvendelseVM> getHenvendelser() {
        return henvendelser;
    }

    public List<HenvendelseVM> getNyesteHenvendelseITraad() {
        List<HenvendelseVM> nyesteHenvendelser = new ArrayList<>();
        for (String id : alleTraadIder()) {
            List<HenvendelseVM> henvendelserITraad = on(getHenvendelser()).filter(where(TRAAD_ID, equalTo(id))).collect(NYESTE_OVERST);
            nyesteHenvendelser.add(henvendelserITraad.get(0));
        }
        return on(nyesteHenvendelser).collect(NYESTE_OVERST);
    }

    private List<String> alleTraadIder() {
        List<String> traadIder = new ArrayList<>();
        for (String id : on(getHenvendelser()).map(TRAAD_ID)) {
            if (!traadIder.contains(id)) {
                traadIder.add(id);
            }
        }
        return traadIder;
    }

    public List<HenvendelseVM> getTraad() {
        for (HenvendelseVM henvendelseVM : valgtHenvendelse) {
            return on(henvendelser).filter(where(TRAAD_ID, equalTo(henvendelseVM.henvendelse.traadId))).collect(NYESTE_OVERST);
        }
        return emptyList();
    }

    public int getTraadLengde(String id) {
        return on(henvendelser).filter(where(TRAAD_ID, equalTo(id))).collect().size();
    }

    public List<HenvendelseVM> getTidligereHenvendelser() {
        List<HenvendelseVM> traad = getTraad();
        return traad.isEmpty() ? traad : traad.subList(1, traad.size());
    }

    public HenvendelseVM getNyesteHenvendelse() {
        List<HenvendelseVM> traad = getTraad();
        return traad.isEmpty() ? null : traad.get(0);
    }

    public final void oppdaterHenvendelserFra(List<Henvendelse> henvendelser) {
        this.henvendelser = on(henvendelser).map(TIL_HENVENDELSE_VM).collect(NYESTE_OVERST);
    }

    public Optional<HenvendelseVM> getValgtHenvendelse() {
        return valgtHenvendelse;
    }

    public void setValgtHenvendelse(String id) {
        valgtHenvendelse = on(henvendelser).filter(where(ID, equalTo(id))).head();
    }

    public void setValgtHenvendelse(HenvendelseVM valgtHenvendelse) {
        this.valgtHenvendelse = optional(valgtHenvendelse);
    }

}
