/** @jsx React.DOM */
var React = require('react');
var EnkeltSok = require('./EnkeltSok.js');
var EnkelVisning = require('./EnkelVisning.js');
var EnkeltElement = require('./EnkeltElement.js');
var Utils = require('utils');

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
        this.sok = Utils.debounce(function (sokQuery) {
            this.props.sok(sokQuery).done(function (resultat) {
                this.setState({
                    elementer: resultat,
                    valgtElement: resultat[0] || {}
                })
            }.bind(this))
        }.bind(this), 150);
        return {
            elementer: this.props.elementer || [],
            valgtElement: this.props.valgtElement || {},
            valgtLocale: this.props.valgtLocale || Utils.Constants.LOCALE_DEFAULT
        };
    },
    componentDidMount: function () {
        this.sok(this.props.sokKomponentProps || {});
    },
    settValgtElement: function (element) {
        this.setState({valgtElement: element});
    },
    settValgtLocale: function (locale) {
        this.setState({valgtLocale: locale});
    },
    sokNavigasjon: function (event) {
        switch (event.keyCode) {
            case 38: /* pil opp */
                event.preventDefault();
                this.settValgtElement(hentElement(forrigeElement, this.state.elementer, this.state.valgtElement));
                break;
            case 40: /* pil ned */
                event.preventDefault();
                this.settValgtElement(hentElement(nesteElement, this.state.elementer, this.state.valgtElement));
                break;
        }
    },
    componentDidUpdate: function () {
        var $this = $(this.refs.tablist.getDOMNode());
        Utils.adjustScroll($this, $this.find('label.valgt').eq(0));
    },
    submit: function (event) {
        this.props.submit(this.state.valgtElement, this.state.valgtLocale);
        event.preventDefault();
    },
    render: function () {
        var listePanelId = Utils.generateId('sok-layout-');
        var forhandsvisningsPanelId = Utils.generateId('sok-layout-');

        var sokElementProps = $.extend({}, this.props.sokKomponentProps, {
            onChange: this.sok,
            onKeyDown: this.sokNavigasjon,
            ariaControls: listePanelId
        });

        var sokElement = React.createElement(this.props.sokKomponent, sokElementProps);
        var listeElementer = this.state.elementer.map(function (element) {
            return React.createElement(this.props.listeelementKomponent, {
                element: element,
                onClick: this.settValgtElement.bind(this, element),
                className: 'sok-element',
                key: element.key || '',
                role: 'tab',
                "aria-label": lagLabelFor(this.props.listeelementKomponent, element),
                erValgt: this.state.valgtElement === element,
                "aria-selected": this.state.valgtElement === element,
                "aria-controls": forhandsvisningsPanelId
            });
        }.bind(this));
        var visningElement = React.createElement(this.props.visningsKomponent, {
            element: this.state.valgtElement,
            locale: this.state.valgtLocale,
            settLocale: this.settValgtLocale
        });

        return (
            <form className={"sok-layout " + this.props.containerClassName} onSubmit={this.submit} >
                <div tabIndex="-1" className="sok-container">
                    {sokElement}
                </div>
                <div className="sok-visning">
                    <div tabIndex="-1" className="sok-liste" role="tablist" ref="tablist" id={listePanelId} aria-live="assertive" aria-atomic="true">
                        {listeElementer}
                    </div>
                    <div tabIndex="-1" className="sok-forhandsvisning" role="tabpanel" id={forhandsvisningsPanelId} aria-atomic="true" aria-live="polite">
                        {visningElement}
                    </div>
                </div>
                <input type="submit" value="submit" className="hidden" />
            </form>
        );
    }
});

var harSendtWarningOmManglendeAriaLabel = false;
function lagLabelFor(komponent, element) {
    try {
        return komponent.lagAriaLabel(element);
    } catch (e) {
        if (!harSendtWarningOmManglendeAriaLabel) {
            console.warn('Fant ingen "lagAriaLabel" i listevisningsKomponenten. Pass på at denne er lagt som static.');
            harSendtWarningOmManglendeAriaLabel = true;
        }
    }
}

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