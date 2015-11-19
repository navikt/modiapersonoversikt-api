import Q from 'q';

export function createTestPromise() {
    const deferred = Q.defer();

    return {
        promise: deferred.promise,
        reject: deferred.reject,
        resolve: deferred.resolve
    }
}

export function delayed(fn, delay = 10) {
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