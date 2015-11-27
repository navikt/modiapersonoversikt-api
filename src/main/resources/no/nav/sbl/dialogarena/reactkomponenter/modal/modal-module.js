const React = require('react');
const Portal = require('./modal-portal');

const Modal = React.createClass({
    propTypes: {
        'isOpen': React.PropTypes.bool,
        'onClose': React.PropTypes.func
    },
    getDefaultProps: function getDefaultProps() {
        return {
            title: {
                text: 'Modal Title',
                show: false,
                tag: 'h1'
            },
            description: {
                text: '',
                show: false,
                tag: 'div'
            },
            closeButton: {
                text: '',
                show: true,
                tag: 'span'
            }
        };
    },
    getInitialState: function getInitialState() {
        return {
            isOpen: this.props.isOpen || false
        };
    },
    componentDidMount: function componentDidMount() {
        if (typeof this.portalElement === 'undefined') {
            this.portalElement = document.createElement('div');
            this.portalElement.className = 'react-modal-container';
            document.body.appendChild(this.portalElement);
        }

        this.renderPortal(this.props, this.state);
    },
    componentWillReceiveProps: function componentWillReceiveProps(props) {
        this.renderPortal(props, this.state);
    },
    componentDidUpdate: function componentDidUpdate() {
        this.renderPortal(this.props, this.state);
    },
    componentWillUnmount: function componentWillUnmount() {
        this.close();
    },
    open: function open() {
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
        this.setState({isOpen: true});
    },
    close: function close(force = true) {
        const precheck = (this.props.onClose || function noOncloseCallbackFound() {return true;})(force);
        if (!force && !precheck) {
            return;
        }

        this.setState({isOpen: false});
        document.body.removeChild(this.portalElement);
        $(document.body).removeClass('modal-open');
        $(document.body).children().removeAttr('aria-hidden');
    },
    renderPortal: function renderPortal(props, state) {
        const modal = {
            open: this.open,
            close: () => this.close(false)
        };

        this.modal = React.render(<Portal {...props} {...state} modal={modal}/>, this.portalElement);
    },
    render: function render() {
        return null;
    }
});

module.exports = Modal;
