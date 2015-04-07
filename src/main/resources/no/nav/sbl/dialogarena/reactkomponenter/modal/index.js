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
            }
        }
    },
    componentDidMount: function () {
        if (typeof this.portalElement === 'undefined') {
            this.portalElement = document.createElement('div');
            this.portalElement.className = "react-modal-container";
            document.body.appendChild(this.portalElement);
        }

        this.renderPortal(this.props, this.state);
    },
    componentWillReceiveProps: function (props) {
        this.renderPortal(props, this.state)
    },
    componentWillUnmount: function () {
        document.body.removeChild(this.portalElement);
    },
    componentDidUpdate: function(){
        this.renderPortal(this.props, this.state)
    },
    open: function () {
        this.setState({isOpen: true});
    },
    close: function () {
        this.setState({isOpen: false});
    },
    renderPortal: function (props, state) {
        var modal = {
            open: this.open,
            close: this.close
        };

        this.modal = React.render(<Portal {...props} {...state} modal={modal} />, this.portalElement);
    },
    render: function () {
        return null;
    }
});

module.exports = Modal;
