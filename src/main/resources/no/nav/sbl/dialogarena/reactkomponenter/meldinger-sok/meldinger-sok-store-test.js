import './../test-config';
import { expect } from 'chai';
import sinon from 'sinon';
import assign from 'object-assign';
import MeldingerSokStore from './meldinger-sok-store';

describe('MeldingerSokStore', function () {

    const initialState = {
        fritekst: "",
        traader: [],
        valgtTraad: {}
    };

    it('setter valgt tråd hvis tråder finnes', function () {
        const traader = [{id: 1}, {id: 2}];
        const store = new MeldingerSokStore(assign({}, initialState, {traader: traader}));

        expect(store.state.valgtTraad).to.equal(traader[0]);
    });

    it('indekserer ved update', function () {
        sinon.spy($, 'ajax');
        var fnr = '12345678910';
        var store = new MeldingerSokStore(assign({}, initialState, {fnr: fnr}));

        store.initializeVisning();

        expect($.ajax.calledOnce).to.equal(true);
        var args = $.ajax.args[0][0];
        expect(args['async']).to.equal(false);
        expect(args['url']).to.contain(fnr).and.to.contain('indekser');

        expect(Ajax.get.calledOnce).to.equal(true);
        expect(url).to.contains(fnr).and.to.contain('indekser');

        Ajax.get.restore();
    });

    it('pil opp ger førre melding men er ikke cyklisk', function () {
        const e1 = {key: "key1"};
        const e2 = {key: "key2"};
        const e3 = {key: "key3"};
        const elementer = [e1, e2, e3];
        const event = $.Event("keypress");
        event.which = 38;
        event.keyCode = 38;
        const store = new MeldingerSokStore(assign({}, initialState, {traader: elementer}));

        store.onKeyDown([], event);

        expect(store.state.valgtTraad).to.equal(e1);

        store.state.valgtTraad = e3;
        store.onKeyDown([], event);
        expect(store.state.valgtTraad).to.equal(e2);

    });

    it('pil ned ger neste melding men er ikke cyklisk', function () {
        const e1 = {key: "key1"};
        const e2 = {key: "key2"};
        const e3 = {key: "key3"};
        const elementer = [e1, e2, e3];
        const event = $.Event("keypress");
        event.which = 40;
        event.keyCode = 40;
        const store = new MeldingerSokStore(assign({}, initialState, {traader: elementer}));
        store.state.valgtTraad = e3;

        store.onKeyDown([], event);

        expect(store.state.valgtTraad).to.equal(e3);

        store.state.valgtTraad = e2;
        store.onKeyDown([], event);
        expect(store.state.valgtTraad).to.equal(e3);

    });
});