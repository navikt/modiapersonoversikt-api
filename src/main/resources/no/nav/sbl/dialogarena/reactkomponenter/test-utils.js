/* eslint-env mocha */
/* eslint no-undef:0 */
import React from 'react';
import Q from 'q';
import TestUtils from 'react-addons-test-utils';

export function createTestPromise() {
    const deferred = Q.defer();

    return {
        promise: deferred.promise,
        reject: deferred.reject,
        resolve: deferred.resolve
    };
}

export function delayed(fn, delay = 100) {
    setTimeout(fn, delay);
}

export function shouldThrowException(fn, done) {
    try {
        fn();
        fail();
    } catch (e) {
        done();
    }
}

export function render(component) {
    const renderedComponent = TestUtils.renderIntoDocument(component);
    return {
        component: renderedComponent,
        dom: React.findDOMNode(renderedComponent)
    };
}

export function strEndsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}
