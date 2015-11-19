import { expect } from 'chai';
import { debounce } from './utils-module';
import { delayed } from './../test-utils';

describe("utils test", function () {
    it("debounce test", function () {
        let c = 0;
        const fn = debounce(function () {
            c = c + 1;
        }, 100, true);

        fn();
        fn();
        fn();

        expect(c).to.equal(1);

    });
});


