/** @jsx React.DOM */
var React = require('react');

var TekstForhandsvisning = React.createClass({
    render: function () {
        return (
            <div className="tekstForhandsvisning">
                <h1>{this.props.tekst.tittel}</h1>
                <p>{this.props.tekst.innhold}</p>
            </div>
        );
    }
});

module.exports = TekstForhandsvisning;
