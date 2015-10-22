package no.nav.sbl.dialogarena.utbetaling.domain.testdata;

import no.nav.tjeneste.virksomhet.utbetaling.v1.informasjon.*;

import java.util.List;

public class LagTestWSYtelse {

    public static WSYtelseskomponent lagYtelseskomponent(String komponenttype, Double komponentbeloep) {
        return new WSYtelseskomponent()
                .withYtelseskomponenttype(komponenttype)
                .withYtelseskomponentbeloep(komponentbeloep);
    }

    public static WSYtelseskomponent lagYtelseskomponent(String komponenttype, Double komponentbeloep, Double satsbeloep, Double satsantall) {
        return lagYtelseskomponent(komponenttype, komponentbeloep)
                .withSatsbeloep(satsbeloep)
                .withSatsantall(satsantall);
    }

    public static WSTrekk lagWSTrekk(String trekktype, Double trekkbeloep, String kreditor) {
        return new WSTrekk()
                .withTrekktype(trekktype)
                .withTrekkbeloep(trekkbeloep)
                .withKreditor(kreditor);
    }
    private static Double finnYtelseskomponentersum(List<WSYtelseskomponent> ytelseskomponenterliste) {
        Double ytelseskomponentersum = 0.0;
        for (WSYtelseskomponent ytelseskomponent : ytelseskomponenterliste) {
            ytelseskomponentersum += ytelseskomponent.getYtelseskomponentbeloep();
        }
        return ytelseskomponentersum;
    }

    public static WSYtelse lagWSYtelse(WSYtelsestyper ytelsestyper, WSPerson rettighetshaver, WSPeriode ytelsesperiode, List<WSYtelseskomponent> ytelseskomponenter) {
        return new WSYtelse()
                .withYtelsestype(ytelsestyper)
                .withRettighetshaver(rettighetshaver)
                .withYtelsesperiode(ytelsesperiode)
                .withYtelseskomponentListe(ytelseskomponenter)
                .withYtelseskomponentersum(finnYtelseskomponentersum(ytelseskomponenter))
                .withBilagsnummer("***REMOVED***");
    }

}
