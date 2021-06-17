package no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.informasjon;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Kodeverkselement extends IdentifiserbarEntitet implements Serializable {

    protected String begrepsannotering;
    protected List<Periode> gyldighetsperiode;

    public List<Periode> getGyldighetsperiode() {
        return gyldighetsperiode;
    }

    public void setGyldighetsperiode(List<Periode> gyldighetsperiode) {
        this.gyldighetsperiode = new ArrayList<>(gyldighetsperiode);
    }
}
