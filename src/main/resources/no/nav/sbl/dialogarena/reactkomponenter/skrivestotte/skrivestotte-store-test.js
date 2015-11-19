require('./../test-config');
var expect = require('chai').expect;
var React = require('react/addons');
var assign = require('object-assign');
var SkrivestotteStore = require('./skrivestotte-store');
import sinon from 'sinon';
import chai from 'chai';
import sinonChai from 'sinon-chai';
chai.use(sinonChai);

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

    it('knagger kan legges til og fjernes', function () {
        var store = new SkrivestotteStore(assign({}, initialState, {knagger: []}));
        store.leggTilKnagg("knagg1");
        store.leggTilKnagg("knagg2");
        store.slettKnagg("knagg1");
        store.leggTilKnagg("knagg3");
        expect(store.state.knagger).to.eql(["knagg2", "knagg3"]);

    });

    const t1 = {key: "key1"};
    const t2 = {key: "key2"};
    const t3 = {key: "key3"};
    const tekster = [t1, t2, t3];
    var event = $.Event("keypress");

    it('pil opp ger førre tekst men er ikke cyklisk', function () {

        event.which = 38;
        event.keyCode = 38;
        var store = new SkrivestotteStore(assign({}, initialState, {tekster: tekster}));

        store.onKeyDown([], event);

        expect(store.state.valgtTekst).to.equal(t1);

        store.state.valgtTekst = t3;
        store.onKeyDown([], event);
        expect(store.state.valgtTekst).to.equal(t2);

    });

    it('pil ned ger neste tekst men er ikke cyklisk', function () {

        event.which = 40;
        event.keyCode = 40;
        var store = new SkrivestotteStore(assign({}, initialState, {tekster: tekster}));
        store.state.valgtTekst = t3;

        store.onKeyDown([], event);

        expect(store.state.valgtTekst).to.equal(t3);

        store.state.valgtTekst = t2;
        store.onKeyDown([], event);
        expect(store.state.valgtTekst).to.equal(t3);

    });

    it('hent søk resultater gør et søk', function () {
        sinon.spy($, 'get');
        var store = new SkrivestotteStore(assign({}, initialState, {tekster: tekster}));
        const fritekst = "helloWorld";
        const knagger = "earth";
        SkrivestotteStore._sok(fritekst, knagger);
        expect($.get).to.have.been.calledOnce;

        var args = $.get.args[0][0];
        expect(args).to.contain('sok').and.to.contain(fritekst).and.to.contain('tags').and.to.contain(knagger);

    });
});