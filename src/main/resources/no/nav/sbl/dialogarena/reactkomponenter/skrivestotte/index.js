var React = ModiaJS.React;
var Modal = ModiaJS.Components.Modal;
var Tekstforslag = require('./Tekstforslag.js');

window.ModiaJS.Components.Skrivestotte = React.createClass({
    vis: function () {
        this.refs.modal.open();
    },
    skjul: function () {
        this.refs.modal.close();
    },
    render: function () {
        return (
            <Modal ref="modal">
                <Tekstforslag {...this.props}/>
            </Modal>
        );
    }
});