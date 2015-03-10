require('./../../testConfig.js');
var expect = require('chai').expect;
var React = require('react/addons');
var assign = require('object-assign');
var Modal = require('../index.js');
var TestUtils = React.addons.TestUtils;

describe('Modal', function () {

    it('Should create portal as a direct child of body', function () {
        var modal = TestUtils.renderIntoDocument(
            <Modal>
                <span></span>
            </Modal>
        );

        var modalContainer = document.getElementsByClassName('react-modal-container');
        expect(modalContainer).not.to.be.null;
    });

    it('Should be default closed and have the correct attributes', function () {
        var modal = TestUtils.renderIntoDocument(
            <Modal>
                <span></span>
            </Modal>
        );
        var portal = modal.modal.getDOMNode();

        expect(portal.hasAttribute('tabindex')).to.be.true;
        expect(portal.hasAttribute('class')).to.be.true;
        expect(portal.hasAttribute('aria-hidden')).to.be.true;
        expect(portal.hasAttribute('role')).to.be.true;
        expect(portal.hasAttribute('aria-labelledby')).to.be.true;
        expect(portal.hasAttribute('aria-describedby')).to.be.true;

        expect(portal.getAttribute('tabindex')).to.be.eql('-1');
        expect(portal.getAttribute('class')).to.be.eql('hidden');
        expect(portal.getAttribute('aria-hidden')).to.be.eql('true');
        expect(portal.getAttribute('role')).to.be.eql('dialog');

        var labelledById = portal.getAttribute('aria-labelledby');
        var describedById = portal.getAttribute('aria-describedby');

        var labelledBy = document.getElementById(labelledById);
        var describedBy = document.getElementById(describedById);

        expect(labelledBy).not.to.be.null;
        expect(describedBy).not.to.be.null;
    });

    it('Should repect the isOpen prop', function(){
        var modal = TestUtils.renderIntoDocument(
            <Modal isOpen={true}>
                <span></span>
            </Modal>
        );
        var portal = modal.modal.getDOMNode();
        expect(portal.getAttribute('class')).not.to.be.eql('hidden');
    });

    it('Renderer content til portal.content div', function () {
        var modal = TestUtils.renderIntoDocument(
            <Modal>
                <span className="forReference">Test</span>
            </Modal>
        );
        var portal = modal.modal;
        var content = portal.refs.content;

        var span = TestUtils.findRenderedDOMComponentWithClass(content, 'forReference');
        expect(span).not.to.be.null;
    });
});