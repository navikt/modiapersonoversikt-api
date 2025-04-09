package no.nav.modiapersonoversikt.rest.ereg

import no.nav.modiapersonoversikt.consumer.ereg.Organisasjon
import no.nav.modiapersonoversikt.consumer.ereg.OrganisasjonService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest")
class OrganisasjonController
    @Autowired
    constructor(
        private val organisasjonService: OrganisasjonService,
    ) {
        @PostMapping("/organisasjonInfo")
        fun hentOrganisasjonInfo(
            @RequestBody orgnummer: String,
        ): Organisasjon? = organisasjonService.hentNoekkelinfo(orgnummer)
    }
