package no.nav.sbl.dialogarena.modiabrukerdialog.web.selftest;

import no.nav.dialogarena.modiabrukerdialog.example.PingResult;
import no.nav.dialogarena.modiabrukerdialog.example.Pingable;
import no.nav.modig.core.exception.SystemException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PropertyListView;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class SelfTestPage extends WebPage {

    private static final Logger logger = getLogger(SelfTestPage.class);

    public SelfTestPage() {
        logger.info("entered SelfTestPage!");
        List<ServiceStatus> statusList = new ArrayList<>();

        //        Add servicestatus' as needed, e.g.
        //                statusList.addAll(getPingableComponentStatus("search", null, "SEARCH_OK", "SEARCH_ERROR"));

        add(new ServiceStatusListView("serviceStatusTable", statusList));
    }

    private List<ServiceStatus> getPingableComponentStatus(String name, Pingable pingable, String okCode, String errorCode) {
        List<ServiceStatus> serviceStatuses = new ArrayList<>();
        try {
            List<PingResult> pingResults = pingable.ping();
            if (!pingResults.isEmpty()) {
                for (PingResult pingResult : pingResults) {
                    serviceStatuses.add(new ServiceStatus(pingResult.getServiceName(),
                            pingResult.getServiceStatus().equals(PingResult.SERVICE_OK) ? okCode : errorCode,
                            pingResult.getElapsedTime()));
                }
            }
        } catch (SystemException se) {
            logger.warn(name + " was not retrievable. Class canonical name: " + pingable.getClass().getCanonicalName() + ". Exception message: " + se.getMessage());
        }
        return serviceStatuses;
    }

    private static class ServiceStatusListView extends PropertyListView<ServiceStatus> {

        private static final long serialVersionUID = 1L;

        public ServiceStatusListView(String id, List<ServiceStatus> statusList) {
            super(id, statusList);
        }

        @Override
        protected void populateItem(ListItem<ServiceStatus> listItem) {
            listItem.add(new Label("name"), new Label("status"), new Label("durationMilis"));
        }
    }

    private static class ServiceStatus implements Serializable {

        private static final long serialVersionUID = 1L;
        private final String name;
        private final String status;
        private final long durationMilis;

        public ServiceStatus(String name, String status, long durationMilis) {
            this.name = name;
            this.status = status;
            this.durationMilis = durationMilis;
        }

        public String getName() {
            return name;
        }

        public String getStatus() {
            return status;
        }

        public long getDurationMilis() {
            return durationMilis;
        }

    }

}
