package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.unleash;

public class UnleashServiceMock implements UnleashService {
    @Override
    public boolean isEnabled(Feature feature) {
        return false;
    }

    @Override
    public boolean isEnabled(String feature) {
        return false;
    }

    @Override
    public Ping ping() {
        return Ping.avskrudd(new Ping.PingMetadata("Unleash Mock", "Mock", "Mock", false));
    }
}
