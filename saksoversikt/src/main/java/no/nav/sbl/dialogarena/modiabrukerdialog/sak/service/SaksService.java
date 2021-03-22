package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.api.service.saker.SakerService;
import no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash.UnleashService;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.FeilendeBaksystemException;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sak;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.resultatwrappere.ResultatWrapper;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.utils.Java8Utils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem.GSAK;
import static no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.GsakSakerService.GSAK_FAGSYSTEM_ID;

@SuppressWarnings("squid:S1166") // Either log or rethrow
public class SaksService {

    @Autowired
    private GsakSakerService gsakSakerService;

    @Autowired
    private PesysService pesysService;

    @Autowired
    private SakerService sakerService;

    @Autowired
    private UnleashService unleashService;

    private static final String SAK_FEATURE = "modiabrukerdialog.rest-sak-impl";

    public ResultatWrapper<List<Sak>> hentAlleSaker(String fnr) {
        Set<Baksystem> feilendeBaksystemer = new HashSet<>();

        Optional<Stream<Sak>> maybeFraGSak;
        Optional<List<Sak>> maybeFraPesys;

        try {
            if (unleashService.isEnabled(SAK_FEATURE)) {
                maybeFraGSak = Optional.of(sakerService.hentSakSaker(fnr).getSaker().stream().map(
                        sak ->
                                new Sak()
                                        .withSaksId(sak.saksId)
                                        .withFagsaksnummer(sak.fagsystemSaksId)
                                        .withTemakode(sak.temaKode)
                                        .withBaksystem(GSAK)
                                        .withFagsystem(sak.fagsystemKode)
                                        .withFagsystem(GSAK_FAGSYSTEM_ID)

                ));
            } else {
                maybeFraGSak = gsakSakerService.hentSaker(fnr);
            }
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
        Stream<Sak> fraPesys = maybeFraPesys.stream().flatMap(Collection::stream);

        return new ResultatWrapper<>(Java8Utils.concat(fraGSak, fraPesys)
                .collect(toList()), feilendeBaksystemer);
    }

}
