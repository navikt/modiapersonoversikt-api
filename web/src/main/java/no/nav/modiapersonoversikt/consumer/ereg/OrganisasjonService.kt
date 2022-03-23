package no.nav.modiapersonoversikt.consumer.ereg

import java.util.*

data class Organisasjon(
    val navn: String
)
interface OrganisasjonService {
    fun hentNoekkelinfo(orgnummer: String): Optional<Organisasjon>
}

class OrganisasjonServiceImpl(private val organisasjonV1Client: OrganisasjonV1Client) : OrganisasjonService {
    override fun hentNoekkelinfo(orgnummer: String): Optional<Organisasjon> {
        return Optional.ofNullable(organisasjonV1Client.hentNokkelInfo(orgnummer))
            .map { nokkelInfo -> Organisasjon(formaterNavn(nokkelInfo.navn)) }
    }

    private fun formaterNavn(orgNavn: OrgNavn): String {
        return listOfNotNull(
            orgNavn.navnelinje1,
            orgNavn.navnelinje2,
            orgNavn.navnelinje3,
            orgNavn.navnelinje4,
            orgNavn.navnelinje5
        ).joinToString(" ")
    }
}
