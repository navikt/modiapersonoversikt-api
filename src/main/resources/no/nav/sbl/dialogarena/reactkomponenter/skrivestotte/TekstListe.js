/** @jsx React.DOM */
var React = ModiaJS.React;

var TekstListe = React.createClass({
    componentDidUpdate: function () {
        var $this = $(this.getDOMNode());
        adjustScroll($this, $this.find('label.valgt').eq(0));
    },
    lagListeElement: function (tekst) {
        var onClickCallback = function () {
            this.props.setValgtTekst(tekst);
        }.bind(this);

        var erValgt = this.props.valgtTekst === tekst;

        return (
            <div className="tekstElement" onClick={onClickCallback} key={tekst.key} id="tekstListePanel"
                role="tab" aria-selected={erValgt} aria-controls="tekstForhandsvisningPanel" aria-live="assertive" aria-aria-atomic="true"
                aria-label={tekst.tittel}>
                <input id={"tekstElementRadio" + tekst.key} name="tekstListeRadio" type="radio" readOnly checked={erValgt} />
                <label htmlFor={"tekstElementRadio" + tekst.key}>
                    <h4 dangerouslySetInnerHTML={{__html: tekst.tittel}}></h4>
                </label>
            </div>
        );
    },
    render: function () {
        var listeElementer = this.props.tekster.map(this.lagListeElement);

        return (
            <div className="tekstListe" role="tablist">
                {listeElementer}
            </div>
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
