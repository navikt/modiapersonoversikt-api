package no.nav.sbl.dialogarena.modiabrukerdialog.sak.service;

import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Baksystem;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.DokumentMetadata;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.providerdomain.Sakstema;
import no.nav.sbl.dialogarena.modiabrukerdialog.sak.service.interfaces.SaksoversiktService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SaksoversiktServiceImpl implements SaksoversiktService {

    public void fjernGamleDokumenter(List<Sakstema> resultat) {
        List<String> journalpostIdAaFjerne = getJournalpostIdAaFjerne(resultat);
        resultat.stream()
                .forEach(sakstema -> sakstema.withDokumentMetadata(sakstema.dokumentMetadata.stream()
                        .filter(dokumentMetadata -> !journalpostIdAaFjerne
                                .contains(dokumentMetadata.getJournalpostId())).collect(Collectors.toList())));
    }


    private List<String> getJournalpostIdAaFjerne(List<Sakstema> resultat) {
        Map<String, DokumentMetadata> muligeJournalpostIdAaFjerne = new HashMap<>();

        final LocalDateTime PROD_SETTNING_DATO = formatDate(System.getProperty("saksoversikt.prodsettningsdato"));
        getMuligeJournalpostIdAaFjerne(resultat, muligeJournalpostIdAaFjerne);

        return muligeJournalpostIdAaFjerne
                .keySet()
                .stream()
                .filter(key -> muligeJournalpostIdAaFjerne.get(key).getBaksystem().size() == 1
                        && muligeJournalpostIdAaFjerne.get(key).getBaksystem().contains(Baksystem.JOARK)
                        && muligeJournalpostIdAaFjerne.get(key).getDato().isBefore(PROD_SETTNING_DATO))
                .collect(Collectors.toList());
    }

    private void getMuligeJournalpostIdAaFjerne(List<Sakstema> resultat, Map<String, DokumentMetadata> muligeJournalpostIdAaFjerne) {
        for (Sakstema sakstema : resultat) {
            for (DokumentMetadata dokumentMetadata : sakstema.dokumentMetadata) {
                String journalpostId = dokumentMetadata.getJournalpostId();
                if (muligeJournalpostIdAaFjerne.containsKey(journalpostId)) {
                    muligeJournalpostIdAaFjerne.remove(journalpostId);
                } else {
                    muligeJournalpostIdAaFjerne.put(journalpostId, dokumentMetadata);
                }
            }
        }
    }

    private static LocalDateTime formatDate(String prodDate) {
        LocalDate date = LocalDate.parse(prodDate);
        LocalTime time = LocalTime.of(0, 0);
        return LocalDateTime.of(date, time);
    }
}
