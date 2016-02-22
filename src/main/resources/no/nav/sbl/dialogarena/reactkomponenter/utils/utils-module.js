import React from 'react';
import sanitizer from 'sanitize-html';

export const Constants = {
    LOCALE_DEFAULT: 'nb_NO'
};

export function getInnhold(valgtTekst, valgtLocale) {
    if (!valgtTekst || !valgtTekst.hasOwnProperty('innhold')) {
        return '';
    }
    return valgtTekst.innhold[valgtLocale] ? valgtTekst.innhold[valgtLocale] : valgtTekst.innhold[Constants.LOCALE_DEFAULT];
}

export function debounce(func, wait, immediate) {
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
}

export function adjustScroll(parent, element) {
    if (element === null || element.length === 0) {
        return;
    }
    const $element = $(element);// Har ikke fjernet jQuery her enda.
    const $parent = $(parent);

    const elementTop = $element.position().top;
    const elementBottom = elementTop + $element.outerHeight();

    if (elementTop < 0) {
        $parent.scrollTop($parent.scrollTop() + elementTop);
    } else if (elementBottom > $parent.outerHeight()) {
        $parent.scrollTop($parent.scrollTop() + (elementBottom - $parent.outerHeight()));
    }
}

export function generateId(prefix) {
    return prefix + (new Date().getTime()) + '-' + ('' + Math.random()).slice(2);
}

export function sanitize(tekst) {
    return sanitizer(tekst);
}

export function leggTilLenkerTags(innhold) {
    const uriRegex = /(([\w-]+:\/\/?|www(?:-\w+)?\.)[^\s()<>]+\w)/g;
    const httpRegex = /^(https?):\/\/.*$/;

    return innhold.replace(uriRegex, (match) => {
        const processedMatch = match.match(httpRegex) ? match : 'http://' + match;
        return '<a target="_blank" href="' + processedMatch + '">' + processedMatch + '</a>';
    });
}

export function tilParagraf(avsnitt) {
    const sanitizedAvsnitt = sanitize(avsnitt, { allowedTags: ['a', 'em'] });
    return <p dangerouslySetInnerHTML={{__html: sanitizedAvsnitt}}></p>;
}

export function omit(obj, filterkeys) {
    const nObj = Object.create(null);
    const filters = filterkeys.hasOwnProperty('length') ? filterkeys : [filterkeys];

    for (const key in obj) {
        if (obj.hasOwnProperty(key) && filters.indexOf(key) < 0) {
            nObj[key] = obj[key];
        }
    }
    return nObj;
}

export function kvpair(data) {
    return Object.keys(data).map((key) => [key, data[key]]);
}

export function autobind(ctx) {
    Object.getOwnPropertyNames(ctx.constructor.prototype)
        .filter((prop) => typeof ctx[prop] === 'function')
        .forEach((method) => {
            ctx[method] = ctx[method].bind(ctx);
        });
}

export default {
    Constants,
    getInnhold,
    debounce,
    adjustScroll,
    generateId,
    sanitize,
    leggTilLenkerTags,
    tilParagraf,
    omit,
    kvpair,
    autobind
};
