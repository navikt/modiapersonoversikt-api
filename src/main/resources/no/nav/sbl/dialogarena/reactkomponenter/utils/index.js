var React = require('react/addons');
var sanitize = require('sanitize-html');

var Utils = {
    Constants: {
        LOCALE_DEFAULT: 'nb_NO'
    },
    getInnhold: function (valgtTekst, valgtLocale) {
        if (!valgtTekst || !valgtTekst.hasOwnProperty('innhold')) {
            return '';
        }
        return valgtTekst.innhold[valgtLocale] ? valgtTekst.innhold[valgtLocale] : valgtTekst.innhold[Utils.Constants.LOCALE_DEFAULT];
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
            debugger;
            return;
        }

        var elementTop = $element.position().top;
        var elementBottom = elementTop + $element.outerHeight();
        var scrollContainer = $parent.find('.mCSB_container');
        var scrollPos = parseInt(scrollContainer.css('top'));

        if (elementTop + scrollPos < 0) {
            $parent.mCustomScrollbar('scrollTo', $element);
        } else if (elementBottom + scrollPos > $parent.outerHeight()) {
            $parent.mCustomScrollbar('scrollTo', $element);
        }
    },
    generateId: function (prefix) {
        return prefix + (new Date().getTime()) + '-' + ('' + Math.random()).slice(2);
    },
    sanitize: function (tekst) {
        return sanitize(tekst);
    },
    leggTilLenkerTags: function (innhold) {
        var uriRegex = /(([\w-]+:\/\/?|www(?:-\w+)?\.)[^\s()<>]+\w)/g;
        var httpRegex = /^(https?):\/\/.*$/;

        return innhold.replace(uriRegex, function (match) {
            match = match.match(httpRegex) ? match : 'http://' + match;
            return '<a target="_blank" href="' + match + '">' + match + '</a>'
        });
    },
    tilParagraf: function (avsnitt) {
        avsnitt = sanitize(avsnitt, {allowedTags: ['a', 'em']});
        return <p dangerouslySetInnerHTML={{__html: avsnitt}}></p>;
    }
};
module.exports = Utils;