package no.nav.sbl.dialogarena.modiabrukerdialog.web.config.mock;

import no.nav.modig.modia.widget.panels.InfoPanelVM;
import no.nav.sykmeldingsperioder.widget.SykepengerWidgetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SykepengerWidgetMockContext {

    @Bean
    public SykepengerWidgetService sykepengerWidgetService() {
        return new SykepengerWidgetService() {
            @Override
            public List<InfoPanelVM> getWidgetContent(String fnr) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

}
