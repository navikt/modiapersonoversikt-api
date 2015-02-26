/** @jsx React.DOM */
var React = require('react');
var EnkeltSok = require('./EnkeltSok.js');
var EnkelVisning = require('./EnkelVisning.js');
var EnkeltElement = require('./EnkeltElement.js');
var Utils = require('./Utils.js');

var Soklayout = React.createClass({
    getDefaultProps: function () {
        return {
            sokKomponent: EnkeltSok,
            listeelementKomponent: EnkeltElement,
            visningsKomponent: EnkelVisning,
            sok: function () {
                console.warn('Fant ingen søk funksjon');
            },
            submit: function () {
                console.warn('Fant ingen submit handler');
            }
        }
    },
    getInitialState: function () {
        return {
            elementer: this.props.elementer || [],
            valgtElement: this.props.valgtElement || {},
            valgtLocale: this.props.valgtLocale || Utils.Constants.LOCALE_DEFAULT
        };
    },
    componentDidMount: function () {
        this.sok(undefined);
    },
    settValgtElement: function (element) {
        this.setState({valgtElement: element});
    },
    settValgtLocale: function (locale) {
        this.setState({valgtLocale: locale});
    },
    sok: Utils.debounce(function (sokQuery) {
        this.props.sok(sokQuery).done(function (resultat) {
            this.setState({
                elementer: resultat,
                valgtElement: resultat[0] || {}
            })
        }.bind(this));
    }, 150),
    sokNavigasjon: function () {
        switch (event.keyCode) {
            case 38: /* pil opp */
                event.preventDefault();
                this.settValgtElement(hentElement(forrigeElement, this.state.elementer, this.state.valgtElement));
                break;
            case 40: /* pil ned */
                event.preventDefault();
                this.settValgtElement(hentElement(nesteElement, this.state.elementer, this.state.valgtElement));
                break;
            case 13: /* enter */
                event.preventDefault();
                this.props.submit(this.state.valgtElement, this.state.valgtLocale);
                break;
        }
    },
    componentDidUpdate: function () {
        var $this = $(this.refs.tablist.getDOMNode());
        Utils.adjustScroll($this, $this.find('label.valgt').eq(0));
    },
    render: function () {
        var sokElement = React.createElement(this.props.sokKomponent, {
            onChange: this.sok,
            onKeyDown: this.sokNavigasjon
        });
        var listeElementer = this.state.elementer.map(function (element) {
            return React.createElement(this.props.listeelementKomponent, {
                element: element,
                onClick: this.settValgtElement.bind(this, element),
                className: 'tekstElement',
                key: element.key || '',
                role: 'tab',
                "aria-live": "assertive",
                "aria-atomic": true,
                "aria-label": element.tittel || element.title || '',
                erValgt: this.state.valgtElement === element
            });
        }.bind(this));
        var visningElement = React.createElement(this.props.visningsKomponent, {
            element: this.state.valgtElement,
            locale: this.state.valgtLocale,
            settLocale: this.settValgtLocale,
            submit: function () {
                this.props.submit(this.state.valgtElement, this.state.valgtLocale);
            }.bind(this)
        });

        return (
            <div className="tekstforslag">
                {sokElement}
                <div className="tekstvisning">
                    <div tabIndex="-1" className="tekstListe" role="tablist" ref="tablist">
                        {listeElementer}
                    </div>
                    <div tabIndex="-1" className="tekstForhandsvisning" role="tabpanel" id="tekstForhandsvisningPanel" aria-atomic="true" aria-live="polite">
                        {visningElement}
                    </div>
                </div>
            </div>
        );
    }
});

function hentElement(hentElement, elementer, valgtElement) {
    for (var i = 0; i < elementer.length; i++) {
        if (elementer[i].key === valgtElement.key) {
            return hentElement(elementer, i);
        }
    }
}
function forrigeElement(elementer, index) {
    return index === 0 ? elementer[0] : elementer[index - 1];
}
function nesteElement(elementer, index) {
    return index === elementer.length - 1 ? elementer[elementer.length - 1] : elementer[index + 1];
}

module.exports = Soklayout;