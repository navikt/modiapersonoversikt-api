package no.nav.sbl.dialogarena.modiabrukerdialog.api.domain.pdl

import java.util.*

data class PdlPersonResponse(
        val errors: List<PdlError>?,
        val data: PdlHentPerson?
)

data class PdlIdentResponse(
        val errors: List<PdlError>?,
        val data: List<PdlIdenter>?
)

data class PdlError(
        val message: String,
        val locations: List<PdlErrorLocation>,
        val path: List<String>?,
        val extensions: PdlErrorExtension
)

data class PdlErrorLocation(
        val line: Int?,
        val column: Int?
)

data class PdlErrorExtension(
        val code: String?,
        val classification: String
)

data class PdlHentPerson(
        val hentPerson: PdlPerson?
)

data class PdlPerson(
        val navn: List<PdlPersonNavn>,
        val kontaktinformasjonForDoedsbo: List<PdlDoedsbo>?,
        val tilrettelagtKommunikasjon: List<PdlTilrettelagtKommunikasjon>?,
        val fullmakt: List<PdlFullmakt>?
)

data class PdlDoedsbo(
        val skifteform: String,
        val attestutstedelsesdato: Date,
        val personSomKontakt: PdlDoedsboPersonSomKontakt?,
        val advokatSomKontakt: PdlDoedsboAdvokatSomKontakt?,
        val organisasjonSomKontakt: PdlDoedsboOrganisasjonSomKontakt?,
        val adresse: PdlDoedsboAdresse
)

data class PdlDoedsboPersonSomKontakt(
        val foedselsdato: Date?,
        val identifikasjonsnummer: String?,
        val personnavn: PdlPersonNavn
)

data class PdlDoedsboOrganisasjonSomKontakt(
        val kontaktperson: PdlPersonNavn?,
        val organisasjonsnavn: String,
        val organisasjonsnummer: String?
)

data class PdlDoedsboAdvokatSomKontakt(
        val personnavn: PdlPersonNavn,
        val organisasjonsnavn: String?,
        val organisasjonsnummer: String?
)

data class PdlPersonNavn(
        val fornavn: String?,
        val mellomnavn: String?,
        val etternavn: String?
)

data class PdlDoedsboAdresse(
        val adresselinje1: String,
        val adresselinje2: String?,
        val poststedsnavn: String,
        val postnummer: String,
        val landkode: String?
)

data class PdlTilrettelagtKommunikasjon (
        val talespraaktolk: PdlTolk?,
        val tegnspraaktolk: PdlTolk?
)

data class PdlTolk(
        val spraak: String?
)

data class PdlFullmakt(
        val motpartsPersonident: String,
        val motpartsRolle: String,
        val omraader: List<String>,
        val gyldigFraOgMed: Date,
        val gyldigTilOgMed: Date
)

data class PdlIdenter(
        val ident: String,
        val gruppe: String,
        val historisk: Boolean
)


