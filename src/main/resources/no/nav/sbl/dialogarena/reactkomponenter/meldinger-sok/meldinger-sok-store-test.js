require('./../test-config');
var expect = require('chai').expect;
var sinon = require('sinon');
var React = require('react/addons');
var assign = require('object-assign');
var MeldingerSokStore = require('./meldinger-sok-store');
var Ajax = require('./../utils/ajax');

describe('MeldingerSokStore', function () {

    var initialState = {
        fritekst: "",
        traader: [],
        valgtTraad: {}
    };

    it('setter valgt tråd hvis tråder finnes', function () {
        var traader = [{id: 1}, {id: 2}];
        var store = new MeldingerSokStore(assign({}, initialState, {traader: traader}));

        expect(store.state.valgtTraad).to.equal(traader[0]);
    });

    it('indekserer ved initializeVisning', function () {
        sinon.spy(Ajax, 'get');

        var fnr = '12345678910';
        var store = new MeldingerSokStore(assign({}, initialState, {fnr: fnr}));

        store.initializeVisning();

        String.prototype.contains = function (it) {
            return this.indexOf(it) != -1;
        };
        var args = Ajax.get.args[0];
        var url = args[0];

        expect(Ajax.get.calledOnce).to.equal(true);
        expect(url).to.contains(fnr).and.to.contain('indekser');

        Ajax.get.restore();
    });

});