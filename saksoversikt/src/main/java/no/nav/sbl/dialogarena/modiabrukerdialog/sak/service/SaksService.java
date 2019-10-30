package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.Java8Utils;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("squid:S1166") // Either log or rethrow
public class SaksService {

    @Inject
    private GsakSakerService gsakSakerService;

    @Inject
    private PesysService pesysService;

    public ResultatWrapper<List<Sak>> hentAlleSaker(String fnr) {
        Set<Baksystem> feilendeBaksystemer = new HashSet<>();

        Optional<Stream<Sak>> maybeFraGSak;
        Optional<Stream<Sak>> maybeFraPesys;

        try {
            maybeFraGSak = gsakSakerService.hentSaker(fnr);
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystemer.add(e.getBaksystem());
            maybeFraGSak = Optional.empty();
        }

        try {
            maybeFraPesys = pesysService.hentSakstemaFraPesys(fnr);
        } catch (FeilendeBaksystemException e) {
            feilendeBaksystemer.add(e.getBaksystem());
            maybeFraPesys = Optional.empty();
        }

        Stream<Sak> fraGSak = maybeFraGSak.orElse(Stream.empty());
        Stream<Sak> fraPesys = maybeFraPesys.orElse(Stream.empty());

        return new ResultatWrapper<>(Java8Utils.concat(fraGSak, fraPesys).collect(toList()), feilendeBaksystemer);
    }

}
