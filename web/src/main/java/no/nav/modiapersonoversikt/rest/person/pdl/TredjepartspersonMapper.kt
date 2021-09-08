package no.nav.modiapersonoversikt.rest.person.pdl

import no.nav.modiapersonoversikt.legacy.api.domain.pdl.generated.HentPersondataLite

class TredjepartspersonMapper(val kodeverk: KodeverkService) {
    fun lagTredjepartsperson(
        person: HentPersondataLite.HentPersonBolkResult,
        tilganger: PersondataService.Tilganger
    ): Persondata.TredjepartsPerson {
        return Persondata.TredjepartsPerson(
            fnr = person.ident,
            navn = person.person?.navn?.firstOrNull()?.let {
                Persondata.Navn(
                    fornavn = it.fornavn,
                    mellomnavn = it.mellomnavn,
                    etternavn = it.etternavn
                )
            },
            adressebeskyttelse = person.person?.adressebeskyttelse?.let(::hentAdressebeskyttelse),
            bostedAdresse = person.person?.bostedsadresse?.let {
                if (person.harTilgang(tilganger)) {
                    hentBostedAdresse(it)
                } else {
                    null
                }
            }
        )
    }

    private fun hentBostedAdresse(adresser: List<HentPersondataLite.Bostedsadresse>): Persondata.Adresse? {
        val adresse = adresser.firstOrNull() ?: return null
        return when {
            adresse.vegadresse != null -> lagAdresseFraVegadresse(adresse.vegadresse!!)
            adresse.matrikkeladresse != null -> lagAdresseFraMatrikkeladresse(adresse.matrikkeladresse!!)
            adresse.utenlandskAdresse != null -> lagAdresseFraUtenlandskAdresse(adresse.utenlandskAdresse!!)
            adresse.ukjentBosted != null -> Persondata.Adresse(adresse.ukjentBosted?.bostedskommune ?: "Ukjent kommune")
            else -> null
        }
    }

    private fun lagAdresseFraVegadresse(adresse: HentPersondataLite.Vegadresse): Persondata.Adresse? {
        return Persondata.Adresse(
            linje1 = listOf(
                adresse.adressenavn,
                adresse.husnummer,
                adresse.husbokstav,
                adresse.bruksenhetsnummer
            ),
            linje2 = listOf(
                adresse.postnummer,
                adresse.postnummer?.let { kodeverk.hentVerdi(Kodeverk.POSTNUMMER, it) }
            ),
            linje3 = listOf(
                adresse.bydelsnummer,
                adresse.kommunenummer
            )
        )
    }

    private fun lagAdresseFraMatrikkeladresse(adresse: HentPersondataLite.Matrikkeladresse): Persondata.Adresse? {
        return Persondata.Adresse(
            linje1 = listOf(
                adresse.bruksenhetsnummer,
                adresse.tilleggsnavn
            ),
            linje2 = listOf(
                adresse.postnummer,
                adresse.kommunenummer
            )
        )
    }

    private fun lagAdresseFraUtenlandskAdresse(adresse: HentPersondataLite.UtenlandskAdresse): Persondata.Adresse? {
        return Persondata.Adresse(
            linje1 = listOf(
                adresse.postboksNummerNavn,
                adresse.adressenavnNummer,
                adresse.bygningEtasjeLeilighet
            ),
            linje2 = listOf(
                adresse.postkode,
                adresse.bySted,
                adresse.regionDistriktOmraade
            ),
            linje3 = listOf(
                kodeverk.hentVerdi(Kodeverk.LAND, adresse.landkode)
            )
        )
    }

    fun hentAdressebeskyttelse(adressebeskyttelseListe: List<HentPersondataLite.Adressebeskyttelse>): Persondata.KodeBeskrivelse<Persondata.AdresseBeskyttelse> {
        val kodebeskrivelse = when (adressebeskyttelseListe.first().gradering) {
            HentPersondataLite.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND,
            HentPersondataLite.AdressebeskyttelseGradering.STRENGT_FORTROLIG -> kodeverk.hentKodeBeskrivelse(
                Kodeverk.DISKRESJONSKODER,
                "SPSF"
            )
            HentPersondataLite.AdressebeskyttelseGradering.FORTROLIG -> kodeverk.hentKodeBeskrivelse(
                Kodeverk.DISKRESJONSKODER,
                "SPSO"
            )
            HentPersondataLite.AdressebeskyttelseGradering.UGRADERT -> Persondata.KodeBeskrivelse("", "Ugradert")
            else -> Persondata.KodeBeskrivelse("", "Ukjent")
        }
        val adressebeskyttelse = when (adressebeskyttelseListe.first().gradering) {
            HentPersondataLite.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND -> Persondata.AdresseBeskyttelse.KODE6_UTLAND
            HentPersondataLite.AdressebeskyttelseGradering.STRENGT_FORTROLIG -> Persondata.AdresseBeskyttelse.KODE6
            HentPersondataLite.AdressebeskyttelseGradering.FORTROLIG -> Persondata.AdresseBeskyttelse.KODE7
            HentPersondataLite.AdressebeskyttelseGradering.UGRADERT -> Persondata.AdresseBeskyttelse.UGRADERT
            else -> Persondata.AdresseBeskyttelse.UKJENT
        }
        return Persondata.KodeBeskrivelse(kode = adressebeskyttelse, beskrivelse = kodebeskrivelse.beskrivelse)
    }

    fun HentPersondataLite.HentPersonBolkResult.harTilgang(tilganger: PersondataService.Tilganger): Boolean {
        val person = this.person ?: return false
        var kode = 0
        val adressebeskyttelse = person.adressebeskyttelse
        for (beskyttelse in adressebeskyttelse) {
            if (beskyttelse.gradering == HentPersondataLite.AdressebeskyttelseGradering.STRENGT_FORTROLIG_UTLAND) {
                kode = 6
                break
            } else if (beskyttelse.gradering == HentPersondataLite.AdressebeskyttelseGradering.STRENGT_FORTROLIG) {
                kode = 6
                break
            } else if (beskyttelse.gradering == HentPersondataLite.AdressebeskyttelseGradering.FORTROLIG) {
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
}
