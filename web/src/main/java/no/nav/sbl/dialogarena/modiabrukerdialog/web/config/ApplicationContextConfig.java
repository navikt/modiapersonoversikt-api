package no.nav.sbl.dialogarena.modiabrukerdialog.web.config;

import org.springframework.core.PriorityOrdered;


public class ApplicationContextConfig implements PriorityOrdered {


    @Override
    public int getOrder() {
        return 0;
    }
}
