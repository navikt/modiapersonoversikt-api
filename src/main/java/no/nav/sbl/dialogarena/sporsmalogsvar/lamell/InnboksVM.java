package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.modig.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.HenvendelseBehandlingService;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils.skillUtTraader;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class InnboksVM implements Serializable {

    private static final Logger log = getLogger(InnboksVM.class);

    private HenvendelseBehandlingService henvendelseBehandlingService;

    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private Map<String, TraadVM> traader = new HashMap<>();
    private List<MeldingVM> nyesteMeldingerITraader = new ArrayList<>();
    private MeldingVM valgtMelding = null;
    private String fnr, feilmeldingKey;
    private String sessionOppgaveId = null, sessionHenvendelseId = null;
    public String traadBesvares;
    public boolean focusValgtTraadOnOpen = false;
    EnforcementPoint pep;

    public InnboksVM(String fnr, HenvendelseBehandlingService henvendelseBehandlingService, EnforcementPoint pep,
                     SaksbehandlerInnstillingerService saksbehandlerInnstillingerService) {
        this.fnr = fnr;
        this.henvendelseBehandlingService = henvendelseBehandlingService;
        this.saksbehandlerInnstillingerService = saksbehandlerInnstillingerService;
        this.pep = pep;
    }

    public String getFnr() {
        return fnr;
    }

    public final void oppdaterMeldinger() {
        traader.clear();
        nyesteMeldingerITraader.clear();
        feilmeldingKey = "";
        try {
            List<Melding> meldinger = henvendelseBehandlingService.hentMeldinger(fnr);

            if (meldinger.isEmpty()) {
                feilmeldingKey = "innboks.feilmelding.ingenmeldinger";
                return;
            }

            Map<String, List<Melding>> meldingTraader = skillUtTraader(meldinger);
            for (Map.Entry<String, List<Melding>> meldingTraad : meldingTraader.entrySet()) {
                traader.put(meldingTraad.getKey(), new TraadVM(TIL_MELDINGVM_TRAAD.apply(meldingTraad.getValue()), pep,
                        saksbehandlerInnstillingerService));
            }
            nyesteMeldingerITraader = traader.values().stream()
                    .map(TraadVM::getNyesteMelding)
                    .sorted(comparing(MeldingVM::getVisningsDato).reversed())
                    .collect(toList());

        } catch (Exception e) {
            log.warn("Feilet ved henting av henvendelser for fnr {}", fnr, e);
            feilmeldingKey = "innboks.feilmelding.feilet";
        }
    }

    public int getTraadLengde(String id) {
        return traader.get(id).getTraadLengde();
    }

    public void setValgtMelding(String id) {
        setValgtMelding(nyesteMeldingerITraader.stream().filter(melding -> melding.getId().equals(id)).findFirst().get());
    }

    public Optional<MeldingVM> getNyesteMeldingITraad(String traadId) {
        Optional<MeldingVM> meldingVM = nyesteMeldingerITraader.stream()
                .filter(m -> m.getTraadId().equals(traadId))
                .findFirst();
        if (!meldingVM.isPresent() && isBlank(feilmeldingKey)) {
            feilmeldingKey = "innboks.feilmelding.ingentilgang";
        }
        return meldingVM;
    }

    public void setValgtMelding(MeldingVM meldingVM) {
        valgtMelding = meldingVM;
    }

    public final IModel<Boolean> erValgtMelding(final MeldingVM meldingVM) {
        return new AbstractReadOnlyModel<Boolean>() {
            @Override
            public Boolean getObject() {
                return valgtMelding != null && valgtMelding.equals(meldingVM);
            }
        };
    }

    public TraadVM getValgtTraad() {
        return valgtMelding != null ? traader.get(valgtMelding.melding.traadId)
                : new TraadVM(new ArrayList<>(), pep, saksbehandlerInnstillingerService);
    }

    public MeldingVM getNyesteMeldingINyesteTraad() {
        return nyesteMeldingerITraader.get(0);
    }

    public List<MeldingVM> getNyesteMeldingerITraader() {
        return nyesteMeldingerITraader;
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

    private static final Function<List<Melding>, List<MeldingVM>> TIL_MELDINGVM_TRAAD = (meldinger) -> {
        List<Melding> meldingerITraad = meldinger.stream().sorted(Melding.NYESTE_FORST).collect(toList());
        return meldingerITraad.stream()
                .map(melding -> new MeldingVM(melding, meldingerITraad.size()))
                .collect(toList());
    };

    public Optional<String> getSessionOppgaveId() {
        return ofNullable(sessionOppgaveId);
    }

    public void setSessionOppgaveId(String sessionOppgaveId) {
        this.sessionOppgaveId = sessionOppgaveId;
    }

    public Optional<String> getSessionHenvendelseId() {
        return ofNullable(sessionHenvendelseId);
    }

    public void setSessionHenvendelseId(String sessionHenvendelseId) {
        this.sessionHenvendelseId = sessionHenvendelseId;
    }

    public void settForsteSomValgtHvisIkkeSatt() {
        if (valgtMelding == null) {
            valgtMelding = nyesteMeldingerITraader.isEmpty() ? null : nyesteMeldingerITraader.get(0);
        }
    }
}