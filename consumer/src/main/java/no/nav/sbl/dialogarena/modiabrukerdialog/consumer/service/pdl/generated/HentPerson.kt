package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.generated

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.types.GraphQLResponse
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List

const val HENT_PERSON: String =
    "query(${'$'}ident: ID!, ${'$'}navnHistorikk: Boolean!){\n  hentPerson(ident: ${'$'}ident) {\n\tnavn(historikk: ${'$'}navnHistorikk) {\n\t  fornavn\n\t  mellomnavn\n\t  etternavn\n\t  forkortetNavn\n\t  originaltNavn {\n\t    fornavn\n\t    mellomnavn\n\t    etternavn\n\t  }\n    }\n    kontaktinformasjonForDoedsbo {\n        skifteform\n        attestutstedelsesdato\n        personSomKontakt {\n            foedselsdato\n            personnavn {\n                fornavn\n                mellomnavn\n                etternavn\n            }\n            identifikasjonsnummer\n        }\n        advokatSomKontakt {\n            personnavn {\n                fornavn\n                mellomnavn\n                etternavn\n            }\n            organisasjonsnavn\n        }\n        organisasjonSomKontakt {\n            organisasjonsnavn\n            kontaktperson {\n                fornavn\n                mellomnavn\n                etternavn\n            }\n        }\n        adresse {\n            adresselinje1\n            adresselinje2\n            poststedsnavn\n            postnummer\n            landkode\n        }\n    }\n    tilrettelagtKommunikasjon {\n        talespraaktolk {\n            spraak\n        }\n        tegnspraaktolk {\n            spraak\n        }\n    }\n    fullmakt {\n        motpartsPersonident\n        motpartsRolle\n        omraader\n        gyldigFraOgMed\n        gyldigTilOgMed\n    }\n    telefonnummer {\n        landskode\n        nummer\n        prioritet\n        metadata {\n            endringer {\n                registrert\n                registrertAv\n            }\n        }\n    }\n  }\n}\n"

class HentPerson(
  private val graphQLClient: GraphQLClient<*>
) {
  suspend fun execute(variables: HentPerson.Variables): GraphQLResponse<HentPerson.Result> =
      graphQLClient.execute(HENT_PERSON, null, variables)

  data class Variables(
    val ident: ID,
    val navnHistorikk: Boolean
  )

  data class OriginaltNavn(
    val fornavn: String?,
    val mellomnavn: String?,
    val etternavn: String?
  )

  data class Navn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String,
    val forkortetNavn: String?,
    val originaltNavn: HentPerson.OriginaltNavn?
  )

  enum class KontaktinformasjonForDoedsboSkifteform {
    OFFENTLIG,

    ANNET,

    /**
     * This is a default enum value that will be used when attempting to deserialize unknown value.
     */
    @JsonEnumDefaultValue
    __UNKNOWN_VALUE
  }

  data class Personnavn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String
  )

  data class KontaktinformasjonForDoedsboPersonSomKontakt(
    val foedselsdato: Date?,
    val personnavn: HentPerson.Personnavn?,
    val identifikasjonsnummer: String?
  )

  data class KontaktinformasjonForDoedsboAdvokatSomKontakt(
    val personnavn: HentPerson.Personnavn,
    val organisasjonsnavn: String?
  )

  data class KontaktinformasjonForDoedsboOrganisasjonSomKontakt(
    val organisasjonsnavn: String,
    val kontaktperson: HentPerson.Personnavn?
  )

  data class KontaktinformasjonForDoedsboAdresse(
    val adresselinje1: String,
    val adresselinje2: String?,
    val poststedsnavn: String,
    val postnummer: String,
    val landkode: String?
  )

  data class KontaktinformasjonForDoedsbo(
    val skifteform: HentPerson.KontaktinformasjonForDoedsboSkifteform,
    val attestutstedelsesdato: Date,
    val personSomKontakt: HentPerson.KontaktinformasjonForDoedsboPersonSomKontakt?,
    val advokatSomKontakt: HentPerson.KontaktinformasjonForDoedsboAdvokatSomKontakt?,
    val organisasjonSomKontakt: HentPerson.KontaktinformasjonForDoedsboOrganisasjonSomKontakt?,
    val adresse: HentPerson.KontaktinformasjonForDoedsboAdresse
  )

  data class Tolk(
    val spraak: String?
  )

  data class TilrettelagtKommunikasjon(
    val talespraaktolk: HentPerson.Tolk?,
    val tegnspraaktolk: HentPerson.Tolk?
  )

  enum class FullmaktsRolle {
    FULLMAKTSGIVER,

    FULLMEKTIG,

    /**
     * This is a default enum value that will be used when attempting to deserialize unknown value.
     */
    @JsonEnumDefaultValue
    __UNKNOWN_VALUE
  }

  data class Fullmakt(
    val motpartsPersonident: String,
    val motpartsRolle: HentPerson.FullmaktsRolle,
    val omraader: List<String>,
    val gyldigFraOgMed: Date,
    val gyldigTilOgMed: Date
  )

  data class Endring(
    val registrert: DateTime,
    val registrertAv: String
  )

  data class Metadata(
    val endringer: List<HentPerson.Endring>
  )

  data class Telefonnummer(
    val landskode: String,
    val nummer: String,
    val prioritet: Int,
    val metadata: HentPerson.Metadata
  )

  data class Person(
    val navn: List<HentPerson.Navn>,
    val kontaktinformasjonForDoedsbo: List<HentPerson.KontaktinformasjonForDoedsbo>,
    val tilrettelagtKommunikasjon: List<HentPerson.TilrettelagtKommunikasjon>,
    val fullmakt: List<HentPerson.Fullmakt>,
    val telefonnummer: List<HentPerson.Telefonnummer>
  )

  data class Result(
    val hentPerson: HentPerson.Person?
  )
}
