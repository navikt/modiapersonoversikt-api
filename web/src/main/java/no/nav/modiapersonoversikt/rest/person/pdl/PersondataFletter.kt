package no.nav.modiapersonoversikt.rest.person.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.norg.AnsattEnhet
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondata.AdressebeskyttelseGradering.*
import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondataLite
import no.nav.modiapersonoversikt.service.dkif.Dkif
import no.nav.tjeneste.virksomhet.person.v3.meldinger.HentPersonResponse
import java.time.LocalDate

// TODO TEMP KODEVERK
interface KodeverkKilde
class FelleskodeverkKilde(val kodeverk: String) : KodeverkKilde
enum class Kodeverk(val kilde: KodeverkKilde) {
    KJONN(FelleskodeverkKilde("Kjønnstyper")),
    LAND(FelleskodeverkKilde("Landkoder")),
    DISKRESJONSKODER(FelleskodeverkKilde("Diskresjonskoder"))
}

interface KodeverkService {
    fun hentVerdi(kodeverk: Kodeverk, kodeterm: String, sprak: String = "nb"): String
}

class PersondataFletter(val kodeverk: KodeverkService) {
    data class Data(
        val persondata: HentPersondata.Person,
        val geografiskeTilknytning: Persondata.Result<String?>,
        val erEgenAnsatt: Persondata.Result<Boolean>,
        val navEnhet: Persondata.Result<AnsattEnhet>,
        val dkifData: Persondata.Result<Dkif.DigitalKontaktinformasjon>,
        val bankkonto: Persondata.Result<HentPersonResponse>,
        val tredjepartsPerson: Persondata.Result<List<HentPersondataLite.HentPersonBolkResult>>
    ) {
        private val ekstraDatapunker = listOf(
            geografiskeTilknytning,
            erEgenAnsatt,
            navEnhet,
            dkifData,
            bankkonto,
            tredjepartsPerson
        )

        fun feilendeSystemer(): List<String> {
            return ekstraDatapunker.mapNotNull {
                if (it is Persondata.Result.Failure<*>) {
                    it.system
                } else {
                    null
                }
            }
        }
    }

    fun flettSammenData(data: Data): Persondata.Data {
        return Persondata.Data(
            feilendeSystemer = data.feilendeSystemer(),
            person = Persondata.Person(
                fnr = hentFnr(data),
                navn = hentNavn(data),
                kjonn = hentKjonn(data),
                fodselsdato = hentFodselsdato(data),
                dodsdato = hentDodsdato(data),
                bostedAdresse = hentBostedAdresse(data),
                kontaktAdresse = hentKontaktAdresse(data),
                navEnhet = hentNavEnhet(data),
                statsborgerskap = hentStatsborgerskap(data),
                adressebeskyttelse = hentAdressebeskyttelse(data),
                sikkerhetstiltak = hentSikkerhetstiltak(data),
                erEgenAnsatt = hentErEgenAnsatt(data),
                personstatus = hentPersonstatus(data),
                sivilstand = hentSivilstand(data),
                foreldreansvar = hentForeldreansvar(data),
                deltBosted = hentDeltBosted(data),
                dodsbo = hentDodsbo(data),
                fullmakt = hentFullmakt(data),
                vergemal = hentVergemal(data),
                tilrettelagtKommunikasjon = hentTilrettelagtKommunikasjon(data),
                telefonnummer = hentTelefonnummer(data),
                kontaktOgReservasjon = hentKontaktOgReservasjon(data),
                bankkonto = hentBankkonto(data)
            )
        )
    }

    private fun hentFnr(data: Data): String {
        return data.persondata.folkeregisteridentifikator
            .filter { it.type == "FNR" }
            .first { it.status == "I_BRUK" }
            .identifikasjonsnummer
    }

    private fun hentNavn(data: Data): Persondata.Navn {
        val navn = data.persondata.navn.first()
        return Persondata.Navn(
            fornavn = navn.fornavn,
            mellomnavn = navn.mellomnavn,
            etternavn = navn.etternavn
        )
    }

