var expect = require('chai').expect;
var assert = require('chai').assert;
import { debounce } from './utils-module';
import { delayed } from './../test-utils';

describe("utils test", function () {
    it("debounce test immediate", function (done) {
        let c = 0;
        const fn = debounce(function () {
            c = c + 1;
        }, 100, true);

        fn();
        fn();
        fn();

        expect(c).to.equal(1);
        done();
    });

    it("debounce test", function (done) {
        let c = 0;
        const fn = debounce(function () {
            c = c + 1;
        }, 0);

        fn();
        fn();
        fn();
        expect(c).to.equal(0);
        delayed(()=>{
            expect(c).to.equal(1);
            done();
        }, 0);
    });
});