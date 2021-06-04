package no.nav.kodeverk.consumer.utils;

import no.nav.kjerneinfo.common.domain.Periode;
import no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.*;
import no.nav.kodeverk.consumer.fim.kodeverk.to.meldinger.FinnKodeverkListeResponse;
import no.nav.kodeverk.consumer.fim.kodeverk.to.meldinger.HentKodeverkRequest;
import no.nav.kodeverk.consumer.fim.kodeverk.to.meldinger.HentKodeverkResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.*;
import no.nav.tjeneste.virksomhet.kodeverk.v2.informasjon.finnkodeverkliste.Kodeverk;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLFinnKodeverkListeResponse;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkRequest;
import no.nav.tjeneste.virksomhet.kodeverk.v2.meldinger.XMLHentKodeverkResponse;
import org.joda.time.DateMidnight;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class KodeverkMapper {
	private static KodeverkMapper instance = null;

	private KodeverkMapper() {}

	public static KodeverkMapper getInstance() {
		if (instance == null) {
			instance = new KodeverkMapper();
		}

		return instance;
	}

	public HentKodeverkResponse map(XMLHentKodeverkResponse xmlResponse) {
		if (xmlResponse == null) {
			return null;
		}
		HentKodeverkResponse response = new HentKodeverkResponse();
		response.setKodeverk(map(xmlResponse.getKodeverk()));
		return response;
	}

	public FinnKodeverkListeResponse map(XMLFinnKodeverkListeResponse xmlResponse) {
		if (xmlResponse == null) {
			return null;
		}

		FinnKodeverkListeResponse response = new FinnKodeverkListeResponse();
		Map<String, no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kodeverk> kodeverksliste = new HashMap<>();
		for (Kodeverk kodeverk : xmlResponse.getKodeverkListe()) {
			kodeverksliste.put(kodeverk.getNavn(), map(kodeverk));
		}
		response.setKodeverkListe(kodeverksliste);
		return response;
	}

	public no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kodeverk map(Kodeverk xmlKodeverk) {
		if (xmlKodeverk == null) {
			return null;
		}
		no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kodeverk kodeverk = new no.nav.kodeverk.consumer.fim.kodeverk.to.informasjon.Kodeverk();
		kodeverk.setNavn(xmlKodeverk.getNavn());
		kodeverk.setUri(xmlKodeverk.getUri());
		kodeverk.setEier(xmlKodeverk.getEier());
		kodeverk.setKilde(map(xmlKodeverk.getKilde()));
		kodeverk.setVersjoneringsdato(map(xmlKodeverk.getVersjoneringsdato()));
		kodeverk.setVersjonsnummer(xmlKodeverk.getVersjonsnummer());
		kodeverk.setGyldighetsperiode(forEach(xmlKodeverk.getGyldighetsperiode(), this::map));
		return kodeverk;
	}

	public EnkeltKodeverk map(XMLKodeverk xmlKodeverk) {
		if (xmlKodeverk == null) {
			return null;
		}
		EnkeltKodeverk kodeverk = new EnkeltKodeverk();
		kodeverk.setNavn(xmlKodeverk.getNavn());
		kodeverk.setUri(xmlKodeverk.getUri());
		kodeverk.setEier(xmlKodeverk.getEier());
		kodeverk.setKilde(map(xmlKodeverk.getKilde()));
		kodeverk.setVersjoneringsdato(map(xmlKodeverk.getVersjoneringsdato()));
		kodeverk.setVersjonsnummer(xmlKodeverk.getVersjonsnummer());
		kodeverk.setGyldighetsperiode(forEach(xmlKodeverk.getGyldighetsperiode(), this::map));

		if (xmlKodeverk instanceof XMLEnkeltKodeverk) {
			kodeverk.setKode(mapKoder(((XMLEnkeltKodeverk) xmlKodeverk).getKode()));
		}

		return kodeverk;
	}

	private Map<String, Kode> mapKoder(List<XMLKode> xmlKoder) {
//		if (xmlKoder == null || xmlKoder.size() == 0) {
//			return null;
//		}
		Map<String, Kode> result = new HashMap<>();

		for (XMLKode xmlKode : xmlKoder) {
			result.put(xmlKode.getNavn(), map(xmlKode));
		}
		return result;
	}

	public Kode map(XMLKode xmlKode) {
		if (xmlKode == null) {
			return null;
		}

		Kode kode = new Kode();
		kode.setNavn(xmlKode.getNavn());
		kode.setUri(xmlKode.getUri());
		kode.setGyldighetsperiode(forEach(xmlKode.getGyldighetsperiode(), this::map));
		kode.setTerm(map(xmlKode.getTerm()));
		return kode;
	}

	private Map<String, Term> map(List<XMLTerm> xmlTermer) {
		Map<String, Term> termer = new HashMap<>();
		if (xmlTermer == null) {
			return termer;
		}

		for (XMLTerm xmlTerm : xmlTermer) {
			termer.put(xmlTerm.getSpraak(), map(xmlTerm));
		}
		return termer;
	}

	private Term map(XMLTerm xmlTerm) {
		if (xmlTerm == null) {
			return null;
		}
		Term term = new Term();
		term.setBeskrivelse(map(xmlTerm.getBeskrivelse()));
		term.setSpraak(xmlTerm.getSpraak());
		term.setGyldighetsperiode(forEach(xmlTerm.getGyldighetsperiode(), this::map));
		term.setNavn(xmlTerm.getNavn());
		term.setUri(xmlTerm.getUri());
		return term;
	}

	private Tekstobjekt map(XMLTekstobjekt beskrivelse) {
		if (beskrivelse == null) {
			return null;
		}
		Tekstobjekt tekstobjekt = new Tekstobjekt();
		tekstobjekt.setUri(beskrivelse.getUri());
		tekstobjekt.setNavn(beskrivelse.getNavn());
		return tekstobjekt;
	}

	public Periode map(XMLPeriode xmlPeriode) {
		if (xmlPeriode == null) {
			return null;
		}
		return new Periode(map(xmlPeriode.getFom()), map(xmlPeriode.getTom()));
	}

	private LocalDate map(DateMidnight date) {
		if (date == null) {
			return null;
		}
		return new LocalDate(date);
	}

	private Kodeverkskilde map(XMLKodeverkskilde xmlKilde) {
		if (xmlKilde == null) {
			return null;
		}

		Kodeverkskilde kilde = new Kodeverkskilde();
		kilde.gyldighetsperiodeKodeverk = map(xmlKilde.getGyldighetsperiodeKodeverk());
		kilde.navn = xmlKilde.getNavn();
		return kilde;
	}

	public XMLHentKodeverkRequest map(HentKodeverkRequest request) {
		if (request == null) {
			return null;
		}

		XMLHentKodeverkRequest xmlRequest = new XMLHentKodeverkRequest();
		xmlRequest.setNavn(request.getNavn());
		xmlRequest.setSpraak(request.getSpraak());
		xmlRequest.setVersjonsnummer(request.getVersjonsnummer());
		return xmlRequest;
	}

	private <S, T> List<T> forEach(List<S> list, Function<S, T> fn) {
		return list.stream().map(fn).collect(Collectors.toList());
	}
}
