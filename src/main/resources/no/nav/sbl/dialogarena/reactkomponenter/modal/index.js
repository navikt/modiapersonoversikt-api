var React = require('react');
var Portal = require('./ModalPortal.js');

var Modal = React.createClass({
    getInitialState: function () {
        return {
            isOpen: this.props.isOpen || false
        };
    },
    getDefaultProps: function () {
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
        }
    },
    componentDidMount: function () {
        if (typeof this.portalElement === 'undefined') {
            this.portalElement = document.createElement('div');
            this.portalElement.className = "react-modal-container";
            document.body.appendChild(this.portalElement);
        }

        this.renderPortal(this.props, this.state)
    },
    componentWillReceiveProps: function (props) {
        this.renderPortal(props, this.state)
    },
    componentWillUnmount: function () {
        document.body.removeChild(this.portalElement);
    },
    componentDidUpdate: function () {
        this.renderPortal(this.props, this.state)
    },
    open: function () {
        var elementsByClassName = document.getElementsByClassName('react-modal-container');
        var match = false;
        for (var i = 0; i < elementsByClassName.length; i++) {
            if (elementsByClassName[i].innerHTML === this.portalElement.innerHTML) {
                match = true;
                break;
            }
        }

        if (!match) {
            document.body.appendChild(this.portalElement);
        }

        $(document.body).addClass('modal-open');
        this.setState({isOpen: true});
    },
    close: function () {
        this.setState({isOpen: false});
        document.body.removeChild(this.portalElement);
        $(document.body).removeClass('modal-open');
    },
    renderPortal: function (props, state) {
        var modal = {
            open: this.open,
            close: this.close
        };

        this.modal = React.render(<Portal {...props} {...state} modal={modal}/>, this.portalElement);
    },
    render: function () {
        return null;
    }
});

module.exports = Modal;
