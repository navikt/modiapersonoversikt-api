/* eslint-env mocha */
/* eslint no-unused-expressions:0 */
import './../test-config';
import { expect } from 'chai';
import React from 'react';
import ReactDOM from 'react-dom';
import Modal from './modal-module';
import TestUtils from 'react-addons-test-utils';

const SPAN_CONTENT = 'span-content';

function createModal(props, children) {
    return TestUtils.renderIntoDocument(React.createElement(
            Modal, props || {},
            children || React.createElement('span', { className: 'forReference' }, SPAN_CONTENT))
    );
}

function getContent(modal) {
    return modal.modal.refs.content;
}

describe('Modal', () => {
    afterEach((done) => {
        ReactDOM.unmountComponentAtNode(document.body);
        document.body.innerHTML = '';
        setTimeout(done);
    });

    it('creates portal as a direct child of body', () => {
        const modal = createModal();

        const modalContainer = document.getElementsByClassName('react-modal-container');
        expect(modalContainer).not.to.be.null;
        expect(modal).not.to.be.null;
    });

    it('default is closed and have the correct attributes', () => {
        const modal = createModal();
        const portal = ReactDOM.findDOMNode(modal.modal);

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

        const labelledById = portal.getAttribute('aria-labelledby');
        const describedById = portal.getAttribute('aria-describedby');

        const labelledBy = document.getElementById(labelledById);
        const describedBy = document.getElementById(describedById);

        expect(labelledBy).not.to.be.null;
        expect(describedBy).not.to.be.null;
    });

    it('repects the isOpen prop', () => {
        const modal = createModal({ isOpen: true });

        const portal = ReactDOM.findDOMNode(modal.modal);
        expect(portal.getAttribute('class')).not.to.be.eql('hidden');
    });

    it('Renders content til portal.content div', () => {
        const modal = createModal();
        const content = getContent(modal);

        expect(content.textContent).to.be.eql(SPAN_CONTENT);
    });
});
