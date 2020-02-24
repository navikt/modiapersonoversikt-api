package no.nav.sbl.dialogarena.abac

import com.fasterxml.jackson.annotation.JsonProperty

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
