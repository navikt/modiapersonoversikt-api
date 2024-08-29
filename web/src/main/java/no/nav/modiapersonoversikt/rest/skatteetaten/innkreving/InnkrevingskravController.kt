package no.nav.modiapersonoversikt.rest.skatteetaten.innkreving

import no.nav.common.types.identer.Fnr
import no.nav.modiapersonoversikt.rest.common.FnrRequest
import no.nav.modiapersonoversikt.rest.skatteetaten.innkreving.json.InnkrevingskravJsonResponse
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.InnkrevingskravId
import no.nav.modiapersonoversikt.service.skatteetaten.innkreving.InnkrevingskravService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/rest/innkrevingskrav")
class InnkrevingskravController(
    private val innkrevingskravService: InnkrevingskravService,
) {
    @GetMapping("/{innkrevingskravId}")
    fun hentInnkrevingskrav(
        @PathVariable innkrevingskravId: String,
    ): ResponseEntity<InnkrevingskravJsonResponse> {
        val innkrevingskrav = innkrevingskravService.hentInnkrevingskrav(InnkrevingskravId(innkrevingskravId))

        return if (innkrevingskrav == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(InnkrevingskravJsonResponse.fromDomain(innkrevingskrav))
        }
    }

    @PostMapping
    fun hentAlleInnkrevingskrav(
        @RequestBody fnrRequest: FnrRequest,
    ): ResponseEntity<List<InnkrevingskravJsonResponse>> {
        val fnr = Fnr(fnrRequest.fnr)

        if (!Fnr.isValid(fnr.get())) {
            return ResponseEntity.badRequest().build()
        }

        val innkrevingskrav = innkrevingskravService.hentAlleInnkrevingskrav(fnr)

        return ResponseEntity.ok(innkrevingskrav.map(InnkrevingskravJsonResponse::fromDomain))
    }
}
