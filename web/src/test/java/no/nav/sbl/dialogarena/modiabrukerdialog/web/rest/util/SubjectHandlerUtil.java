package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.brukerdialog.security.context.SubjectHandlerUtils;
import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.brukerdialog.security.domain.IdentType;

public class SubjectHandlerUtil {

    private final static String SYSTEMUSER_USERNAME = "no.nav.modig.security.systemuser.username";

    public static void setInnloggetSaksbehandler(String ident) {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
        System.setProperty(SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(ident, IdentType.InternBruker).getSubject());
    }

}
