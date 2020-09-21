package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.Java8Utils;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@SuppressWarnings("squid:S1166") // Either log or rethrow
public class SaksService {

    @Autowired
    private GsakSakerService gsakSakerService;

    @Autowired
    private PesysService pesysService;

    public ResultatWrapper<List<Sak>> hentAlleSaker(String fnr) {
        Set<Baksystem> feilendeBaksystemer = new HashSet<>();

        Optional<Stream<Sak>> maybeFraGSak;
        Optional<List<Sak>> maybeFraPesys;

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
        Stream<Sak> fraPesys = maybeFraPesys.map(Collection::stream).orElse(Stream.empty());

        return new ResultatWrapper<>(Java8Utils.concat(fraGSak, fraPesys)
                .collect(toList()), feilendeBaksystemer);
    }

}
