/** @jsx React.DOM */
var React = require('react');

var Filter = React.createClass({
    getInitialState: function () {
        return {
            fritekst: ""
        }
    },
    setFritekst: function (event) {
        this.props.setFritekst(event.nativeEvent.target.value);
    },
    render: function () {
        return (
            <div>
                <input type="text" placeholder="Søk" onChange={this.setFritekst} />
            </div>
        );
    }
});

module.exports = Filter;
