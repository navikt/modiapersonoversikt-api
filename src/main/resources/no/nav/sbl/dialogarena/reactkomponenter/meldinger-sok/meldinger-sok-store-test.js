require('./../test-config');
var expect = require('chai').expect;
var sinon = require('sinon');
var React = require('react/addons');
var assign = require('object-assign');
var MeldingerSokStore = require('./meldinger-sok-store');
var Ajax = require('./../utils/ajax');

describe('SkrivestotteStore', function () {

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

    it('indekserer ved update', function () {
        sinon.spy(Ajax, 'get');
        var fnr = '12345678910';
        var store = new MeldingerSokStore(assign({}, initialState, {fnr: fnr}));

        store.initializeVisning();

        const compareURL = sinon.match(function (url) {
            return url.contains(fnr) && url.contains('indekser');
        });

        String.prototype.contains = function (it) {
            return this.indexOf(it) != -1;
        };

        expect(Ajax.get.calledOnce).to.equal(true);
        expect(Ajax.get.calledWith(compareURL)).to.equal(true);

        Ajax.get.restore();
    });

});