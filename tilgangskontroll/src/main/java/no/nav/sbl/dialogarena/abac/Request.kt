package no.nav.sbl.dialogarena.abac

data class Attribute(val attributeId: String, val value: String)

enum class Category {
    AccessSubject,
    Environment,
    Action,
    Resource
}

data class CategoryAttribute(private val attribute: MutableList<Attribute> = ArrayList()) {
    fun attribute(attributeId: AbacAttributeId, value: String) {
        this.attribute.add(Attribute(attributeId.getId(), value))
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

data class AbacRequest(
        val request: Map<Category, CategoryAttribute>
)

fun abacRequest(block: Request.() -> Unit): AbacRequest {
    return AbacRequest(Request().apply(block).requestAttributes)
}
