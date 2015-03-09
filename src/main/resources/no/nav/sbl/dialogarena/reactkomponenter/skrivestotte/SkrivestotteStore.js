var Utils = require('utils');
var Store = require('./../store');

var SkrivestotteStore = function () {
    Store.apply(this, arguments);
    if(this.state.tekster.length > 0){
        this.state.valgtTekst = this.state.tekster[0];
    }
};
SkrivestotteStore.prototype = $.extend({}, Store.prototype, SkrivestotteStore.prototype);

SkrivestotteStore.prototype.tekstChanged = function (tekst) {
    this.state.valgtTekst = tekst;
    this.fireUpdate(this.listeners);
};

SkrivestotteStore.prototype.leggTilKnagg = function (knagg) {
    this.state.knagger = this.state.knagger || [];
    this.state.knagger.push(knagg);

    hentSokeresultater.bind(this)(this.state.fritekst, this.state.knagger);

    this.fireUpdate(this.listeners);
};

SkrivestotteStore.prototype.slettKnagg = function (knagg) {
    var nyeKnagger = this.state.knagger.slice(0);
    var index = nyeKnagger.indexOf(knagg);
    this.state.knagger.splice(index, 1);

    hentSokeresultater.bind(this)(this.state.fritekst, this.state.knagger);

    this.fireUpdate(this.listeners);
};

SkrivestotteStore.prototype.setLocale = function(locale){
    this.state.valgtLocale = locale;
    this.fireUpdate(this.listeners);
};

SkrivestotteStore.prototype.onChange = function (data) {
    this.state.fritekst = data.fritekst;
    this.state.knagger = data.knagger;

    hentSokeresultater.bind(this)(this.state.fritekst, this.state.knagger);

    this.fireUpdate(this.listeners);
};

SkrivestotteStore.prototype.onKeyDown = function (event) {
    switch (event.keyCode) {
        case 38: /* pil opp */
            event.preventDefault();
            this.state.valgtTekst = hentTekst(forrigeTekst, this.state.tekster, this.state.valgtTekst);
            this.fireUpdate(this.listeners);
            break;
        case 40: /* pil ned */
            event.preventDefault();
            this.state.valgtTekst = hentTekst(nesteTekst, this.state.tekster, this.state.valgtTekst);
            this.fireUpdate(this.listeners);
            break;
    }
};

SkrivestotteStore.prototype.submit = function(onSubmit, event){
    event.preventDefault();
    var $tekstfelt = $('#' + this.state.tekstfeltId);
    var eksisterendeTekst = $tekstfelt.focus().val();
    eksisterendeTekst += eksisterendeTekst.length === 0 ? "" : "\n";

    $tekstfelt
        .focus()
        .val(eksisterendeTekst + autofullfor(stripEmTags(Utils.getInnhold(this.state.valgtTekst, this.state.valgtLocale)), this.state.autofullfor))
        .trigger('input');

    onSubmit();
};

function hentTekst(hentElement, elementer, valgtElement) {
    for (var i = 0; i < elementer.length; i++) {
        if (elementer[i].key === valgtElement.key) {
            return hentElement(elementer, i);
        }
    }
}

function forrigeTekst(elementer, index) {
    return index === 0 ? elementer[0] : elementer[index - 1];
}

function nesteTekst(elementer, index) {
    return index === elementer.length - 1 ? elementer[elementer.length - 1] : elementer[index + 1];
}

var hentSokeresultater =
    Utils.debounce(function (fritekst, knagger) {
        sok(fritekst, knagger).done(function (resultat) {
            this.state.tekster = resultat;
            this.state.valgtTekst = resultat[0] || {};
            this.fireUpdate(this.listeners);
        }.bind(this))
    }, 150);

var sok = function (fritekst, knagger) {
    fritekst = fritekst || '';
    knagger = knagger || [];

    fritekst = fritekst.replace(/^#*(.*)$/, '$1');

    var url = '/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + encodeURIComponent(fritekst);
    if (knagger.length !== 0) {
        url += '&tags=' + encodeURIComponent(knagger);
    }

    return $.get(url);
};

function stripEmTags(tekst) {
    return tekst.replace(/<em>(.*?)<\/em>/g, '$1')
}

function autofullfor(tekst, autofullforMap) {
    var nokler = {
        'bruker.fnr': autofullforMap.bruker.fnr,
        'bruker.fornavn': autofullforMap.bruker.fornavn,
        'bruker.etternavn': autofullforMap.bruker.etternavn,
        'bruker.navn': autofullforMap.bruker.navn,
        'bruker.navkontor': autofullforMap.bruker.navkontor,
        'saksbehandler.fornavn': autofullforMap.saksbehandler.fornavn,
        'saksbehandler.etternavn': autofullforMap.saksbehandler.etternavn,
        'saksbehandler.navn': autofullforMap.saksbehandler.navn,
        'saksbehandler.enhet': autofullforMap.saksbehandler.enhet
    };

    return tekst.replace(/\[(.*?)]/g, function (tekst, resultat) {
        return nokler[resultat] || '[ukjent nøkkel]';
    });
}

module.exports = SkrivestotteStore;