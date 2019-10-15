package no.nav.sbl.dialogarena.modiabrukerdialog.api.utils.http;

import no.nav.brukerdialog.security.context.SubjectHandler;
import no.nav.brukerdialog.security.context.SubjectHandlerUtils;
import no.nav.brukerdialog.security.context.ThreadLocalSubjectHandler;
import no.nav.brukerdialog.security.domain.IdentType;
import no.nav.brukerdialog.tools.SecurityConstants;

public class SubjectHandlerUtil {

    public static void setInnloggetSaksbehandler(String ident) {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
        System.setProperty(SecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(ident, IdentType.InternBruker).getSubject());
    }

}
