var Utils = require('utils');
var Store = require('./../Store');

var MeldingerSokStore = function () {
    Store.apply(this, arguments);
    if (this.state.traader.length > 0) {
        this.state.valgtTraad = this.state.traader[0];
    }
    this.state.initialisert = false;
};
MeldingerSokStore.prototype = $.extend({}, Store.prototype, MeldingerSokStore.prototype);

MeldingerSokStore.prototype.onChange = function (event) {
    this.state.fritekst = event.target.value;

    hentSokeresultater.bind(this)(this.state.fritekst);

    this.fireUpdate(this.listeners);
};

MeldingerSokStore.prototype.update = function (props) {
    $.extend(this.state, props);
    $.ajax({
        async: false,
        url: '/modiabrukerdialog/rest/meldinger/' + this.state.fnr + '/indekser'
    });

    this.onChange({target: {value: this.state.fritekst}});

    this.fireUpdate(this.listeners);
};

MeldingerSokStore.prototype.traadChanged = function (traad, tabliste) {
    this.state.valgtTraad = traad;

    updateScroll(tabliste, this.state.traader.indexOf(this.state.valgtTraad));

    this.fireUpdate(this.listeners);
};

MeldingerSokStore.prototype.onKeyDown = function (tabliste, event) {
    switch (event.keyCode) {
        case 38: /* pil opp */
            event.preventDefault();
            this.state.valgtTraad = hentMelding(forrigeMelding, this.state.traader, this.state.valgtTraad);

            updateScroll(tabliste.getDOMNode(), this.state.traader.indexOf(this.state.valgtTraad));

            this.fireUpdate(this.listeners);
            break;
        case 40: /* pil ned */
            event.preventDefault();
            this.state.valgtTraad = hentMelding(nesteMelding, this.state.traader, this.state.valgtTraad);

            updateScroll(tabliste.getDOMNode(), this.state.traader.indexOf(this.state.valgtTraad));

            this.fireUpdate(this.listeners);
            break;
    }
};

MeldingerSokStore.prototype.oppdaterTraadRefs = function (traadMarkupIds) {
    this.state.traadMarkupIds = traadMarkupIds;
};

MeldingerSokStore.prototype.submit = function (afterSubmit, event) {
    event.preventDefault();
    $('#' + this.state.traadMarkupIds[this.state.valgtTraad.traadId]).click();
    afterSubmit();
};

function updateScroll(tabliste, valgtIndex) {
    var $parent = $(tabliste);
    var $valgt = $parent.find('.sok-element').eq(valgtIndex);

    Utils.adjustScroll($parent, $valgt);
}

var hentSokeresultater =
    Utils.debounce(function (fritekst) {
        sok(this.state.fnr, fritekst)
            .done(function (traader) {
                traader.forEach(function (traad) {
                    traad.key = traad.traadId;
                    traad.datoInMillis = traad.dato.millis;
                    traad.innhold = traad.meldinger[0].fritekst;
                    traad.opprettetDato = traad.meldinger[0].opprettetDatoTekst;

                    traad.meldinger.forEach(function (melding) {
                        melding.erInngaaende = ['SPORSMAL_SKRIFTLIG', 'SVAR_SBL_INNGAAENDE'].indexOf(melding.meldingstype) >= 0;
                        melding.fraBruker = melding.erInngaaende ? melding.fnrBruker : melding.navIdent;
                    });
                });
                this.state.traader = traader;
                this.state.valgtTraad = traader[0] || {};
                this.state.initialisert = true;
                this.fireUpdate(this.listeners);
            }.bind(this))
            .fail(function () {
                $('.innboksSokToggle').click();
            }.bind(this))

    }, 150);

var sok = function (fnr, query) {
    query = query || "";
    query = query.replace(/\./g, '');
    var url = '/modiabrukerdialog/rest/meldinger/' + fnr + '/sok/' + encodeURIComponent(query);
    return $.get(url);
};

function hentMelding(hentElement, elementer, valgtElement) {
    for (var i = 0; i < elementer.length; i++) {
        if (elementer[i].key === valgtElement.key) {
            return hentElement(elementer, i);
        }
    }
}

function forrigeMelding(elementer, index) {
    return index === 0 ? elementer[0] : elementer[index - 1];
}

function nesteMelding(elementer, index) {
    return index === elementer.length - 1 ? elementer[elementer.length - 1] : elementer[index + 1];
}
module.exports = MeldingerSokStore;