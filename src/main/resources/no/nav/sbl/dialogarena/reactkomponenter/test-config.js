import Enzyme from 'enzyme';
import Adapter from 'enzyme-adapter-react-16';

Enzyme.configure({ adapter: new Adapter() });

const { JSDOM } = require('jsdom');

const jsdom = new JSDOM('<!doctype html><html><body></body></html>');
const { window } = jsdom;
require('jquery')(window);

window.ModiaJS = {};
window.ModiaJS.Components = {};
global.$ = window.$;
global.Intl = require('intl');

function copyProps(src, target) {
    const props = Object.getOwnPropertyNames(src)
        .filter(prop => typeof target[prop] === undefined)
        .map(prop => Object.getOwnPropertyDescriptor(src, prop));

    Object.defineProperties(target, props);
}

global.window = window;
global.document = window.document;
global.navigator = {
    userAgent: 'node.js'
};

copyProps(window, global);


// shim for $(':focusable') since it is part of jQuery UI and we dont need that.
// SONAR:OFF
function visible(element) {
    return $.expr.filters.visible(element) && !$(element).parents().addBack().filter(() =>
        $.css(this, 'visibility') === 'hidden').length;
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
        img = $(`img[usemap=#${mapName}]`)[0];
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
    tabbable
});
// SONAR:ON

module.exports = {};
