(function() {
    /**
     * Oppretter Modig.Modia hvis den ikke finnes
     */
    if (typeof(Modig) === "undefined") {
        window.Modig = {};
    }

    if (typeof(Modig.Modia) === "undefined") {
        window.Modig.Modia = {};
    }

    /**
     * Represents the client side saksoversikt view.
     *
     * Handles shortcut keys and navigation using arrow and number keys.
     *
     * @param selector Selector for the markup element that represents the view
     * @param shortcut Shortcut key that can be used to put focus on the Saksoversikt view.
     * @constructor
     */
    var SaksoversiktView = function SaksoversiktView(selector, shortcut) {
        this.el = $(selector);
        var itemSelector = '> UL > LI > A';

        this.el.addKeyNavigation({
            itemsSelector: itemSelector,
            numberNavigation: true
        });

        Modig.shortcutListener.on({key: shortcut}, $.proxy(this.onShortcut, this));

        window.SaksoversiktViews = window.SaksoversiktViews || [];
        window.SaksoversiktViews[selector] = this;

        var alleTemaLenker =  this.el.find(itemSelector);

        alleTemaLenker.click(function(event, notClearFocus) {
            $("UL > LI.aktiv").removeClass("aktiv");
            $(event.currentTarget).parent("li").addClass("aktiv");
            if(notClearFocus != true) {
                $(".saksoversikt > header.lamellhode a").focus();
            }
        });

        alleTemaLenker.focus(function(event) {
            $(event.currentTarget).trigger("click", true);
        });
    }

    SaksoversiktView.prototype.onShortcut = function onShortcut() {
        this.el.focus();
    };

    window.Modig.Modia.SaksoversiktView = SaksoversiktView;
})();
