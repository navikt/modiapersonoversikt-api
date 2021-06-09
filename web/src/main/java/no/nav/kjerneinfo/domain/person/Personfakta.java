package no.nav.kjerneinfo.domain.person;

import no.nav.kjerneinfo.common.domain.Kodeverdi;
import no.nav.kjerneinfo.domain.info.Bankkonto;
import no.nav.kjerneinfo.domain.info.BankkontoUtland;
import no.nav.kjerneinfo.domain.person.fakta.*;
import no.nav.kjerneinfo.domain.person.predicate.HarDiskresjonskode;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.io.Serializable;
import java.util.*;

public class Personfakta implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int BARN_ALDERSGRENSE = 21;
    private int personfaktaId;
    private Personnavn personnavn;
    private Adresselinje alternativAdresse;
    private Bankkonto bankkonto;
    private Kodeverdi bostatus;
    private Adresselinje bostedsadresse;
    private Kodeverdi diskresjonskode;
    private List<Familierelasjon> harFraRolleIList;
    private String fodested;
    private String gjeldendeMedlemskapstatus;
    private AnsvarligEnhet ansvarligEnhet;
    private Kodeverdi kjonn;
    private Adresselinje postadresse;
    private Kodeverdi statsborgerskap;
    private Kodeverdi sivilstand;
    private LocalDate sivilstandFom;
    private LocalDateTime doedsdato;
    private Sikkerhetstiltak sikkerhetstiltak;
    private List<Kodeverdi> tilrettelagtKommunikasjon;
    private GeografiskTilknytning geografiskTilknytning;
    private List<Telefon> kontaktinformasjon = new ArrayList<>();
    private Kodeverdi gjeldendePostadressetype;
    private boolean egenansatt;

    public boolean getEgenansatt() {
        return egenansatt;
    }

    public void setEgenansatt(boolean egenansatt) {
        this.egenansatt = egenansatt;
    }

    public int getPersonfaktaId() {
        return personfaktaId;
    }

    public void setPersonfaktaId(int personfaktaId) {
        this.personfaktaId = personfaktaId;
    }

    public Personnavn getPersonnavn() {
        return personnavn;
    }

    public void setPersonnavn(Personnavn personnavn) {
        this.personnavn = personnavn;
    }

    public Kodeverdi getSivilstand() {
        return sivilstand;
    }

    public void setSivilstand(Kodeverdi sivilstand) {
        this.sivilstand = sivilstand;
    }

    public Adresselinje getAdresse() {
        return bostedsadresse;
    }

    public void setAdresse(Adresse adresse) {
        this.bostedsadresse = adresse;
    }

    public Kodeverdi getKjonn() {
        return kjonn;
    }

    public void setKjonn(Kodeverdi kjonn) {
        this.kjonn = kjonn;
    }

    public Kodeverdi getBostatus() {
        return bostatus;
    }

    public void setBostatus(Kodeverdi bostatus) {
        this.bostatus = bostatus;
    }

    public String getGjeldendeMedlemskapstatus() {
        return gjeldendeMedlemskapstatus;
    }
    public void setGjeldendeMedlemskapstatus(String gjeldendeMedlemskapstatus) {
        this.gjeldendeMedlemskapstatus = gjeldendeMedlemskapstatus;
    }

    public Kodeverdi getStatsborgerskap() {
        return statsborgerskap;
    }

    public void setStatsborgerskap(Kodeverdi statsborgerskap) {
        this.statsborgerskap = statsborgerskap;
    }

    public Kodeverdi getDiskresjonskode() {
        return diskresjonskode;
    }

    public String getDiskresjonskodeBeskrivelse() {
        if (!isHarDiskresjonskode()) {
            return "";
        } else if (diskresjonskode.getKodeRef().equals(Diskresjonskoder.FORTROLIG_ADRESSE.getValue())) {
            return Diskresjonskoder.FORTROLIG_ADRESSE.getBeskrivelse();
        } else if (diskresjonskode.getKodeRef().equals(Diskresjonskoder.STRENGT_FORTROLIG_ADRESSE.getValue())) {
            return Diskresjonskoder.STRENGT_FORTROLIG_ADRESSE.getBeskrivelse();
        } else {
            return "";
        }
    }

    public void setDiskresjonskode(Kodeverdi diskresjonskode) {
        this.diskresjonskode = diskresjonskode;
    }

    public String getFodested() {
        return fodested;
    }

    public void setFodested(String fodested) {
        this.fodested = fodested;
    }

    public boolean isBankkontoIUtland() {
        return bankkonto instanceof BankkontoUtland;
    }

    public boolean isBankkontoINorge() {
        return !isBankkontoIUtland();
    }

    public Bankkonto getBankkonto() {
        return bankkonto;
    }

    public void setBankkonto(Bankkonto bankkonto) {
        this.bankkonto = bankkonto;
    }

    public Adresselinje getBostedsadresse() {
        return bostedsadresse;
    }

    public void setBostedsadresse(Adresselinje bostedsadresse) {
        this.bostedsadresse = bostedsadresse;
    }

    public Adresselinje getPostadresse() {
        return postadresse;
    }

    public void setPostadresse(Adresselinje postadresse) {
        this.postadresse = postadresse;
    }

    public void setGjeldendePostadressetype(Kodeverdi gjeldendePostadressetype) {
        this.gjeldendePostadressetype = gjeldendePostadressetype;
    }

    public List<Familierelasjon> getHarFraRolleIList() {
        if (harFraRolleIList == null) {
            harFraRolleIList = new ArrayList<>();
        }
        return harFraRolleIList;
    }

    public void setHarFraRolleIList(List<Familierelasjon> harFraRolleIList) {
        this.harFraRolleIList = harFraRolleIList;
    }

    public boolean isAlternativAdresseIUtland() {
        return alternativAdresse instanceof AlternativAdresseUtland;
    }

    public boolean isAlternativAdresseINorge() {
        return alternativAdresse instanceof Adresse
                || alternativAdresse instanceof Matrikkeladresse
                || alternativAdresse instanceof Postboksadresse;
    }

    public Adresselinje getAlternativAdresse() {
        return alternativAdresse;
    }

    public void setAlternativAdresse(Adresselinje alternativAdresse) {
        this.alternativAdresse = alternativAdresse;
    }

    public AnsvarligEnhet getAnsvarligEnhet() {
        return ansvarligEnhet;
    }

    public void setAnsvarligEnhet(AnsvarligEnhet ansvarligEnhet) {
        this.ansvarligEnhet = ansvarligEnhet;
    }

    public LocalDateTime getDoedsdato() {
        return doedsdato;
    }

    public void setDoedsdato(LocalDateTime doedsdato) {
        this.doedsdato = doedsdato;
    }

    public boolean isDoed() {
        return doedsdato != null || (getBostatus() != null && "DØD".equals(getBostatus().getKodeRef()));
    }

    public Familierelasjon getBorMed() {
        for (Familierelasjon familierelasjon : getHarFraRolleIList()) {
            if (familierelasjon.getHarSammeBosted()
                    && (Familierelasjonstype.GIFT.name().equals(familierelasjon.getTilRolle())
                    || Familierelasjonstype.SAMBOER.name().equals(familierelasjon.getTilRolle())
                    || Familierelasjonstype.PARTNER.name().equals(familierelasjon.getTilRolle())
                    || Familierelasjonstype.EKTE.name().equals(familierelasjon.getTilRolle()))) {
                return familierelasjon;
            }
        }
        return null;
    }

    public Familierelasjon getEktefelleSamboer() {
        for (Familierelasjon familierelasjon : getHarFraRolleIList()) {
            if (Familierelasjonstype.GIFT.name().equals(familierelasjon.getTilRolle())
                    || Familierelasjonstype.SAMBOER.name().equals(familierelasjon.getTilRolle())
                    || Familierelasjonstype.PARTNER.name().equals(familierelasjon.getTilRolle())
                    || Familierelasjonstype.EKTE.name().equals(familierelasjon.getTilRolle())) {
                return familierelasjon;
            }
        }
        return null;
    }

    public String getBorMedPartner() {
        boolean borMedPartner = false;
        if (getEktefelleSamboer() != null) {
            borMedPartner = getEktefelleSamboer().getHarSammeBosted();
        }

        if (borMedPartner) {
            return "Ja";
        } else {
            return "Nei";
        }
    }

    public boolean isHarDiskresjonskode6Eller7() {
        return isHarDiskresjonskode()
                && (diskresjonskode.getKodeRef().equals(Diskresjonskoder.FORTROLIG_ADRESSE.getValue())
                || diskresjonskode.getKodeRef().equals(Diskresjonskoder.STRENGT_FORTROLIG_ADRESSE.getValue()));
    }

    public boolean isHarDiskresjonskode() {
        return diskresjonskode != null && diskresjonskode.getKodeRef() != null;
    }

    public boolean isHarBarnMedDiskresjonskode() {
        return getHarFraRolleIList()
                .stream()
                .anyMatch(new HarDiskresjonskode(Familierelasjonstype.BARN));
    }

    public boolean isHarForelderMedDiskresjonskode() {
        return getHarFraRolleIList()
                .stream()
                .anyMatch(new HarDiskresjonskode(Familierelasjonstype.FARA).or(new HarDiskresjonskode(Familierelasjonstype.MORA)));
    }

    public boolean isHarEktefelleMedDiskresjonskode() {
        return getHarFraRolleIList()
                .stream()
                .anyMatch(new HarDiskresjonskode(Familierelasjonstype.GIFT)
                        .or(new HarDiskresjonskode(Familierelasjonstype.EKTE))
                        .or(new HarDiskresjonskode(Familierelasjonstype.PARTNER))
                );
    }

    public boolean isHarSamboerMedDiskresjonskode() {
        return getHarFraRolleIList()
                .stream()
                .anyMatch(new HarDiskresjonskode(Familierelasjonstype.SAMBOER));
    }

    public Person getFar() {
        return getFamilierelasjon(Familierelasjonstype.FARA);
    }

    public Person getMor() {
        return getFamilierelasjon(Familierelasjonstype.MORA);
    }

    public int getAntallBarn() {
        int antallBarn = 0;
        for (Familierelasjon familierelasjon : getHarFraRolleIList()) {
            if (Familierelasjonstype.BARN.name().equals(familierelasjon.getTilRolle()) && familierelasjon.getTilPerson().getFodselsnummer().getAlder() <= BARN_ALDERSGRENSE) {
                antallBarn++;
            }
        }
        return antallBarn;
    }

    public List<Familierelasjon> getBarn() {
        List<Familierelasjon> barn = new ArrayList<>();
        for (Familierelasjon familierelasjon : getHarFraRolleIList()) {
            if (Familierelasjonstype.BARN.name().equals(familierelasjon.getTilRolle())
                    && familierelasjon.getTilPerson() != null
                    && familierelasjon.getTilPerson().getFodselsnummer() != null
                    && familierelasjon.getTilPerson().getFodselsnummer().getAlder() <= BARN_ALDERSGRENSE) {
                barn.add(familierelasjon);
            }
        }

        Collections.sort(barn, new Comparator<Familierelasjon>() {
            @Override
            public int compare(Familierelasjon o1, Familierelasjon o2) {
                if ((!o1.getHarSammeBosted() && !o2.getHarSammeBosted()) || (o1.getHarSammeBosted() && o2.getHarSammeBosted())) {
                    return o1.getTilPerson().getFodselsnummer().getAlder() - o2.getTilPerson().getFodselsnummer().getAlder();
                } else if (o1.getHarSammeBosted() && !o2.getHarSammeBosted()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        return barn;
    }

    public Person getFamilierelasjon(Familierelasjonstype familierelasjonstype) {
        for (Familierelasjon familierelasjon : getHarFraRolleIList()) {
            if (familierelasjonstype.name().equals(familierelasjon.getTilRolle())) {
                return familierelasjon.getTilPerson();
            }
        }
        return null;
    }

    public LocalDate getSivilstandFom() {
        return sivilstandFom;
    }

    public void setSivilstandFom(LocalDate sivilstandFom) {
        this.sivilstandFom = sivilstandFom;
    }

    public List<Kodeverdi> getTilrettelagtKommunikasjon() {
        if (tilrettelagtKommunikasjon == null) {
            setTilrettelagtKommunikasjon(new ArrayList<>());
        }
        return tilrettelagtKommunikasjon;
    }

    public void setTilrettelagtKommunikasjon(List<Kodeverdi> tilrettelagtKommunikasjon) {
        this.tilrettelagtKommunikasjon = tilrettelagtKommunikasjon;
    }

    public void setGeografiskTilknytning(GeografiskTilknytning geografiskTilknytning) {
        this.geografiskTilknytning = geografiskTilknytning;
    }

    public GeografiskTilknytning getGeografiskTilknytning() {
        return geografiskTilknytning;
    }

    public Optional<Telefon> getMobil() {
        return getTelefon("MOBI");
    }

    public Optional<Telefon> getHjemTlf() {
        return getTelefon("HJET");
    }

    public Optional<Telefon> getJobbTlf() {
        return getTelefon("ARBT");
    }

    public boolean harTelefonnummer() {
        return harTelefonnummer(getMobil()) || (harTelefonnummer(getJobbTlf()) || harTelefonnummer(getHjemTlf()));
    }

    public String getTelefonnummer() {
        Optional<Telefon> mobil = getMobil();
        Optional<Telefon> hjemTlf = getHjemTlf();
        Optional<Telefon> jobbTlf = getJobbTlf();

        if (mobil.isPresent()) {
            return mobil.get().getTelefonnummerMedRetningsnummer();
        } else if (hjemTlf.isPresent()) {
            return hjemTlf.get().getTelefonnummerMedRetningsnummer();
        } else if (jobbTlf.isPresent()) {
            return jobbTlf.get().getTelefonnummerMedRetningsnummer();
        } else {
            return "";
        }
    }

    private boolean harTelefonnummer(Optional<Telefon> mobil) {
        return mobil.isPresent() && mobil.get().harIdentifikator();
    }

    public boolean isHarTilrettelagtKommunikasjon() {
        return tilrettelagtKommunikasjon != null && !tilrettelagtKommunikasjon.isEmpty();
    }

    public Sikkerhetstiltak getSikkerhetstiltak() {
        return sikkerhetstiltak;
    }

    public void setSikkerhetstiltak(Sikkerhetstiltak sikkerhetstiltak) {
        this.sikkerhetstiltak = sikkerhetstiltak;
    }

    public boolean isHarSikkerhetstiltak() {
        return this.sikkerhetstiltak != null;
    }

    public void setKontaktinformasjon(List<Telefon> kontaktinformasjon) {
        this.kontaktinformasjon = kontaktinformasjon;
    }

    public Kodeverdi getGjeldendePostadressetype() {
        return gjeldendePostadressetype;
    }

    public List<Telefon> getTelefoner() {
        return kontaktinformasjon;
    }

    @Override
    public String toString() {
        return "Personfakta [personfaktaId=" + personfaktaId + ", personnavn="
                + personnavn + ", sivilstand=" + (sivilstand != null ? sivilstand.getKodeRef() : null) + ", adresse="
                + bostedsadresse + "]";
    }

    private Optional<Telefon> getTelefon(String value) {
        return kontaktinformasjon.stream()
                .filter(telefon -> telefon.getType().getKodeRef().equals(value))
                .findFirst();
    }
}
