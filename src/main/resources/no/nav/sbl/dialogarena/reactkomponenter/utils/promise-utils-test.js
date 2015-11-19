var expect = require('chai').expect;
var assert = require('chai').assert;
import Q from 'q';
import PromiseUtils from './promise-utils';

describe("PromiseUtils", function () {
    it("Should throw exception if N is negative", function (done) {
        shouldThrowException(() => PromiseUtils.atLeastN(-1, []), done);
    });

    it("Should throw exception if N is greater then number of promises", function (done) {
        shouldThrowException(() => PromiseUtils.atLeastN(1, []), done);
    });

    it("Should throw exception if N is not a number", function (done) {
        shouldThrowException(() => PromiseUtils.atLeastN("", []), done);
    });

    it("Resolved if N = 1 promises are OK", function(done){
        var data = "Data";
        var p1 = createPromise();
        var p2 = createPromise();

        PromiseUtils.atLeastN(1, {p1: p1.promise, p2: p2.promise})
            .then((results) => {
                expect(results.p1.value).to.equal(data);
                expect(results.p2.value).to.be.an('undefined');
                done();
            });

        p1.resolve(data);
        p2.reject();

        delayed(() => assert.fail());//Delayed slik at promise fungerer
    });

    it("Resolved if N = 2 promises are OK", function(done){
        var data = "Data";
        var data2 = "Data2";
        var p1 = createPromise();
        var p2 = createPromise();
        var p3 = createPromise();

        PromiseUtils.atLeastN(2, {p1: p1.promise, p2: p2.promise, p3: p3.promise})
            .then((results) => {
                expect(results.p1.value).to.equal(data);
                expect(results.p2.value).to.equal(data2);
                done();
            });

        p1.resolve(data);
        p2.resolve(data2);
        p3.reject();

        delayed(() => assert.fail());//Delayed slik at promise fungerer
    });

    it("Reject if N promises are not satisfied", function(done){
        var data = "Data";
        var data2 = "Data2";
        var p1 = createPromise();
        var p2 = createPromise();
        var p3 = createPromise();

        PromiseUtils.atLeastN(3, {p1: p1.promise, p2: p2.promise, p3: p3.promise})
            .catch(() => {
                done();
            });

        p1.resolve(data);
        p2.resolve(data2);
        p3.reject();

        delayed(() => assert.fail());//Delayed slik at promise fungerer
    });
});

function createPromise() {
    const deferred = Q.defer();

    return {
        promise: deferred.promise,
        reject: deferred.reject,
        resolve: deferred.resolve
    }
}

function delayed(fn, delay = 0) {
    setTimeout(fn, delay);
}

function shouldThrowException(fn, done) {
    try {
        fn();
        fail();
    } catch (e) {
        done();
    }
}