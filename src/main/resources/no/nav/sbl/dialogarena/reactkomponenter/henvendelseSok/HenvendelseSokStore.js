var Utils = require('utils');
var Store = require('./../store');

var HenvendelseSokStore = function () {
    Store.apply(this, arguments);
    if (this.state.traader.length > 0) {
        this.state.valgtTraad = this.state.traader[0];
    }
};
HenvendelseSokStore.prototype = $.extend({}, Store.prototype, HenvendelseSokStore.prototype);

HenvendelseSokStore.prototype.onChange = function (event) {
    this.state.fritekst = event.target.value;

    hentSokeresultater.bind(this)(this.state.fritekst);

    this.fireUpdate(this.listeners);
};

HenvendelseSokStore.prototype.traadChanged = function (traad) {
    this.state.valgtTraad = traad;
    this.fireUpdate(this.listeners);
}

HenvendelseSokStore.prototype.onKeyDown = function (event) {
    switch (event.keyCode) {
        case 38: /* pil opp */
            event.preventDefault();
            this.state.valgtTraad = hentHenvendelse(forrigeHenvendelse, this.state.traader, this.state.valgtTraad);
            this.fireUpdate(this.listeners);
            break;
        case 40: /* pil ned */
            event.preventDefault();
            this.state.valgtTraad = hentHenvendelse(nesteHenvendelse, this.state.traader, this.state.valgtTraad);
            console.log('event', event);
            this.fireUpdate(this.listeners);
            break;
    }
}

HenvendelseSokStore.prototype.oppdaterTraadRefs = function (traadMarkupIds) {
    this.state.traadMarkupIds = traadMarkupIds;
};

HenvendelseSokStore.prototype.submit = function (afterSubmit, event) {
    event.preventDefault();
    $('#' + this.state.traadMarkupIds[this.state.valgtTraad.traadId]).click();
    afterSubmit();
};

var hentSokeresultater =
    Utils.debounce(function (fritekst) {
        sok(this.state.fnr, fritekst).done(function (traader) {
            traader.forEach(function (traad) {
                traad.key = traad.traadId;
                traad.datoInMillis = traad.dato.millis;
                traad.innhold = traad.meldinger[0].fritekst;
                traad.meldinger.forEach(function (melding) {
                    melding.erInngaaende = ['SPORSMAL_SKRIFTLIG', 'SVAR_SBL_INNGAAENDE'].indexOf(melding.meldingstype) >= 0;
                    melding.fraBruker = melding.erInngaaende ? melding.fnrBruker : melding.eksternAktor;
                });
            });
            console.log('traader', traader);
            this.state.traader = traader;
            this.state.valgtTraad = traader[0] || {};
            this.fireUpdate(this.listeners);
        }.bind(this))
    }, 150);

var sok = function (fnr, query) {
    query = query || "";
    var url = '/modiabrukerdialog/rest/meldinger/' + fnr + '/sok/' + encodeURIComponent(query);
    return $.get(url);
};

function hentHenvendelse(hentElement, elementer, valgtElement) {
    for (var i = 0; i < elementer.length; i++) {
        if (elementer[i].key === valgtElement.key) {
            return hentElement(elementer, i);
        }
    }
}

function forrigeHenvendelse(elementer, index) {
    return index === 0 ? elementer[0] : elementer[index - 1];
}

function nesteHenvendelse(elementer, index) {
    return index === elementer.length - 1 ? elementer[elementer.length - 1] : elementer[index + 1];
}
module.exports = HenvendelseSokStore;