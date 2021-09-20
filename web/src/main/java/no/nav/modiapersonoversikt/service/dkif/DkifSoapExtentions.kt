package no.nav.modiapersonoversikt.service.dkif

import no.nav.tjeneste.virksomhet.digitalkontaktinformasjon.v1.meldinger.WSHentDigitalKontaktinformasjonResponse
import java.time.LocalDate
import javax.xml.datatype.XMLGregorianCalendar

object DkifSoapExtentions {
    fun WSHentDigitalKontaktinformasjonResponse.fromDTO() = Dkif.DigitalKontaktinformasjon(
        personident = this.digitalKontaktinformasjon.personident,
        reservasjon = this.digitalKontaktinformasjon.reservasjon,
        mobiltelefonnummer = this.getMobilnummer(),
        epostadresse = this.getEpostAdresse()
    )

    @JvmStatic
    fun responseFromDTO(response: WSHentDigitalKontaktinformasjonResponse) = response.fromDTO()

    private fun WSHentDigitalKontaktinformasjonResponse.getMobilnummer(): Dkif.MobilTelefon? {
        val mobiltelefonnummer = this.digitalKontaktinformasjon.mobiltelefonnummer ?: return null
        return Dkif.MobilTelefon(
            value = mobiltelefonnummer.value,
            sistOppdatert = mobiltelefonnummer.sistOppdatert.toLocalDate(),
            sistVerifisert = mobiltelefonnummer.sistVerifisert.toLocalDate()
        )
    }

    private fun WSHentDigitalKontaktinformasjonResponse.getEpostAdresse(): Dkif.Epostadresse? {
        val epostadresse = this.digitalKontaktinformasjon.epostadresse ?: return null
        return Dkif.Epostadresse(
            value = epostadresse.value,
            sistOppdatert = epostadresse.sistOppdatert.toLocalDate(),
            sistVerifisert = epostadresse.sistVerifisert.toLocalDate()
        )
    }

    private fun XMLGregorianCalendar?.toLocalDate(): LocalDate? {
        return if (this == null) {
            null
        } else {
            LocalDate.of(
                this.year,
                this.month,
                this.day
            )
        }
    }
}
