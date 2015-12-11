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
            return;
        }

        var elementTop = $element.position().top;
        var elementBottom = elementTop + $element.outerHeight();

        if (elementTop < 0) {
            $parent.scrollTop($parent.scrollTop() + elementTop);
        } else if (elementBottom > $parent.outerHeight()) {
            $parent.scrollTop($parent.scrollTop() + (elementBottom - $parent.outerHeight()));
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
    },
    omit: function (obj, filterkeys) {
        var nObj = Object.create(null);
        var filters = filterkeys.hasOwnProperty('length') ? filterkeys : [filterkeys];

        for (var key in obj) {
            if (obj.hasOwnProperty(key) && filters.indexOf(key) < 0) {
                nObj[key] = obj[key];
            }
        }
        return nObj;
    },
    kvpair: function (data) {
        return Object.keys(data).map((key) => [key, data[key]]);
    }

};
module.exports = Utils;