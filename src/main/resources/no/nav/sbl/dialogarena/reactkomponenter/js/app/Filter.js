/** @jsx React.DOM */
var React = require('react');

var Filter = React.createClass({
    getInitialState: function () {
        return {
            temagrupper: [{kode: 'ARBD', navn: 'Arbeid'}, {kode: 'FMLI', navn: 'Familie'}]
        }
    },
    componentDidMount: function () {
        this.props.setFilter({temagruppe: this.state.temagrupper[0].kode});
    },
    setFilter: function (event) {
        this.props.setFilter({temagruppe: event.nativeEvent.target.value});
    },
    render: function () {
        return (
            <div>
                <select onChange={this.setFilter}>
                {
                    this.state.temagrupper.map(function (temagruppe) {
                        return (
                            <option value={temagruppe.kode}>{temagruppe.navn}</option>
                        );
                    })
                    }
                </select>
            </div>
        );
    }
});

module.exports = Filter;
