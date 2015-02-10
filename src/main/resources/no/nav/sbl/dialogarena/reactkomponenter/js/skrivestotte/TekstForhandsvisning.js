/** @jsx React.DOM */
var React = require('react');

var TekstForhandsvisning = React.createClass({
    render: function () {
        var paragraphs = lagParagrafer(this.props.tekst.innhold);

        return (
            <div className="tekstForhandsvisning">
                {paragraphs}
            </div>
        );
    }
});

function lagParagrafer(tekst) {
    var paragrafer = null;

    if (typeof tekst !== 'undefined') {
        paragrafer = tekst.split('\n').map(function (linje) {
            return <p>{linje}</p>
        });
    }

    return paragrafer;
}

module.exports = TekstForhandsvisning;
