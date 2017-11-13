/* eslint-env mocha */
/* eslint no-undef:0 */
import Q from 'q';
import TestUtils from 'react-addons-test-utils';
import ReactDOM from 'react-dom';

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
        dom: ReactDOM.findDOMNode(renderedComponent) // eslint-disable-line react/no-deprecated
    };
}

export function strEndsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}