    private fun hentKjonn(data: Data): Persondata.KodeBeskrivelse<Persondata.Kjonn> {
        val kjonn = data.persondata.kjoenn.first().kjoenn
        return when (kjonn) {
            HentPersondata.KjoennType.MANN -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.M)
            HentPersondata.KjoennType.KVINNE -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.K)
            else -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.U)
        }
    }

    private fun hentFodselsdato(data: PersondataFletter.Data): LocalDate? {
        return data.persondata.foedsel.first().foedselsdato?.value
    }

    private fun hentDodsdato(data: PersondataFletter.Data): LocalDate? {
        return data.persondata.doedsfall.first().doedsdato?.value
    }

    private fun hentBostedAdresse(data: Data): Persondata.Adresse? {
        TODO("Not yet implemented")
    }

    private fun hentKontaktAdresse(data: Data): Persondata.Adresse? {
        TODO("Not yet implemented")
    }

    private fun hentNavEnhet(data: Data): Persondata.Enhet? {
        return data.navEnhet
            .map { Persondata.Enhet(it.enhetId, it.enhetNavn) }
            .getOrNull()
    }

    private fun hentStatsborgerskap(data: Data): List<Persondata.Statsborgerskap> {
        return data.persondata.statsborgerskap.map {
            val land = when (it.land) {
                "XUK" -> Persondata.KodeBeskrivelse("XUK", "Ukjent")
                else -> kodeverk.hentKodeBeskrivelse(Kodeverk.LAND, it.land)
            }
            Persondata.Statsborgerskap(
                land = land,
                gyldigFraOgMed = it.gyldigFraOgMed?.value,
                gyldigTilOgMed = it.gyldigTilOgMed?.value
            )
        }
    }

    private fun hentAdressebeskyttelse(data: Data): Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse> {
        val kodebeskrivelse = when (data.persondata.adressebeskyttelse.first().gradering) {
            STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG -> kodeverk.hentKodeBeskrivelse(
                Kodeverk.DISKRESJONSKODER,
                "SPSF"
            )
            FORTROLIG -> kodeverk.hentKodeBeskrivelse(Kodeverk.DISKRESJONSKODER, "SPSO")
            UGRADERT -> Persondata.KodeBeskrivelse("", "Ugradert")
            else -> Persondata.KodeBeskrivelse("", "Ukjent")
        }
        val adressebeskyttelse = when (data.persondata.adressebeskyttelse.first().gradering) {
            STRENGT_FORTROLIG_UTLAND -> Persondata.AdresseBeskyttelse.KODE6_UTLAND
            STRENGT_FORTROLIG -> Persondata.AdresseBeskyttelse.KODE6
            FORTROLIG -> Persondata.AdresseBeskyttelse.KODE7
            UGRADERT -> Persondata.AdresseBeskyttelse.UGRADERT
            else -> Persondata.AdresseBeskyttelse.UKJENT
        }
        return Persondata.KodeBeskrivelse(kode = adressebeskyttelse, beskrivelse = kodebeskrivelse.beskrivelse)
    }

    private fun hentSikkerhetstiltak(data: Data): List<Persondata.Sikkerhetstiltak> {
        return data.persondata.sikkerhetstiltak.map {
            Persondata.Sikkerhetstiltak(
                type = Persondata.SikkerhetstiltakType.valueOf(it.tiltakstype),
                gyldigFraOgMed = it.gyldigFraOgMed.value,
                gyldigTilOgMed = it.gyldigTilOgMed.value
            )
        }
    }

    private fun hentErEgenAnsatt(data: Data): Persondata.EgenAnsatt {
        return data.erEgenAnsatt
            .map {
                if (it) {
                    Persondata.EgenAnsatt.JA
                } else {
                    Persondata.EgenAnsatt.NEI
                }
            }.getOrElse(Persondata.EgenAnsatt.UKJENT)
    }

    private fun hentPersonstatus(data: Data): Persondata.KodeBeskrivelse<Persondata.PersonStatus> {
        TODO("Not yet implemented")
    }

    private fun hentSivilstand(data: Data): Persondata.Sivilstand {
        TODO("Not yet implemented")
    }

    private fun hentForeldreansvar(data: Data): List<Persondata.Foreldreansvar> {
        TODO("Not yet implemented")
    }

    private fun hentDeltBosted(data: Data): List<Persondata.DeltBosted> {
        TODO("Not yet implemented")
    }

    private fun hentDodsbo(data: PersondataFletter.Data): List<Persondata.Dodsbo> {
        TODO("Not yet implemented")
    }

    private fun hentFullmakt(data: PersondataFletter.Data): List<Persondata.Fullmakt> {
        TODO("Not yet implemented")
    }

    private fun hentVergemal(data: PersondataFletter.Data): List<Persondata.Verge> {
        TODO("Not yet implemented")
    }

    private fun hentTilrettelagtKommunikasjon(data: Data): List<Persondata.TilrettelagtKommunikasjon> {
        TODO("Not yet implemented")
    }

    private fun hentTelefonnummer(data: Data): List<Persondata.Telefon> {
        TODO("Not yet implemented")
    }

    private fun hentKontaktOgReservasjon(data: Data): Dkif.DigitalKontaktinformasjon? {
        TODO("Not yet implemented")
    }

    private fun hentBankkonto(data: Data): Persondata.Bankkonto {
        TODO("Not yet implemented")
    }

    private fun <T> KodeverkService.hentKodeBeskrivelse(
        kodeverk: Kodeverk,
        termnavn: T,
        sprak: String = "nb"
    ): Persondata.KodeBeskrivelse<T> {
        val beskrivelse = this.hentVerdi(kodeverk, termnavn.toString(), sprak)
        return Persondata.KodeBeskrivelse(
            kode = termnavn,
            beskrivelse = beskrivelse
        )
    }
}
