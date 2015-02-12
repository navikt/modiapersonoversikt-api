/** @jsx React.DOM */
var React = require('react');

var TekstListe = React.createClass({
    componentDidUpdate: function () {
        var $this = $(this.getDOMNode());
        adjustScroll($this, $this.find('label.valgt').eq(0));
    },
    render: function () {
        var tekster = this.props.tekster;
        var listeElementer = tekster.map(this.lagListeElement);

        return (
            <div className="tekstListe">
                {listeElementer}
            </div>
        );
    },

    lagListeElement: function (tekst) {
        var onClickCallback = function () {
            this.props.setValgtTekst(tekst);
        }.bind(this);

        return (
            <label className={this.props.valgtTekst === tekst ? 'tekstElement valgt' : 'tekstElement'} onClick={onClickCallback}>
                <input name="tekstElementRadio" type="radio" />
                <h4 dangerouslySetInnerHTML={{__html: tekst.tittel}}></h4>
                <p dangerouslySetInnerHTML={{__html: tekst.innhold["nb_NO"]}}></p>
            </label>
        );
    }
});

function adjustScroll($parent, $element) {
    if ($element.length === 0) {
        return;
    }

    var elementTop = $element.position().top;
    var elementBottom = elementTop + $element.outerHeight();

    if (elementTop < 0) {
        $parent.scrollTop($parent.scrollTop() + elementTop);
    } else if (elementBottom > $parent.outerHeight()) {
        $parent.scrollTop($parent.scrollTop() + (elementBottom - $parent.outerHeight()));
    }
}

module.exports = TekstListe;
