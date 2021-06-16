package no.nav.modiapersonoversikt.consumer.kodeverk.consumer.fim.kodeverk.to.informasjon;

import no.nav.modiapersonoversikt.legacy.kjerneinfo.common.domain.Periode;

import java.io.Serializable;

public class Kodeverkskilde implements Serializable {
	public Periode gyldighetsperiodeKodeverk;
	public String navn;
}
