var React = ModiaJS.React;
var Portal = require('./ModalPortal.js');

var Modal = React.createClass({
    componentDidMount: function () {
        if (typeof this.portalElement === 'undefined') {
            this.portalElement = document.createElement('div');
            this.portalElement.className = "react-modal-container";
            document.body.appendChild(this.portalElement);
        }

        this.renderPortal(this.props);
    },
    componentWillReceiveProps: function (props) {
        this.renderPortal(props)
    },
    componentWillUnmount: function () {
        docuement.body.removeChild(this.portalElement);
    },
    renderPortal: function (props) {
        this.modal = React.render(<Portal {...props} />, this.portalElement);
    },
    render: function () {
        return null;
    }
});

window.ModiaJS.Components.Modal = Modal;
module.exports = Modal;
