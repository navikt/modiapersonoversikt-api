require('./../../test-config');
var expect = require('chai').expect;
var sinon = require('sinon');
var React = require('react/addons');
var Utils = require('./../../utils/utils-module');
var TekstForhandsvisning = require('../tekst-forhandsvisning');

describe.skip('TekstForhandsvisning', function () {
    var TestUtils = React.addons.TestUtils;

    var tekst = {innhold: {'nb_NO': 'tekst'}, tags: []};

    it('splitter tekst i avsnitt', function () {
        window.Modig = {};
        sinon.spy(Utils, 'tilParagraf');

        var element = TestUtils.renderIntoDocument(
            <TekstForhandsvisning tekst={tekst} />
        );

        expect(Utils.tilParagraf.called).to.equal(true);

        Utils.tilParagraf.restore();
        delete window.Modig;
    });


});