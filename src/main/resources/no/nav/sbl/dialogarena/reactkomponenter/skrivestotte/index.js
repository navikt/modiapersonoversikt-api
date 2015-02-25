var React = require('react');
var Modal = require('modal');
var Tekstforslag = require('./Tekstforslag.js');

module.exports = React.createClass({
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