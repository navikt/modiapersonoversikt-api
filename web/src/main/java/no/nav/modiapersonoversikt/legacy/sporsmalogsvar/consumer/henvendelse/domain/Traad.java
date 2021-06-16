package no.nav.modiapersonoversikt.legacy.sporsmalogsvar.consumer.henvendelse.domain;

import no.nav.modiapersonoversikt.legacy.api.domain.Oppgave;
import no.nav.modiapersonoversikt.legacy.api.domain.henvendelse.Melding;
import no.nav.modiapersonoversikt.legacy.api.utils.henvendelse.delsvar.DelsvarSammenslaaer;
import no.nav.modiapersonoversikt.legacy.api.utils.henvendelse.delsvar.DelsvarUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Traad {

    private List<Melding> meldinger;

    public Traad(List<Melding> meldinger) {
        if (DelsvarUtils.harAvsluttendeSvarEtterDelsvar(meldinger)) {
            this.meldinger = DelsvarSammenslaaer.sammenslaFullforteDelsvar(meldinger);
        } else {
            this.meldinger = meldinger;
        }
    }


    public Melding getEldsteMelding() {
        return meldinger.get(0);
    }

    public String getTraadId() {
        return getRotmelding().traadId;
    }

    public List<Melding> getMeldinger() {
        return meldinger;
    }

    private Melding getRotmelding() {
        return meldinger.stream()
                .filter(melding -> melding.id.equals(melding.traadId))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    public boolean besvaringKanFerdigstilleOppgave(@Nullable Oppgave oppgave) {
        if (oppgave == null) {
            return true;
        }
        return meldinger
                .stream()
                .anyMatch((melding) -> melding.id.contains(oppgave.henvendelseId));
    }
}
