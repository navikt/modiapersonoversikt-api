import React, { Component } from 'react';
import PT from 'prop-types';
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
    const element = React.createElement(tagType, { id, className }, data.text);
    return {
        id,
        hidden: data.show ? null : element,
        visible: data.show ? element : null
    };
}

class ModalPortal extends Component {

    componentWillMount() {
        this.focusAfterClose = undefined;
        this.setState((prevState, props) => {
           return {
               title: createAriaOptional('title', props.title),
               description: createAriaOptional('description', props.description),
               closeButton: createAriaOptional('closeButton', props.closeButton)
           };
        });
    }
    componentDidMount() {
        if (this.props.isOpen === true) {
            this.focusFirst();
        }
    }
    componentDidUpdate() {
        if (this.props.isOpen) {
            if (!$.contains(ReactDOM.findDOMNode(this.refs.content), document.activeElement)) {
                this.focusFirst();
            }
        } else {
            this.restoreFocus();
        }
    }
    keyHandler(event) {
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
    }
    handleTab(isShiftkey) {
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
    }
    focusFirst() {
        this.focusAfterClose = document.activeElement;
        let tabbables = $(ReactDOM.findDOMNode(this.refs.content)).find(':tabbable');
        this.props.skipFocus.forEach((skipFocusTag) => {
            tabbables = tabbables.not(skipFocusTag);
        });

        if (tabbables.length > 0) {
            tabbables.eq(0).focus();
        }
    }
    restoreFocus() {
        if (this.focusAfterClose) {
            this.focusAfterClose.focus();
            this.focusAfterClose = undefined;
        }
    }
    render() {
        let children = this.props.children;
        if (!children.hasOwnProperty('length')) {
            children = [children];
        }

        children.map((child) => React.cloneElement(child, { modal: this.props.modal }));

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
            <div
                tabIndex="-1"
                className={cls}
                aria-hidden={!this.props.isOpen}
                onKeyDown={this.keyHandler.bind(this)}
                role="dialog"
                aria-labelledby={title.id}
                aria-describedby={description.id}
            >
                <div className="backdrop" onClick={this.props.modal.close}></div>
                <iframe src="about:blank" className="cover" aria-hidden />
                {title.hidden}
                {description.hidden}
                <div className="centering" style={this.props.width ? { width: this.props.width + 'px' } : null}>
                    <div
                        className="content"
                        ref="content"
                        style={this.props.height ?
                        { height: this.props.height + 'px', marginTop: (this.props.height / -2) + 'px' }
                         : null}
                    >
                        {title.visible}
                        {description.visible}
                        {children}
                        {closeButton}
                    </div>
                </div>
            </div>
        );
    }
}

ModalPortal.propTypes = {
    description: PT.object,
        closeButton: PT.object,
        title: PT.object,
        isOpen: PT.bool,
        modal: PT.object.isRequired,
        skipFocus: PT.array,
        children: PT.node.isRequired,
        width: PT.number,
        height: PT.number
};

ModalPortal.defaultProps = {
    skipFocus: ['div'],
    isOpen: false
};


export default ModalPortal;
