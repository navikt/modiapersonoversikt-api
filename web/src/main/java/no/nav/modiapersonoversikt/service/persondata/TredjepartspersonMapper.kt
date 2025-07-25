package no.nav.modiapersonoversikt.service.persondata

import no.nav.modiapersonoversikt.consumer.krr.Krr
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.AdressebeskyttelseGradering
import no.nav.modiapersonoversikt.consumer.pdl.generated.enums.KjoennType
import no.nav.modiapersonoversikt.consumer.pdl.generated.henttredjepartspersondata.*
import no.nav.modiapersonoversikt.service.enhetligkodeverk.EnhetligKodeverk
import java.time.LocalDate
import java.time.Period
import no.nav.modiapersonoversikt.service.enhetligkodeverk.KodeverkConfig as Kodeverk

class TredjepartspersonMapper(
    private val kodeverk: EnhetligKodeverk.Service,
) {
    fun lagTredjepartsperson(
        ident: String,
        person: Person?,
        tilganger: PersondataService.Tilganger,
        kontaktinformasjonTredjepartsperson: Persondata.DigitalKontaktinformasjonTredjepartsperson?,
    ): Persondata.TredjepartsPerson? {
        if (person == null) return null
        val fodselsdato = person.foedselsdato.mapNotNull { it.foedselsdato }
        val harTilgang = person.harTilgang(tilganger)
        return Persondata.TredjepartsPerson(
            fnr = ident,
            navn =
                person.navn
                    .prioriterKildesystem()
                    .mapNotNull {
                        if (harTilgang) {
                            Persondata.Navn(
                                fornavn = it.fornavn,
                                mellomnavn = it.mellomnavn,
                                etternavn = it.etternavn,
                            )
                        } else {
                            null
                        }
                    },
            fodselsdato = if (harTilgang) fodselsdato else emptyList(),
            alder = if (harTilgang) hentAlder(fodselsdato) else null,
            kjonn = if (harTilgang) hentKjonn(person) else emptyList(),
            adressebeskyttelse = hentAdressebeskyttelse(person.adressebeskyttelse),
            bostedAdresse =
                person.bostedsadresse.mapNotNull {
                    if (harTilgang) {
                        hentBostedAdresse(it)
                    } else {
                        null
                    }
                },
            dodsdato = person.doedsfall.mapNotNull { it.doedsdato },
            digitalKontaktinformasjon = kontaktinformasjonTredjepartsperson,
        )
    }

    fun tilKontaktinformasjonTredjepartsperson(
        krrInfo: Krr.DigitalKontaktinformasjon,
    ): Persondata.DigitalKontaktinformasjonTredjepartsperson =
        Persondata.DigitalKontaktinformasjonTredjepartsperson(
            mobiltelefonnummer = krrInfo.mobiltelefonnummer?.value,
            reservasjon = krrInfo.reservasjon?.value,
        )

    private fun hentKjonn(person: Person?): List<Persondata.KodeBeskrivelse<Persondata.Kjonn>> =
        person?.kjoenn?.map {
            when (it.kjoenn) {
                KjoennType.MANN -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.M)
                KjoennType.KVINNE -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.K)
                else -> kodeverk.hentKodeBeskrivelse(Kodeverk.KJONN, Persondata.Kjonn.U)
            }
        } ?: emptyList()

    private fun hentAlder(fodselsdato: List<LocalDate>): Int? =
        fodselsdato
            .firstOrNull()
            ?.let {
                Period.between(it, LocalDate.now()).years
            }

    private fun hentBostedAdresse(adresse: Bostedsadresse): Persondata.Adresse? =
        when {
            adresse.vegadresse != null -> lagAdresseFraVegadresse(adresse.vegadresse!!)
            adresse.matrikkeladresse != null -> lagAdresseFraMatrikkeladresse(adresse.matrikkeladresse!!)
            adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(adresse.utenlandskAdresse!!)
            adresse.ukjentBosted != null ->
                Persondata.Adresse(
                    linje1 = adresse.ukjentBosted?.bostedskommune ?: "Ukjent kommune",
                    sistEndret = null,
                )
            else -> null
        }

    private fun lagAdresseFraVegadresse(adresse: Vegadresse): Persondata.Adresse =
        Persondata.Adresse(
            linje1 =
                listOf(
                    adresse.adressenavn,
                    adresse.husnummer,
                    adresse.husbokstav,
                    adresse.bruksenhetsnummer,
                ),
            linje2 =
                listOf(
                    adresse.postnummer,
                    adresse.postnummer?.let { kodeverk.hentKodeverk(Kodeverk.POSTNUMMER).hentVerdi(it, it) },
                ),
            sistEndret = null,
        )

    private fun lagAdresseFraMatrikkeladresse(adresse: Matrikkeladresse): Persondata.Adresse =
        Persondata.Adresse(
            linje1 =
                listOf(
                    adresse.bruksenhetsnummer,
                    adresse.tilleggsnavn,
                ),
            linje2 =
                listOf(
                    adresse.postnummer,
                    adresse.kommunenummer,
                ),
            sistEndret = null,
        )

    private fun lagAdresseFraUtenlandskAdresse(adresse: UtenlandskAdresse): Persondata.Adresse =
        Persondata.Adresse(
            linje1 =
                listOf(
                    adresse.postboksNummerNavn,
                    adresse.adressenavnNummer,
                    adresse.bygningEtasjeLeilighet,
                ),
            linje2 =
                listOf(
                    adresse.postkode,
                    adresse.bySted,
                    adresse.regionDistriktOmraade,
                ),
            linje3 =
                listOf(
                    kodeverk.hentKodeverk(Kodeverk.LAND).hentVerdi(adresse.landkode, adresse.landkode),
                ),
            sistEndret = null,
        )

    private fun hentAdressebeskyttelse(
        adressebeskyttelseListe: List<Adressebeskyttelse>,
    ): List<Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse>> =
        adressebeskyttelseListe.map {
            val kodebeskrivelse =
                when (it.gradering) {
                    AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND, AdressebeskyttelseGradering.STRENGT_FORTROLIG ->
                        kodeverk.hentKodeBeskrivelse(
                            Kodeverk.DISKRESJONSKODER,
                            "SPSF",
                        )
                    AdressebeskyttelseGradering.FORTROLIG -> kodeverk.hentKodeBeskrivelse(Kodeverk.DISKRESJONSKODER, "SPFO")
                    AdressebeskyttelseGradering.UGRADERT -> Persondata.KodeBeskrivelse("", "Ugradert")
                    else -> Persondata.KodeBeskrivelse("", "Ukjent")
                }
            val adressebeskyttelse =
                when (it.gradering) {
                    AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND -> Persondata.AdresseBeskyttelse.KODE6_UTLAND
                    AdressebeskyttelseGradering.STRENGT_FORTROLIG -> Persondata.AdresseBeskyttelse.KODE6
                    AdressebeskyttelseGradering.FORTROLIG -> Persondata.AdresseBeskyttelse.KODE7
                    AdressebeskyttelseGradering.UGRADERT -> Persondata.AdresseBeskyttelse.UGRADERT
                    else -> Persondata.AdresseBeskyttelse.UKJENT
                }
            Persondata.KodeBeskrivelse(kode = adressebeskyttelse, beskrivelse = kodebeskrivelse.beskrivelse)
        }

    private fun Person?.harTilgang(tilganger: PersondataService.Tilganger): Boolean {
        val person = this ?: return false
        var kode = 0
        val adressebeskyttelse = person.adressebeskyttelse
        for (beskyttelse in adressebeskyttelse) {
            if (beskyttelse.gradering == AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) {
                kode = 6
                break
            } else if (beskyttelse.gradering == AdressebeskyttelseGradering.STRENGT_FORTROLIG) {
                kode = 6
                break
            } else if (beskyttelse.gradering == AdressebeskyttelseGradering.FORTROLIG) {
                kode = 7
                break
            }
        }

        return when (kode) {
            6 -> tilganger.kode6
            7 -> tilganger.kode7
            else -> true
        }
    }

    private fun List<Navn>.prioriterKildesystem(): List<Navn> = this.sortedBy { MasterPrioritet[it.metadata.master] }
}
