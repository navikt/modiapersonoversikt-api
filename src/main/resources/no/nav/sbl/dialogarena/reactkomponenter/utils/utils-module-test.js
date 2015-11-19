var expect = require('chai').expect;
var assert = require('chai').assert;
import { delayed } from './../test-utils';
import { debounce, generateId, leggTilLenkerTags, omit, kvpair } from './utils-module';

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

    it("Genererer OK id", function(){
        const suffix = "minid";

        var exp = new RegExp("^" + suffix + "\\d{13}-\\d{1,17}$");
        var result = exp.test(generateId(suffix));

        assert.isTrue(result);
    });

    it("Finner lenker i fritekst", function(){
        const fritekst = `
            Her er vg.no (denne funker ikke) du kan også bruke www.vg.no eller om du virkelig vil https://www.vg.no
        `;
        const result = leggTilLenkerTags(fritekst);

        assert.isTrue(result.indexOf('<a target="_blank" href="http://www.vg.no">') >= 0);
        assert.isTrue(result.indexOf('<a target="_blank" href="https://www.vg.no">') >= 0);
    });

    it("omit kopierer object med unntatt av verdier", function(){
        const myObj = {
            a: 1,
            b: 2,
            c: 3
        };

        const filterVerdi = omit(myObj, 'a');
        const filterListe = omit(myObj, ['a', 'b']);

        assert.isTrue(Object.keys(filterVerdi).length === 2);
        assert.isTrue(Object.keys(filterListe).length === 1);
    });

    it("kvpair mapper object til double-array", function(){
        const myObj = {
            a: 1,
            b: 2,
            c: 3
        };

        const kv = kvpair(myObj);

        expect(kv).to.have.length(3);
        expect(kv[0]).to.eql(['a', 1]);
        expect(kv[1]).to.eql(['b', 2]);
        expect(kv[2]).to.eql(['c', 3]);
    });
});