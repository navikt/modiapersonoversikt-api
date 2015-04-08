window.ModiaJS = {};
window.ModiaJS.Components = {};
(function () {
    var Ap = Array.prototype;
    var slice = Ap.slice;
    var Fp = Function.prototype;

    Fp.bind = function (context) {
        var func = this;
        var args = slice.call(arguments, 1);

        function bound() {
            var invokedAsContructor = func.prototype && (this instanceof func);
            return func.apply(
                !invokedAsContructor && context || this, args.concat(slice.call(arguments))
            );
        }

        bound.prototype = func.prototype;

        return bound;
    };
})();

//jQuery
window.$ = require('jquery');

//shim for $(':focusable') since it is part of jQuery UI and we dont need that.
function visible(element) {
    return $.expr.filters.visible(element) && !$(element).parents().addBack().filter(function () {
            return $.css(this, 'visibility') === 'hidden';
        }).length;
}
function focusable(element, isTabIndexNotNaN) {
    var map, mapName, img, nodeName = element.nodeName.toLowerCase();

    if ('area' === nodeName) {
        map = element.parentNode;
        mapName = map.name;
        if (!element.href || !mapName || map.nodeName.toLocaleLowerCase() !== 'map') {
            return false;
        }
        img = $('img[usemap=#' + mapName + ']')[0];
        return !!img && visible(img);
    }


    return (/input|select|textarea|button|object/.test(nodeName) ?
            !element.disabled :
            'a' === nodeName ?
            element.href || isTabIndexNotNaN :
                isTabIndexNotNaN) &&
        visible(element);
}

function tabbable(element) {
    var tabIndex = $.attr(element, 'tabindex');
    if (tabIndex === null) {
        tabIndex = undefined;
    }

    var isTabIndexNaN = isNaN(tabIndex);
    return (isTabIndexNaN || tabIndex >= 0) && focusable(element, !isTabIndexNaN);
}

$.extend($.expr[':'], {
    focusable: function (element) {
        return focusable(element, !isNaN($.attr(element, 'tabindex')));
    },
    tabbable: tabbable
});


module.exports = {};
