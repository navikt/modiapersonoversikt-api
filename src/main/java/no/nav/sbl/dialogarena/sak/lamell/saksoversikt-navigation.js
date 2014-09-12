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

        this.el.addKeyNavigation({
            itemsSelector: '> UL > LI:not(.aktiv) > A',
            numberNavigation: true
        });

        Modig.shortcutListener.on({key: shortcut}, $.proxy(this.onShortcut, this));

        window.SaksoversiktViews = window.SaksoversiktViews || [];
        window.SaksoversiktViews[selector] = this;
    };

    SaksoversiktView.prototype.onShortcut = function onShortcut() {
        this.el.focus();
    };

    window.Modig.Modia.SaksoversiktView = SaksoversiktView;

})();
