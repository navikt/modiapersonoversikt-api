package no.nav.kjerneinfo.consumer.organisasjon

import no.nav.kjerneinfo.domain.organisasjon.Organisasjon
import java.util.*

interface OrganisasjonService {
    fun hentNoekkelinfo(orgnummer: String): Optional<Organisasjon>
}

class OrganisasjonServiceImpl(private val organisasjonV1Client: OrganisasjonV1Client) : OrganisasjonService {
    override fun hentNoekkelinfo(orgnummer: String): Optional<Organisasjon> {
        return Optional.ofNullable(organisasjonV1Client.hentNokkelInfo(orgnummer))
                .map { nokkelInfo ->
                    val formatterOrgNavn = formaterNavn(nokkelInfo.navn)
                    Organisasjon().withNavn(formatterOrgNavn)
                }
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
