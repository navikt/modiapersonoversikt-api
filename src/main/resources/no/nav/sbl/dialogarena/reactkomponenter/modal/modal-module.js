import React from 'react';
import Portal from './modal-portal';

class Modal extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            isOpen: this.props.isOpen || false
        };

        this.open = this.open.bind(this);
        this.close = this.close.bind(this);
        this.renderPortal = this.renderPortal.bind(this);
    }

    componentDidMount() {
        if (typeof this.portalElement === 'undefined') {
            this.portalElement = document.createElement('div');
            this.portalElement.className = 'react-modal-container';
            document.body.appendChild(this.portalElement);
        }

        this.renderPortal(this.props, this.state);
    }

    componentWillReceiveProps(props) {
        this.renderPortal(props, this.state);
    }

    componentDidUpdate() {
        this.renderPortal(this.props, this.state);
    }

    componentWillUnmount() {
        this.close();
    }

    open() {
        const elementsByClassName = document.getElementsByClassName('react-modal-container');
        let match = false;
        for (let i = 0; i < elementsByClassName.length; i++) {
            if (elementsByClassName[i].innerHTML === this.portalElement.innerHTML) {
                match = true;
                break;
            }
        }

        if (!match) {
            document.body.appendChild(this.portalElement);
        }

        $(document.body).addClass('modal-open');
        $(document.body).children().not(this.portalElement).attr('aria-hidden', true);
        this.setState({ isOpen: true });
    }

    close(force = true) {
        const precheck = (this.props.onClosing || function noOncloseCallbackFound() {
            return true;
        })(force);
        if (!force && !precheck) {
            return;
        }

        this.setState({ isOpen: false });
        document.body.removeChild(this.portalElement);
        $(document.body).removeClass('modal-open');
        $(document.body).children().removeAttr('aria-hidden');
    }

    renderPortal(props, state) {
        const modal = {
            open: this.open,
            close: () => this.close(false)
        };

        this.modal = React.render(<Portal {...props} {...state} modal={modal}/>, this.portalElement);
    }

    render() {
        return null;
    }
}

export function defaultHelper(text, show, tag) {
    return { text, show, tag };
}

Modal.defaultProps = {
    title: defaultHelper('Modal Title', false, 'h1'),
    description: defaultHelper('', false, 'div'),
    closeButton: defaultHelper('', true, 'span')
};

export const AriaPropType = React.PropTypes.shape({
    text: React.PropTypes.string,
    show: React.PropTypes.bool,
    tag: React.PropTypes.string
});

Modal.propTypes = {
    'isOpen': React.PropTypes.bool,
    'onClosing': React.PropTypes.func,
    'title': AriaPropType,
    'description': AriaPropType,
    'closeButton': AriaPropType
};

export default Modal;
