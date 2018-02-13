package no.nav.sbl.dialogarena.sporsmalogsvar.domain;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Fritekst;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Traader {

    private final Map<String, List<Melding>> traader;

    public Traader(List<Melding> meldinger) {
        Map<String, List<Melding>> traader = MeldingUtils.skillUtTraader(meldinger);
        this.traader = sammenslaFullforteDelsvar(traader);
    }

    private Map<String, List<Melding>> sammenslaFullforteDelsvar(Map<String, List<Melding>> traader) {
        return traader.values().stream()
                .map(this::sammenslaFullforteDelsvar)
                .collect(Collectors.toMap(traad -> traad.get(0).traadId, traad -> traad));
    }

    private List<Melding> sammenslaFullforteDelsvar(List<Melding> traad) {
        if (traadHarAvsluttendeSvarEtterDelsvar(traad)) {
            List<Fritekst> fritekster = getFriteksterFraDelsvar(traad);
            Melding avsluttendeSvar = getAvsluttendeSvar(traad);
            fritekster.addAll(avsluttendeSvar.getFriteksterMedEldsteForst());

            avsluttendeSvar.withFritekst(fritekster.toArray(new Fritekst[fritekster.size()]));
            return traad.stream()
                    .filter(melding-> !melding.erDelvisSvar())
                    .collect(Collectors.toList());
        }
        return traad;
    }

    private List<Fritekst> getFriteksterFraDelsvar(List<Melding> traad) {
        return traad.stream()
                        .filter(Melding::erDelvisSvar)
                        .sorted(Comparator.comparing(a -> a.opprettetDato))
                        .map(melding -> new Fritekst(melding.getFritekst(), melding.skrevetAv, melding.opprettetDato))
                        .collect(Collectors.toList());
    }

    private Melding getAvsluttendeSvar(List<Melding> traad) {
        return traad.stream()
                .sorted(Comparator.comparing(melding -> melding.opprettetDato))
                .filter(Melding::erSvarSkriftlig)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private boolean traadHarAvsluttendeSvarEtterDelsvar(List<Melding> traad) {
        traad.sort(Comparator.comparing(melding -> melding.opprettetDato));
        Collections.reverse(traad);

        Iterator<Melding> iterator = traad.iterator();
        Melding melding = iterator.next();
        while (iterator.hasNext()) {
            if (melding.erSvarSkriftlig() && iterator.hasNext() && iterator.next().erDelvisSvar()) {
                return true;
            }
        }
        return false;
    }

    public Map<String, List<Melding>> getTraader() {
        return traader;
    }

    public boolean erUtenMeldinger() {
        return traader.size() == 0;
    }
}
