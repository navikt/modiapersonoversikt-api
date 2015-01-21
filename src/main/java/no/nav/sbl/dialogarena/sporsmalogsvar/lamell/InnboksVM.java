package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.lang.option.Optional;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Melding;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.commons.collections15.Transformer;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.modig.lang.collections.IterUtils.on;
import static no.nav.modig.lang.collections.PredicateUtils.equalTo;
import static no.nav.modig.lang.collections.PredicateUtils.where;
import static no.nav.modig.lang.option.Optional.none;
import static no.nav.modig.lang.option.Optional.optional;
import static no.nav.sbl.dialogarena.sporsmalogsvar.common.utils.MeldingUtils.skillUtTraader;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.ID;
import static no.nav.sbl.dialogarena.sporsmalogsvar.lamell.MeldingVM.TRAAD_ID;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class InnboksVM implements Serializable {

    private static final Logger log = getLogger(InnboksVM.class);

    private HenvendelseBehandlingService henvendelseBehandlingService;

    private Map<String, TraadVM> traader = new HashMap<>();
    private List<MeldingVM> nyesteMeldingerITraad = new ArrayList<>();
    private Optional<MeldingVM> valgtMelding;
    private String fnr, feilmeldingKey;
    private Optional<String> sessionOppgaveId = none(), sessionHenvendelseId = none();

    public InnboksVM(String fnr, HenvendelseBehandlingService henvendelseBehandlingService) {
        this.fnr = fnr;
        this.henvendelseBehandlingService = henvendelseBehandlingService;
        oppdaterMeldinger();
        valgtMelding = optional(nyesteMeldingerITraad.isEmpty() ? null : nyesteMeldingerITraad.get(0));
    }

    public String getFnr() {
        return fnr;
    }

    public final void oppdaterMeldinger() {
        traader.clear();
        feilmeldingKey = "";
        try {
            List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(fnr);

            if (meldinger.isEmpty()) {
                feilmeldingKey = "innboks.feilmelding.ingenmeldinger";
                return;
            }

            Map<String, List<Melding>> meldingTraader = skillUtTraader(meldinger);
            for (Map.Entry<String, List<Melding>> meldingTraad : meldingTraader.entrySet()) {
                traader.put(meldingTraad.getKey(), new TraadVM(TIL_MELDINGVM_TRAAD.transform(meldingTraad.getValue())));
            }
            nyesteMeldingerITraad = on(traader.values()).map(new Transformer<TraadVM, MeldingVM>() {
                @Override
                public MeldingVM transform(TraadVM traadVM) {
                    return traadVM.getNyesteMelding();
                }
            }).collect(MeldingVM.NYESTE_FORST);

        } catch (Exception e) {
            log.warn("Feilet ved henting av henvendelser for fnr {}", fnr, e);
            feilmeldingKey = "innboks.feilmelding.feilet";
        }
    }

    public int getTraadLengde(String id) {
        return traader.get(id).getTraadLengde();
    }

    public void setValgtMelding(String id) {
        setValgtMelding(on(nyesteMeldingerITraad).filter(where(ID, equalTo(id))).head().get());
    }

    public Optional<MeldingVM> getNyesteMeldingITraad(String traadId) {
        Optional<MeldingVM> meldingVM = on(nyesteMeldingerITraad).filter(where(TRAAD_ID, equalTo(traadId))).head();
        if (!meldingVM.isSome()) {
            feilmeldingKey = "innboks.feilmelding.ingentilgang";
        }
        return meldingVM;
    }

    public void setValgtMelding(MeldingVM meldingVM) {
        valgtMelding = optional(meldingVM);
    }

    public final IModel<Boolean> erValgtMelding(final MeldingVM meldingVM) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return valgtMelding.isSome() && valgtMelding.get().equals(meldingVM);
            }
        };
    }

    public TraadVM getValgtTraad() {
        return valgtMelding.isSome() ? traader.get(valgtMelding.get().melding.traadId) : new TraadVM(new ArrayList<MeldingVM>());
    }

    public MeldingVM getNyesteMeldingINyesteTraad() {
        return nyesteMeldingerITraad.get(0);
    }

    public Map<String, TraadVM> getTraader() {
        return traader;
    }

    public boolean harTraader() {
        return !traader.isEmpty();
    }

    public AbstractReadOnlyModel<Boolean> harFeilmelding() {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return isNotBlank(feilmeldingKey);
            }
        };
    }

    private static final Transformer<List<Melding>, List<MeldingVM>> TIL_MELDINGVM_TRAAD = new Transformer<List<Melding>, List<MeldingVM>>() {
        @Override
        public List<MeldingVM> transform(List<Melding> meldinger) {
            List<Melding> meldingerITraad = on(meldinger).collect(Melding.NYESTE_FORST);
            List<MeldingVM> meldingVMTraad = new ArrayList<>();
            for (Melding melding : meldingerITraad) {
                meldingVMTraad.add(new MeldingVM(melding, meldingerITraad.size()));
            }
            return meldingVMTraad;
        }
    };

    public Optional<String> getSessionOppgaveId() {
        return sessionOppgaveId;
    }

    public void setSessionOppgaveId(String sessionOppgaveId) {
        this.sessionOppgaveId = optional(sessionOppgaveId);
    }

    public Optional<String> getSessionHenvendelseId() {
        return sessionHenvendelseId;
    }

    public void setSessionHenvendelseId(String sessionHenvendelseId) {
        this.sessionHenvendelseId = optional(sessionHenvendelseId);
    }
}