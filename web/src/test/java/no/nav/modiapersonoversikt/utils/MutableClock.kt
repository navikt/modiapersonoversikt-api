package no.nav.modiapersonoversikt.utils

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAmount

class MutableClock(
    private var instant: Instant = Instant.now(),
    private var zone: ZoneId = ZoneId.systemDefault()
) : Clock() {
    override fun getZone(): ZoneId = zone

    override fun withZone(zone: ZoneId?): Clock {
        return if (zone == this.zone) this else {
            this.zone = requireNotNull(zone) {
                "Cannot set zone to null"
            }
            this
        }
    }

    override fun instant(): Instant = instant

    fun plusDays(amount: Long): MutableClock = plus(Duration.ofDays(amount))
    fun plusHours(amount: Long): MutableClock = plus(Duration.ofHours(amount))
    fun plusMinutes(amount: Long): MutableClock = plus(Duration.ofMinutes(amount))

    fun plus(amount: TemporalAmount): MutableClock {
        instant = instant.plus(amount)
        return this
    }
}
