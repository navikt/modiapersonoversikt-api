package no.nav.sbl.dialogarena.modiabrukerdialog.consumer.service.pdl.generated

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.types.GraphQLResponse
import io.ktor.client.request.HttpRequestBuilder
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

const val HENT_NAVN: String =
    "query(${'$'}ident: ID!, ${'$'}navnHistorikk: Boolean!){\n  hentPerson(ident: ${'$'}ident) {\n\tnavn(historikk: ${'$'}navnHistorikk) {\n\t  fornavn\n\t  mellomnavn\n\t  etternavn\n    }\n  }\n}"

class HentNavn(
  private val graphQLClient: GraphQLClient<*>
) {
  suspend fun execute(variables: HentNavn.Variables, config: HttpRequestBuilder.() -> Unit = {}): GraphQLResponse<HentNavn.Result> =
      graphQLClient.execute(HENT_NAVN, null, variables)

  data class Variables(
    val ident: ID,
    val navnHistorikk: Boolean
  )

  data class Navn(
    val fornavn: String,
    val mellomnavn: String?,
    val etternavn: String
  )

  data class Person(
    val navn: List<HentNavn.Navn>
  )

  data class Result(
    val hentPerson: HentNavn.Person?
  )
}
