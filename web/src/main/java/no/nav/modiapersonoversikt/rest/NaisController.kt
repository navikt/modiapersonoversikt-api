package no.nav.modiapersonoversikt.rest

import no.nav.common.health.selftest.SelfTestCheck
import no.nav.common.health.selftest.SelfTestUtils
import no.nav.common.health.selftest.SelftestHtmlGenerator
import no.nav.modiapersonoversikt.infrastructure.types.Pingable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal")
class NaisController {
    @Autowired
    lateinit var checks: List<SelfTestCheck>

    @Autowired
    lateinit var pingables: List<Pingable>

    @GetMapping("/isReady")
    fun isReady(): ResponseEntity<Void> = ResponseEntity.status(200).build()

    @GetMapping("/isAlive")
    fun isAlive(): ResponseEntity<Void> = ResponseEntity.status(200).build()

    @GetMapping("/selftest")
    fun selftest(): ResponseEntity<String> {
        val result = SelfTestUtils.checkAll(checks.plus(pingables.map { it.ping() }))
        return ResponseEntity
            .status(SelfTestUtils.findHttpStatusCode(result))
            .contentType(MediaType.TEXT_HTML)
            .body(SelftestHtmlGenerator.generate(result))
    }
}
