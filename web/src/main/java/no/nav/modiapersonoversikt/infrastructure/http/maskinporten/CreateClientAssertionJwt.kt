package no.nav.modiapersonoversikt.infrastructure.http.maskinporten

import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.time.Instant
import java.util.*

fun createClientAssertionJwt(
    clientJwk: String,
    clientId: String,
    issuer: String,
    scopes: Set<String>,
): String {
    val rsaKey: RSAKey = RSAKey.parse(clientJwk)
    val signer = RSASSASigner(rsaKey.toPrivateKey())

    val header: JWSHeader =
        JWSHeader.Builder(JWSAlgorithm.RS256)
            .keyID(rsaKey.keyID)
            .type(JOSEObjectType.JWT)
            .build()

    val now: Date = Date.from(Instant.now())
    val expiration: Date = Date.from(Instant.now().plusSeconds(60))

    val claims: JWTClaimsSet =
        JWTClaimsSet.Builder().apply {
            if (scopes.isNotEmpty()) {
                claim("scope", scopes.joinToString(" "))
            }

            issuer(clientId)
            audience(issuer)
            issueTime(now)
            expirationTime(expiration)
            jwtID(UUID.randomUUID().toString())
        }.build()

    return SignedJWT(header, claims)
        .apply { sign(signer) }
        .serialize()
}
