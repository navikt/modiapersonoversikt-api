package no.nav.sbl.dialogarena.sporsmalogsvar.lamell;

import no.nav.brukerdialog.security.tilgangskontroll.policy.pep.EnforcementPoint;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.Oppgave;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.service.saksbehandler.SaksbehandlerInnstillingerService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.HenvendelseBehandlingService;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Meldinger;
import no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain.Traad;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.slf4j.LoggerFactory.getLogger;

public class InnboksVM implements Serializable {

    private static final Logger log = getLogger(InnboksVM.class);

    private HenvendelseBehandlingService henvendelseBehandlingService;

    private SaksbehandlerInnstillingerService saksbehandlerInnstillingerService;

    private Map<String, TraadVM> traaderVM = new HashMap<>();
    private List<MeldingVM> nyesteMeldingerITraader = new ArrayList<>();
    private MeldingVM valgtMelding = null;
    private String fnr, feilmeldingKey;
    private String sessionOppgaveId = null, sessionHenvendelseId = null;
    public String traadBesvares;
    public boolean focusValgtTraadOnOpen = false;
    EnforcementPoint pep;
    public final List<Oppgave> tildelteOppgaver = new ArrayList<>();
    public final List<Oppgave> tildelteOppgaverUtenDelsvar = new ArrayList<>();

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
        traaderVM.clear();
        nyesteMeldingerITraader.clear();
        feilmeldingKey = "";
        try {
            Meldinger meldinger = henvendelseBehandlingService.hentMeldinger(fnr, saksbehandlerInnstillingerService.getSaksbehandlerValgtEnhet());

            if (meldinger.erUtenMeldinger()) {
                feilmeldingKey = "innboks.feilmelding.ingenmeldinger";
                return;
            }

            List<Traad> traader = meldinger.getTraader();
            for (Traad traad: traader) {
                traaderVM.put(traad.getTraadId(), new TraadVM(TIL_MELDINGVM_TRAAD.apply(traad.getMeldinger()), pep,
                        saksbehandlerInnstillingerService));
            }
            nyesteMeldingerITraader = traaderVM.values().stream()
                    .map(TraadVM::getNyesteMelding)
                    .sorted(comparing(MeldingVM::getDato).reversed())
                    .collect(toList());

        } catch (Exception e) {
            log.warn("Feilet ved henting av henvendelser for fnr {}", fnr, e);
            feilmeldingKey = "innboks.feilmelding.feilet";
        }
        tildelteOppgaverUtenDelsvar.clear();
        tildelteOppgaverUtenDelsvar.addAll(tildelteOppgaver.stream()
                .filter(oppgave -> !ofNullable(getTraader().get(oppgave.henvendelseId))
                        .map(TraadVM::harDelsvar)
                        .orElse(false))
                .collect(toList()));
    }

    public int getTraadLengde(String id) {
        return traaderVM.get(id).getTraadLengde();
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
        return ofNullable(valgtMelding)
                .map(meldingVM -> traaderVM.get(meldingVM.melding.traadId))
                .orElse(new TraadVM(new ArrayList<>(), pep, saksbehandlerInnstillingerService));
    }

    public MeldingVM getNyesteMeldingINyesteTraad() {
        return nyesteMeldingerITraader.get(0);
    }

    public List<MeldingVM> getNyesteMeldingerITraader() {
        return nyesteMeldingerITraader;
    }

    public Map<String, TraadVM> getTraader() {
        return traaderVM;
    }

    public boolean harTraader() {
        return !traaderVM.isEmpty();
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
        int traadLengde = meldinger.size();
        return meldinger.stream()
                .sorted(Melding.NYESTE_FORST)
                .map(melding -> new MeldingVM(melding, traadLengde))
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