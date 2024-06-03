package no.nav.modiapersonoversiktproxy.consumer.infotrygd.sykepenger.mapping.to;

import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.Bruker;
import no.nav.modiapersonoversiktproxy.consumer.infotrygd.domain.sykepenger.Sykmeldingsperiode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SykepengerResponse implements Serializable {

    private Bruker bruker;
    private List<Sykmeldingsperiode> sykmeldingsperioder;

    public SykepengerResponse() {
    }

    public SykepengerResponse(Bruker bruker, List<Sykmeldingsperiode> sykmeldingsperioder) {
        this.bruker = bruker;
        this.sykmeldingsperioder = sykmeldingsperioder;
    }

    public Bruker getBruker() {
        return bruker;
    }

    public void setBruker(Bruker bruker) {
        this.bruker = bruker;
    }

    public List<Sykmeldingsperiode> getSykmeldingsperioder() {
        if (sykmeldingsperioder == null) {
            sykmeldingsperioder = new ArrayList<>();
        }
        return sykmeldingsperioder;
    }

    public void setSykmeldingsperioder(List<Sykmeldingsperiode> sykmeldingsperioder) {
        this.sykmeldingsperioder = sykmeldingsperioder;
    }
}
