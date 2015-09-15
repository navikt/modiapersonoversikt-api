require('./../../test-config.js');
var expect = require('chai').expect;
var sinon = require('sinon');
var React = require('react/addons');
var assign = require('object-assign');
var MeldingerSokStore = require('../meldinger-sok-store');

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
        sinon.spy($, 'ajax');
        var fnr = '12345678910';
        var store = new MeldingerSokStore(assign({}, initialState, {fnr: fnr}));

        store.update();

        expect($.ajax.calledOnce).to.equal(true);
        var args = $.ajax.args[0][0];
        expect(args['async']).to.equal(false);
        expect(args['url']).to.contain(fnr).and.to.contain('indekser');

        $.ajax.restore();
    });

});