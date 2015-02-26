var Utils = {
    Constants: {
        LOCALE_DEFAULT: 'nb_NO'
    },
    debounce: function (func, wait, immediate) {
        var timeout;
        return function () {
            var context = this, args = arguments;
            var later = function () {
                timeout = null;
                if (!immediate) {
                    func.apply(context, args);
                }
            };
            var callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) {
                func.apply(context, args);
            }
        };
    },
    adjustScroll: function ($parent, $element) {
        if ($element.length === 0) {
            return;
        }

        var elementTop = $element.position().top;
        var elementBottom = elementTop + $element.outerHeight();

        if (elementTop < 0) {
            $parent.scrollTop($parent.scrollTop() + elementTop);
        } else if (elementBottom > $parent.outerHeight()) {
            $parent.scrollTop($parent.scrollTop() + (elementBottom - $parent.outerHeight()));
        }
    }
};


module.exports = Utils;
