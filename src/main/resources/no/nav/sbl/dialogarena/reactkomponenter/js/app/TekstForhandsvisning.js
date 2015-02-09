/** @jsx React.DOM */
var React = require('react');

var TekstForhandsvisning = React.createClass({
    render: function () {
        return (
            <div className="tekstForhandsvisning">
                <p>{this.props.tekst.tekst}</p>
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
