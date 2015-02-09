/** @jsx React.DOM */
var React = require('react');

var TekstForhandsvisning = React.createClass({
    render: function () {
        return (
            <div className="tekstForhandsvisning">
                <p>{this.props.tekst.innhold}</p>
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
