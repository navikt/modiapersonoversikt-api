var Utils = require('utils');

var Store = function (state) {
    this.listeners = [];
    this.state = state;
    if (this.state.henvendelser.length > 0) {
        this.state.valgtHenvendelse = this.state.henvendelser[0];
    }
};

Store.prototype.addListener = function (listener) {
    this.listeners.push(listener);
};

Store.prototype.removeListener = function (listener) {
    var nyeListeners = this.listeners.slice(0);
    var index = nyeListeners.indexOf(listener);
    this.listeners.splice(index, 1);
};

Store.prototype.getState = function () {
    return this.state;
};

Store.prototype.onChange = function (event) {
    this.state.fritekst = event.target.value;

    hentSokeresultater.bind(this)(this.state.fritekst);

    fireUpdate(this.listeners);
};

Store.prototype.henvendelseChanged = function(henvendelse) {
    this.state.valgtHenvendelse = henvendelse;
    fireUpdate(this.listeners);
}

Store.prototype.onKeyDown = function(event) {
    switch (event.keyCode) {
        case 38: /* pil opp */
            event.preventDefault();
            this.state.valgtHenvendelse = hentHenvendelse(forrigeHenvendelse, this.state.henvendelser, this.state.valgtHenvendelse);
            fireUpdate(this.listeners);
            break;
        case 40: /* pil ned */
            event.preventDefault();
            this.state.valgtHenvendelse = hentHenvendelse(nesteHenvendelse, this.state.henvendelser, this.state.valgtHenvendelse);
            fireUpdate(this.listeners);
            break;
    }
}

Store.prototype.oppdaterTraadRefs = function(traadMarkupIds){
    this.state.traadMarkupIds = traadMarkupIds;
};

Store.prototype.submit = function(onSubmit, event){
    event.preventDefault();

    $('#' + this.state.traadMarkupId[this.state.valgtTraad.traadId]).click();
    onSubmit();
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
            this.state.henvendelser = traader;
            this.state.valgtHenvendelse = traader[0] || {};
            fireUpdate(this.listeners);
        }.bind(this))
    }, 150);

var sok = function (fnr, query) {
    query = query || "";
    var url = '/modiabrukerdialog/rest/meldinger/' + fnr + '/sok/' + encodeURIComponent(query);
    return $.get(url);
};


function fireUpdate(listeners) {
    listeners.forEach(function (listener) {
        listener();
    });
}


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

module.exports = Store;