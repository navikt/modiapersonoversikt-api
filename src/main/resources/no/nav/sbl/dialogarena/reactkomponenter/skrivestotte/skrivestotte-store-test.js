require('./../test-config');
var expect = require('chai').expect;
var React = require('react/addons');
var assign = require('object-assign');
var SkrivestotteStore = require('./skrivestotte-store');

describe('SkrivestotteStore', function () {

    var initialState = {
        fritekst: "",
        tekster: [],
        valgtTekst: {}
    };

    it('setter valgt tekst hvis tekster finnes', function () {
        var tekster = [{id: 1}];
        var store = new SkrivestotteStore(assign({}, initialState, {tekster: tekster}));

        expect(store.state.valgtTekst).to.equal(tekster[0]);
    });

});