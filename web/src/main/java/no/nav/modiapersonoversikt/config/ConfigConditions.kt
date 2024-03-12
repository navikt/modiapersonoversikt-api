package no.nav.modiapersonoversikt.config

import no.nav.common.utils.EnvironmentUtils.*
import org.springframework.context.annotation.Condition
import org.springframework.context.annotation.ConditionContext
import org.springframework.core.type.AnnotatedTypeMetadata

class InProdCondition : Condition {
    override fun matches(
        context: ConditionContext,
        metadata: AnnotatedTypeMetadata,
    ): Boolean {
        return PROD_CLUSTERS.contains(requireClusterName())
    }
}

class InDevCondition : Condition {
    override fun matches(
        context: ConditionContext,
        metadata: AnnotatedTypeMetadata,
    ): Boolean {
        return DEV_CLUSTERS.contains(requireClusterName())
    }
}
