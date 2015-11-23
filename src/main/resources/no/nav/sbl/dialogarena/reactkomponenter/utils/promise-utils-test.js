/* eslint-env mocha */
import { expect, assert } from 'chai';
import PromiseUtils from './promise-utils';
import {createTestPromise, delayed, shouldThrowException} from './../test-utils';

describe('PromiseUtils', () => {
    it('Should throw exception if N is negative', (done) => {
        shouldThrowException(() => PromiseUtils.atLeastN(-1, []), done);
    });

    it('Should throw exception if N is greater then number of promises', (done) => {
        shouldThrowException(() => PromiseUtils.atLeastN(1, []), done);
    });

    it('Should throw exception if N is not a number', (done) => {
        shouldThrowException(() => PromiseUtils.atLeastN('', []), done);
    });

    it('Resolved if N = 1 promises are OK', (done) => {
        const data = 'Data';
        const p1 = createTestPromise();
        const p2 = createTestPromise();

        PromiseUtils.atLeastN(1, {p1: p1.promise, p2: p2.promise})
            .then((results) => {
                expect(results.p1.value).to.equal(data);
                expect(results.p2.value).to.be.an('undefined');
                done();
            });

        p1.resolve(data);
        p2.reject();

        delayed(() => assert.fail());// Delayed slik at promise fungerer
    });

    it('Resolved if N = 2 promises are OK', (done) => {
        const data = 'Data';
        const data2 = 'Data2';
        const p1 = createTestPromise();
        const p2 = createTestPromise();
        const p3 = createTestPromise();

        PromiseUtils.atLeastN(2, {p1: p1.promise, p2: p2.promise, p3: p3.promise})
            .then((results) => {
                expect(results.p1.value).to.equal(data);
                expect(results.p2.value).to.equal(data2);
                done();
            });

        p1.resolve(data);
        p2.resolve(data2);
        p3.reject();

        delayed(() => assert.fail());// Delayed slik at promise fungerer
    });

    it('Reject if N promises are not satisfied', (done) => {
        const data = 'Data';
        const data2 = 'Data2';
        const p1 = createTestPromise();
        const p2 = createTestPromise();
        const p3 = createTestPromise();

        PromiseUtils.atLeastN(3, {p1: p1.promise, p2: p2.promise, p3: p3.promise})
            .catch(() => {
                done();
            });

        p1.resolve(data);
        p2.resolve(data2);
        p3.reject();

        delayed(() => assert.fail());// Delayed slik at promise fungerer
    });
});
