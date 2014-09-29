package no.nav.nav.sbl.dialogarena.modiabrukerdialog.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Kanal {
    TEKST("TEKST.kvittering.bekreftelse"),
    TELEFON("TELEFON.kvittering.bekreftelse"),
    OPPMOTE("OPPMOTE.kvittering.bekreftelse");

    public final static List<Kanal> TELEFON_OG_OPPMOTE = new ArrayList<>(Arrays.asList(TELEFON, OPPMOTE));

    private final String kvitteringKey;

    private Kanal(String kvitteringKey) {
        this.kvitteringKey = kvitteringKey;
    }

    public String getKvitteringKey(String prefix) {
        return prefix + "." + kvitteringKey;
    }
}
