/* eslint-env mocha */
import { expect, assert } from 'chai';
import { delayed } from './../test-utils';
import { debounce, generateId, leggTilLenkerTags, omit, kvpair } from './utils-module';

describe('utils test', () => {
    it('debounce test immediate', (done) => {
        let c = 0;
        const fn = debounce(() => {
            c = c + 1;
        }, 100, true);

        fn();
        fn();
        fn();

        expect(c).to.equal(1);
        done();
    });

    it('debounce test', (done) => {
        let c = 0;
        const fn = debounce(() => {
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

    it('Genererer OK id', () => {
        const suffix = 'minid';

        const exp = new RegExp('^' + suffix + '\\d{1,13}-\\d+$');
        const result = exp.test(generateId(suffix));

        assert.isTrue(result);
    });

    it('Finner lenker i fritekst', () => {
        const fritekst = `
            Her er vg.no (denne funker ikke) du kan også bruke www.vg.no eller om du virkelig vil https://www.vg.no
        `;
        const result = leggTilLenkerTags(fritekst);

        assert.isTrue(result.indexOf('<a target="_blank" href="http://www.vg.no">') >= 0);
        assert.isTrue(result.indexOf('<a target="_blank" href="https://www.vg.no">') >= 0);
    });

    it('omit kopierer object med unntatt av verdier', () => {
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

    it('kvpair mapper object til double-array', () => {
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
