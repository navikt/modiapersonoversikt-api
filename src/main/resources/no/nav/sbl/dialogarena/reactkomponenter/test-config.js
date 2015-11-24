window.ModiaJS = {};
window.ModiaJS.Components = {};
(function bindPolyfill() {
    const Ap = Array.prototype;
    const slice = Ap.slice;
    const Fp = Function.prototype;

    Fp.bind = function binder(context) {
        const func = this;
        const args = slice.call(arguments, 1);

        function bound() {
            const invokedAsContructor = func.prototype && (this instanceof func);
            return func.apply(
                !invokedAsContructor && context || this, args.concat(slice.call(arguments))
            );
        }

        bound.prototype = func.prototype;

        return bound;
    };
})();

// jQuery
window.$ = require('jquery');

// shim for $(':focusable') since it is part of jQuery UI and we dont need that.
// SONAR:OFF
function visible(element) {
    return $.expr.filters.visible(element) && !$(element).parents().addBack().filter(() => {
        return $.css(this, 'visibility') === 'hidden';
    }).length;
}

function _focusableForNonAreaNode(element, isTabIndexNotNaN, nodeName) {
    if (/input|select|textarea|button|object/.test(nodeName)) {
        return !element.disabled;
    }

    if (nodeName === 'a') {
        return element.href || isTabIndexNotNaN;
    }

    return isTabIndexNotNaN && visible(element);
}

function focusable(element, isTabIndexNotNaN) {
    let map;
    let mapName;
    let img;
    const nodeName = element.nodeName.toLowerCase();

    if (nodeName === 'area') {
        map = element.parentNode;
        mapName = map.name;
        if (!element.href || !mapName || map.nodeName.toLocaleLowerCase() !== 'map') {
            return false;
        }
        img = $('img[usemap=#' + mapName + ']')[0];
        return !!img && visible(img);
    }

    return _focusableForNonAreaNode(element, isTabIndexNotNaN, nodeName);
}

function tabbable(element) {
    let tabIndex = $.attr(element, 'tabindex');
    if (tabIndex === null) {
        tabIndex = undefined;
    }

    const isTabIndexNaN = isNaN(tabIndex);
    return (isTabIndexNaN || tabIndex >= 0) && focusable(element, !isTabIndexNaN);
}

$.extend($.expr[':'], {
    focusable: function focusableExtention(element) {
        return focusable(element, !isNaN($.attr(element, 'tabindex')));
    },
    tabbable: tabbable
});
// SONAR:ON

module.exports = {};
