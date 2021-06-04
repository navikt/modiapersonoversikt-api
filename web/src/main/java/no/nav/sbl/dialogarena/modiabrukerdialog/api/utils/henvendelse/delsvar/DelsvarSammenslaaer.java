package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.henvendelse.delsvar;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DelsvarSammenslaaer {

    public static List<Melding> sammenslaFullforteDelsvar(List<Melding> meldinger) {
        List<Fritekst> fritekster = getFriteksterFraDelsvar(meldinger);

        return getAvsluttendeSvar(meldinger)
                .map((avsluttendeSvar) -> {
                    fritekster.addAll(avsluttendeSvar.getFriteksterMedEldsteForst());

                    avsluttendeSvar.withFritekst(fritekster.toArray(new Fritekst[fritekster.size()]));
                    return meldinger.stream()
                            .filter(melding-> !melding.erDelvisSvar())
                            .collect(Collectors.toList());
                })
                .orElse(meldinger);

    }

    private static List<Fritekst> getFriteksterFraDelsvar(List<Melding> meldinger) {
        return meldinger.stream()
                .filter(Melding::erDelvisSvar)
                .sorted(Comparator.comparing(melding -> melding.ferdigstiltDato))
                .map(melding -> new Fritekst(melding.getFritekst(), melding.skrevetAv, melding.ferdigstiltDato))
                .collect(Collectors.toList());
    }

    private static Optional<Melding> getAvsluttendeSvar(List<Melding> meldinger) {
        return meldinger.stream()
                .sorted(Comparator.comparing(melding -> melding.ferdigstiltDato))
                .filter(Melding::erSvarSkriftlig)
                .findFirst();
    }

}
