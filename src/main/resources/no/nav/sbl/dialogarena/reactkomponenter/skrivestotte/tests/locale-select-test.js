require('./../../test-config.js');
var expect = require('chai').expect;
var React = require('react/addons');
var LocaleSelect = require('../locale-select');

describe('LocaleSelect', function () {
    var TestUtils = React.addons.TestUtils;

    it('rendrer ingenting hvis kun ett locale', function () {
        var props = {tekst: {innhold: [{nb_NO: 'tekst'}]}};

        var element = TestUtils.renderIntoDocument(
            <LocaleSelect {...props} />
        );

        var rendered = TestUtils.scryRenderedDOMComponentsWithTag(element, 'select');

        expect(rendered).to.be.length(0);
    });

    it('rendrer selectboks med locales hvis mer enn ett locale', function () {
        var props = {tekst: {innhold: [{nb_NO: 'tekst'}, {en_US: 'text'}]}};

        var element = TestUtils.renderIntoDocument(
            <LocaleSelect {...props} />
        );

        var rendered = TestUtils.scryRenderedDOMComponentsWithTag(element, 'select');

        expect(rendered).to.be.length(1);
    });
});