package no.nav.sbl.dialogarena.sporsmalogsvar.consumer.henvendelse.domain;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.henvendelse.Melding;
import no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.MeldingUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Meldinger {

    private final List<Traad> traader;

    public Meldinger(List<Melding> meldinger) {
        traader = lagTraader(meldinger);
    }

    private List<Traad> lagTraader(List<Melding> meldinger) {
        Map<String, List<Melding>> traader = MeldingUtils.skillUtTraader(meldinger);
        return traader.values().stream()
                .map(Traad::new)
                .collect(Collectors.toList());
    }

    public List<Traad> getTraader() {
        return traader;
    }

    public Optional<Traad> getTraad(String traadId) {
        return traader.stream()
                .filter(traad -> traad.getTraadId().equals(traadId))
                .findFirst();
    }

    public boolean erUtenMeldinger() {
        return traader.size() == 0;
    }

}
