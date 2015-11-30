import React from 'react/addons';
import sanitize from 'sanitize-html';

const Utils = {
    Constants: {
        LOCALE_DEFAULT: 'nb_NO'
    },
    getInnhold: (valgtTekst, valgtLocale) => {
        if (!valgtTekst || !valgtTekst.hasOwnProperty('innhold')) {
            return '';
        }
        return valgtTekst.innhold[valgtLocale] ? valgtTekst.innhold[valgtLocale] : valgtTekst.innhold[Utils.Constants.LOCALE_DEFAULT];
    },
    debounce: function debounce(func, wait, immediate) {
        let timeout;
        return function invoker() {
            const context = this;
            const args = arguments;
            const later = () => {
                timeout = null;
                if (!immediate) {
                    func.apply(context, args);
                }
            };
            const callNow = immediate && !timeout;
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
            if (callNow) {
                func.apply(context, args);
            }
        };
    },
    adjustScroll: (parent, element) => {
        if (element === null || element.length === 0) {
            return;
        }
        const $element = $(element);// Har ikke fjernet jQuery her enda.
        const $parent = $(parent);

        const elementTop = $element.position().top;
        const elementBottom = elementTop + $element.outerHeight();
        const scrollContainer = $parent.find('.mCSB_container');
        const scrollPos = parseInt(scrollContainer.css('top'), 10);

        if ((elementTop + scrollPos < 0) || (elementBottom + scrollPos > $parent.outerHeight())) {
            $parent.mCustomScrollbar('scrollTo', $element, {scrollInertia: 0});
        }
    },
    generateId: (prefix) => {
        return prefix + (new Date().getTime()) + '-' + ('' + Math.random()).slice(2);
    },
    sanitize: (tekst) => {
        return sanitize(tekst);
    },
    leggTilLenkerTags: (innhold) => {
        const uriRegex = /(([\w-]+:\/\/?|www(?:-\w+)?\.)[^\s()<>]+\w)/g;
        const httpRegex = /^(https?):\/\/.*$/;

        return innhold.replace(uriRegex, (match) => {
            const processedMatch = match.match(httpRegex) ? match : 'http://' + match;
            return '<a target="_blank" href="' + processedMatch + '">' + processedMatch + '</a>';
        });
    },
    tilParagraf: (avsnitt) => {
        const sanitizedAvsnitt = sanitize(avsnitt, {allowedTags: ['a', 'em']});
        return <p dangerouslySetInnerHTML={{__html: sanitizedAvsnitt}}></p>;
    },
    omit: (obj, filterkeys) => {
        const nObj = Object.create(null);
        const filters = filterkeys.hasOwnProperty('length') ? filterkeys : [filterkeys];

        for (const key in obj) {
            if (obj.hasOwnProperty(key) && filters.indexOf(key) < 0) {
                nObj[key] = obj[key];
            }
        }
        return nObj;
    },
    kvpair: (data) => {
        return Object.keys(data).map((key) => [key, data[key]]);
    },
    autobind: (ctx) => {
        Object.getOwnPropertyNames(ctx.constructor.prototype)
            .filter((prop) => typeof ctx[prop] === 'function')
            .forEach((method) => {
                ctx[method] = ctx[method].bind(ctx);
            });
    }
};

export default Utils;
