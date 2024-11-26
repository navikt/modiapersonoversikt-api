package no.nav.modiapersonoversiktproxy.consumer.ereg

data class Organisasjon(
    val navn: String,
)

interface OrganisasjonService {
    fun hentNoekkelinfo(orgnummer: String): Organisasjon?
}

class OrganisasjonServiceImpl(
    private val organisasjonV1Client: OrganisasjonV1Client,
) : OrganisasjonService {
    override fun hentNoekkelinfo(orgnummer: String): Organisasjon? =
        organisasjonV1Client.hentNokkelInfo(orgnummer)?.let { Organisasjon(formaterNavn(it.navn)) }

    private fun formaterNavn(orgNavn: OrgNavn): String =
        listOfNotNull(
            orgNavn.navnelinje1,
            orgNavn.navnelinje2,
            orgNavn.navnelinje3,
            orgNavn.navnelinje4,
            orgNavn.navnelinje5,
        ).joinToString(" ")
}
