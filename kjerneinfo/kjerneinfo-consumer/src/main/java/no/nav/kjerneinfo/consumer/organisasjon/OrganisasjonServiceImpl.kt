package no.nav.kjerneinfo.consumer.organisasjon

import no.nav.kjerneinfo.domain.organisasjon.Organisasjon
import java.util.*

interface OrganisasjonService {
    fun hentNoekkelinfo(orgnummer: String?): Optional<Organisasjon>
}

class OrganisasjonServiceImpl(private val organisasjonV1RestClient: OrganisasjonV1RestClient) : OrganisasjonService {
    override fun hentNoekkelinfo(orgnummer: String?): Optional<Organisasjon> {
        val formatterOrgNavn = formaterNavn(organisasjonV1RestClient.hentKjernInfoFraRestClient(orgnummer!!).navn.navnelinje1)
        return Optional.of(Organisasjon().withNavn(formatterOrgNavn))
    }

    private fun formaterNavn(wsNavn: String): String {
        return java.lang.String.join(" ", wsNavn)
    }
}
