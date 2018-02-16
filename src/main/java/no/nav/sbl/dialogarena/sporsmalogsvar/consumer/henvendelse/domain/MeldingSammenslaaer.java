package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MeldingSammenslaaer {

    private final List<Melding> meldinger;

    public MeldingSammenslaaer(List<Melding> meldinger) {
        this.meldinger = meldinger;
    }

    public List<Melding> sammenslaFullforteDelsvar() {
        List<Fritekst> fritekster = getFriteksterFraDelsvar();
        Melding avsluttendeSvar = getAvsluttendeSvar();
        fritekster.addAll(avsluttendeSvar.getFriteksterMedEldsteForst());

        avsluttendeSvar.withFritekst(fritekster.toArray(new Fritekst[fritekster.size()]));
        return meldinger.stream()
                .filter(melding-> !melding.erDelvisSvar())
                .collect(Collectors.toList());
    }

    private List<Fritekst> getFriteksterFraDelsvar() {
        return meldinger.stream()
                .filter(Melding::erDelvisSvar)
                .sorted(Comparator.comparing(melding -> melding.opprettetDato))
                .map(melding -> new Fritekst(melding.getFritekst(), melding.skrevetAv, melding.opprettetDato))
                .collect(Collectors.toList());
    }

    private Melding getAvsluttendeSvar() {
        return meldinger.stream()
                .sorted(Comparator.comparing(melding -> melding.opprettetDato))
                .filter(Melding::erSvarSkriftlig)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }
}
