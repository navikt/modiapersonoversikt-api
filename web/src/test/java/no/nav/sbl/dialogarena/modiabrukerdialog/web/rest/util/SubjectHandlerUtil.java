package no.nav.sbl.dialogarena.modiabrukerdialog.web.rest.util;

import no.nav.modig.core.context.ModigSecurityConstants;
import no.nav.modig.core.context.SubjectHandler;
import no.nav.modig.core.context.SubjectHandlerUtils;
import no.nav.modig.core.context.ThreadLocalSubjectHandler;
import no.nav.modig.core.domain.IdentType;

public class SubjectHandlerUtil {

    public static void setInnloggetSaksbehandler(String ident) {
        System.setProperty(SubjectHandler.SUBJECTHANDLER_KEY, ThreadLocalSubjectHandler.class.getCanonicalName());
        System.setProperty(ModigSecurityConstants.SYSTEMUSER_USERNAME, "srvModiabrukerdialog");
        SubjectHandlerUtils.setSubject(new SubjectHandlerUtils.SubjectBuilder(ident, IdentType.InternBruker).getSubject());
    }

}
