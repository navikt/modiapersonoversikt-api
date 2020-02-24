package no.nav.sbl.dialogarena.abac

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

data class Attribute(@JsonProperty("AttributeId")val attributeId: String, @JsonProperty("Value")val value: String)

enum class Category {
    AccessSubject,
    Environment,
    Action,
    Resource
}

data class CategoryAttribute(@JsonProperty("Attribute")val attribute: MutableList<Attribute> = ArrayList()) {
    fun attribute(attributeId: String, value: String) {
        this.attribute.add(Attribute(attributeId, value))
    }
}

class Request {
    internal val requestAttributes = HashMap<Category, CategoryAttribute>()

    fun subject(block: CategoryAttribute.() -> Unit): Request {
        requestAttributes.getOrPut(Category.AccessSubject) { CategoryAttribute().apply(block) }
        return this
    }

    fun environment(block: CategoryAttribute.() -> Unit): Request {
        requestAttributes.getOrPut(Category.Environment) { CategoryAttribute().apply(block) }
        return this
    }

    fun action(block: CategoryAttribute.() -> Unit): Request {
        requestAttributes.getOrPut(Category.Action) { CategoryAttribute().apply(block) }
        return this
    }

    fun resource(block: CategoryAttribute.() -> Unit): Request {
        requestAttributes.getOrPut(Category.Resource) { CategoryAttribute().apply(block) }
        return this
    }
}

typealias AbacRequest = Map<String, Map<Category, CategoryAttribute>>
fun abacRequest(block: Request.() -> Unit): AbacRequest {
    return mapOf("Request" to Request().apply(block).requestAttributes)
}

fun main() {
    val request = abacRequest {
        subject {
            attribute("urn:oasis:names:tc:xacml:1.0:subject:subject-id", "A111111")
            attribute("no.nav.abac.attributter.subject.felles.subjectType", "InternBruker")
        }
        environment {
            attribute("no.nav.abac.attributter.environment.felles.pep_id", "srvEksempelPep")
        }
        action {
            attribute("urn:oasis:names:tc:xacml:1.0:action:action-id", "read")
        }
        resource {
            attribute("no.nav.abac.attributter.resource.felles.domene", "veilarb")
            attribute("no.nav.abac.attributter.resource.felles.resource_type", "no.nav.abac.attributter.subject.felles.har_tilgang_egen_ansatt")
        }
    }
    val mapper = ObjectMapper().registerModule(KotlinModule())
    println(mapper.writeValueAsString(request))
    println(request)
}