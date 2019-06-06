package no.nav.sbl.dialogarena.modiabrukerdialog.web.pages.skrivestotteadmin

import no.nav.modig.frontend.MetaTag
import org.apache.wicket.markup.head.*
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.request.Url
import org.apache.wicket.request.resource.UrlResourceReference

class SkrivestotteAdminPage : WebPage() {
    companion object {
        val skriveStotteDomain = System.getProperty("skrivestotte.domain", "")
        val JS = UrlResourceReference(Url.parse("${skriveStotteDomain}/static/js/main.js"))
        val CSS = UrlResourceReference(Url.parse("${skriveStotteDomain}/static/css/main.css"))
    }

    override fun renderHead(response: IHeaderResponse?) {
        fjernGlobaleRessurser(response as ResourceAggregator)

        response.render(JavaScriptContentHeaderItem.forScript("window.skrivestotteDomain = '$skriveStotteDomain'", null))
        response.render(JavaScriptUrlReferenceHeaderItem.forReference(JS))
        response.render(CssUrlReferenceHeaderItem.forReference(CSS))
    }

    // Stygg hack for å fjerne all felles Css og JS som er konfigurert i WicketApplication siden vi mounter en ekstern app på denne siden
    private fun fjernGlobaleRessurser(resources: ResourceAggregator) {
        val itemsToBeRendered = ResourceAggregator::class.java.getDeclaredField("itemsToBeRendered")
        itemsToBeRendered.isAccessible = true

        val oldResources = itemsToBeRendered.get(resources) as LinkedHashMap<HeaderItem, ResourceAggregator.RecordedHeaderItem>
        val newResources = LinkedHashMap<HeaderItem, ResourceAggregator.RecordedHeaderItem>()

        oldResources
                .entries
                .filter { it.key is MetaTag || it.key is PageHeaderItem }
                .forEach {
                    newResources[it.key] = it.value
                }

        itemsToBeRendered.set(resources, newResources)
    }
}