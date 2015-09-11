require('./../test-config');
var expect = require('chai').expect;
var sinon = require('sinon');
var React = require('react/addons');
var Utils = require('./../utils/utils-module');
var TekstForhandsvisning = require('./tekst-forhandsvisning');

describe('TekstForhandsvisning', function () {
    var TestUtils = React.addons.TestUtils;

    var tekst = {innhold: {'nb_NO': 'tekst'}, tags: []};

    it('splitter tekst i avsnitt', function () {
        sinon.spy(Utils, 'tilParagraf');

        var shallowRenderer = TestUtils.createRenderer();
        shallowRenderer.render(
            <TekstForhandsvisning tekst={tekst} />
        );

        expect(Utils.tilParagraf.called).to.equal(true);

        Utils.tilParagraf.restore();
    });


});