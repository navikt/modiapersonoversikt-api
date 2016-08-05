/* eslint-env mocha */
/* eslint no-unused-expressions:0 new-cap:0 */
import './../test-config';
import { expect } from 'chai';
import assign from 'object-assign';
import sinon from 'sinon';
import chai from 'chai';
import sinonChai from 'sinon-chai';
import SkrivestotteStore from './skrivestotte-store';
import Ajax from '../utils/ajax';

chai.use(sinonChai);

describe('SkrivestotteStore', () => {
    before(() => {
        sinon.stub(SkrivestotteStore, '_updateScroll');
    });

    after(() => {
        SkrivestotteStore._updateScroll.restore();
    });

    const initialState = {
        fritekst: '',
        tekster: [],
        valgtTekst: {}
    };

    it('setter valgt tekst hvis tekster finnes', () => {
        const tekster = [{ id: 1 }];
        const store = new SkrivestotteStore(assign({}, initialState, { tekster }));

        expect(store.state.valgtTekst).to.equal(tekster[0]);
    });

    it('knagger kan legges til og fjernes', () => {
        const store = new SkrivestotteStore(assign({}, initialState, { knagger: [] }));
        store.leggTilKnagg('knagg1');
        store.leggTilKnagg('knagg2');
        store.slettKnagg('knagg1');
        store.leggTilKnagg('knagg3');
        expect(store.state.knagger).to.eql(['knagg2', 'knagg3']);
    });

    const t1 = { key: 'key1' };
    const t2 = { key: 'key2' };
    const t3 = { key: 'key3' };
    const tekster = [t1, t2, t3];
    const event = $.Event('keypress');

    it('pil opp ger førre tekst men er ikke cyklisk', () => {
        event.which = 38;
        event.keyCode = 38;
        const store = new SkrivestotteStore(assign({}, initialState, { tekster }));

        store.onKeyDown([], event);

        expect(store.state.valgtTekst).to.equal(t1);

        store.state.valgtTekst = t3;
        store.onKeyDown([], event);
        expect(store.state.valgtTekst).to.equal(t2);
    });

    it('pil ned ger neste tekst men er ikke cyklisk', () => {
        event.which = 40;
        event.keyCode = 40;
        const store = new SkrivestotteStore(assign({}, initialState, { tekster }));
        store.state.valgtTekst = t3;

        store.onKeyDown([], event);

        expect(store.state.valgtTekst).to.equal(t3);

        store.state.valgtTekst = t2;
        store.onKeyDown([], event);
        expect(store.state.valgtTekst).to.equal(t3);
    });

    it('hent søk resultater gør et søk', () => {
        sinon.spy(Ajax, 'get');
        const fritekst = 'helloWorld';
        const knagger = 'earth';
        SkrivestotteStore._sok(fritekst, knagger);

        expect(Ajax.get).to.have.been.calledOnce;

        const args = Ajax.get.args[0];
        const url = args[0];
        expect(url).to.contain('sok').and.to.contain(fritekst).and.to.contain('tags').and.to.contain(knagger);

        Ajax.get.restore();
    });
});
