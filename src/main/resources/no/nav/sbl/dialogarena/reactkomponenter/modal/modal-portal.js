import React, { PropTypes as pt } from 'react';
import ReactDOM from 'react-dom';

function createId(prefix) {
    return prefix + new Date().getTime() + '-' + Math.random();
}

function createAriaOptional(name, data) {
    const id = createId('react-modalx-' + name + '-');
    const tagComponent = data.tag.split('.');
    const tagType = tagComponent[0];
    let className = '';

    if (tagComponent.length > 1) {
        className = tagComponent[1];
    }
    const element = React.createElement(tagType, { id: id, className: className }, data.text);
    return {
        id: id,
        hidden: data.show ? null : element,
        visible: data.show ? element : null
    };
}

const ModalPortal = React.createClass({
    propTypes: {
        description: pt.object,
        closeButton: pt.object,
        title: pt.object,
        isOpen: pt.bool,
        modal: pt.object.isRequired,
        skipFocus: pt.array,
        children: pt.object.isRequired,
        width: pt.number,
        height: pt.number
    },
    getDefaultProps: function getDefaultProps() {
        return {
            skipFocus: ['div'],
            isOpen: false
        };
    },
    getInitialState: function getInitialState() {
        return {
            title: createAriaOptional('title', this.props.title),
            description: createAriaOptional('description', this.props.description),
            closeButton: createAriaOptional('closeButton', this.props.closeButton)
        };
    },
    componentDidMount: function componentDidMount() {
        if (this.props.isOpen === true) {
            this.focusFirst();
        }
    },
    componentDidUpdate: function componentDidUpdate() {
        if (this.props.isOpen) {
            if (!$.contains(ReactDOM.findDOMNode(this.refs.content), document.activeElement)) {
                this.focusFirst();
            }
        } else {
            this.restoreFocus();
        }
    },
    focusAfterClose: undefined,
    keyHandler: function keyHandler(event) {
        const keyMap = {
            27: function escHandler() { // ESC
                this.props.modal.close();
                event.preventDefault();
            },
            9: function tabHandler() { // TAB
                if (this.handleTab(event.shiftKey)) {
                    event.preventDefault();
                }
            }
        };

        (keyMap[event.keyCode] || function passThrough() {
        }).bind(this)();

        // No leaks
        event.stopPropagation();
    },
    handleTab: function handleTab(isShiftkey) {
        const $content = $(ReactDOM.findDOMNode(this.refs.content));
        const focusable = $content.find(':tabbable');
        const lastValidIndex = isShiftkey ? 0 : focusable.length - 1;


        const currentFocusElement = $content.find(':focus');

        if (focusable.eq(lastValidIndex).is(currentFocusElement)) {
            const newFocusIndex = isShiftkey ? focusable.length - 1 : 0;
            focusable.eq(newFocusIndex).focus();
            return true;
        }
        return false;
    },
    focusFirst: function focusFirst() {
        this.focusAfterClose = document.activeElement;
        let tabbables = $(ReactDOM.findDOMNode(this.refs.content)).find(':tabbable');
        this.props.skipFocus.forEach(function removeFromTabbables(skipFocusTag) {
            tabbables = tabbables.not(skipFocusTag);
        });

        if (tabbables.length > 0) {
            tabbables.eq(0).focus();
        }
    },
    restoreFocus: function restoreFocus() {
        if (this.focusAfterClose) {
            this.focusAfterClose.focus();
            this.focusAfterClose = undefined;
        }
    },
    render: function render() {
        let children = this.props.children;
        if (!children.hasOwnProperty('length')) {
            children = [children];
        }

        children.map(function addModalProp(child) {
            return React.cloneElement(child, { modal: this.props.modal });
        }.bind(this));

        const title = this.state.title;
        const description = this.state.description;
        let closeButton = null;
        if (this.props.closeButton.show) {
            closeButton = (
                <button className="closeButton" onClick={this.props.modal.close}>
                    {this.state.closeButton.visible}
                </button>
            );
        }

        const cls = this.props.isOpen ? '' : 'hidden';

        // iframe må være her for å fikse en rendering bug i IE, uten iframe'n vil inline-pdf-visningen alltid legge
        // seg over modal-vinduene.
        return (
            <div tabIndex="-1" className={cls} aria-hidden={!this.props.isOpen} onKeyDown={this.keyHandler}
                 role="dialog" aria-labelledby={title.id} aria-describedby={description.id}>
                <div className="backdrop" onClick={this.props.modal.close}></div>
                <iframe src="about:blank" className="cover" aria-hidden/>
                {title.hidden}
                {description.hidden}
                <div className="centering" style={this.props.width ? {'width': this.props.width + 'px'} : null}>
                    <div className="content" ref="content"
                         style={this.props.height ? {'height': this.props.height + 'px', 'marginTop': (this.props.height / -2) + 'px'} : null}>
                        {title.visible}
                        {description.visible}
                        {children}
                        {closeButton}
                    </div>
                </div>
            </div>
        );
    }
});

export default ModalPortal;
