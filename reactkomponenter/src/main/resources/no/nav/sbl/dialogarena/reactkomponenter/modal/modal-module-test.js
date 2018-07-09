/* eslint-env mocha */
/* eslint no-unused-expressions:0 */
import './../test-config';
import { expect } from 'chai';
import React from 'react';
import ReactDOM from 'react-dom';
import Modal from './modal-module';
import Portal from './modal-portal';
import TestUtils from 'react-dom/test-utils';
import { createRenderer } from 'react-test-renderer/shallow';

const SPAN_CONTENT = 'span-content';

function createModal(props, children) {
    return TestUtils.renderIntoDocument(React.createElement(
            Modal, props || {},
            children || React.createElement('span', { className: 'forReference' }, SPAN_CONTENT))
    );
}

function createPortal(shouldBeOpen) {
    const modal = {
        open: shouldBeOpen,
        close: () => this.close(false)
    };
    const props = {
        title : {
            tag: 'h1',
            id: 'id'
        },
        description: {
            tag: 'p'
        },
        closeButton: {
            tag: 'span'
        },
        isOpen: shouldBeOpen
    };

    const portals = <Portal {...props} modal={modal} ><span className={'portal-content'}>{SPAN_CONTENT}</span></Portal>;
    return TestUtils.renderIntoDocument(portals);
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
        const portalVisible = false;
        const portal = ReactDOM.findDOMNode(createPortal(portalVisible));

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
    });

    it('repects the isOpen prop', () => {
        const portalVisible = true;
        const portal = ReactDOM.findDOMNode(createPortal(portalVisible));
        expect(portal.getAttribute('class')).not.to.be.eql('hidden');
    });

    it('Renders content til portal.content div', () => {
        const portalVisible = true;
        const portal = ReactDOM.findDOMNode(createPortal(portalVisible));
        const contentNode = portal.getElementsByClassName('portal-content')[0];
        expect(contentNode.textContent).to.be.eql(SPAN_CONTENT);
    });

});
