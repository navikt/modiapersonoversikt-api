package no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.constants;

import java.util.List;

import static java.util.Arrays.asList;

public class URLParametere {
    public static final String OPPGAVEID = "oppgaveid";
    public static final String HENVENDELSEID = "henvendelseid";
    public static final String BESVARES = "besvares";

    public static final List<String> URL_TIL_SESSION_PARAMETERE = asList(HENVENDELSEID, OPPGAVEID, BESVARES);
}
