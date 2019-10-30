package no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon;

import no.nav.kjerneinfo.common.domain.Periode;

import java.io.Serializable;

public class Kodeverkskilde implements Serializable {
	protected Periode gyldighetsperiodeKodeverk;
	protected String navn;
}
