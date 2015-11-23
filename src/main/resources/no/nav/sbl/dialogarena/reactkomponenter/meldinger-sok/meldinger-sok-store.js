import Utils from './../utils/utils-module';
import Store from './../utils/store';
import WicketSender from './../react-wicket-mixin/wicket-sender';

function updateScroll(tabliste, valgtIndex) {
    const $parent = $(tabliste);
    const $valgt = $parent.find('.sok-element').eq(valgtIndex);

    Utils.adjustScroll($parent, $valgt);
}

const sok = function sok(fnr, query = '') {
    const processedQuery = query.replace(/\./g, '');
    const url = '/modiabrukerdialog/rest/meldinger/' + fnr + '/sok/' + encodeURIComponent(processedQuery);
    return $.get(url);
};

const hentSokeresultater =
    Utils.debounce(function doSok(fritekst) {
        sok(this.state.fnr, fritekst)
            .done(function onDone(traader) {
                traader.forEach((traad) => {
                    traad.key = traad.traadId;
                    traad.datoInMillis = traad.dato.millis;
                    traad.innhold = traad.meldinger[0].fritekst;
                    traad.opprettetDato = traad.meldinger[0].opprettetDatoTekst;

                    traad.meldinger.forEach((melding) => {
                        melding.erInngaaende = ['SPORSMAL_SKRIFTLIG', 'SVAR_SBL_INNGAAENDE'].indexOf(melding.meldingstype) >= 0;
                        melding.fraBruker = melding.erInngaaende ? melding.fnrBruker : melding.navIdent;
                    });
                });
                this.state.traader = traader;
                this.state.valgtTraad = traader[0] || {};
                this.state.initialisert = true;
                updateScroll($(this.container).find('.sok-liste'), 0);
                this.fireUpdate(this.listeners);
            }.bind(this))
            .fail(function onFail(jqXHR) {
                if (jqXHR.status === 403) {
                    this.sendToWicket('reindekser');
                } else {
                    this.state.feilet = true;
                    this.fireUpdate(this.listeners);
                }
            }.bind(this));
    }, 150);

function MeldingerSokStore() {
    Store.apply(this, arguments);
    if (this.state.traader.length > 0) {
        this.state.valgtTraad = this.state.traader[0];
    }
    this.state.initialisert = false;
    this.state.feilet = false;
    this.sendToWicket = WicketSender.bind(this, this.state.wicketurl, this.state.wicketcomponent);
}
MeldingerSokStore.prototype = $.extend({}, Store.prototype, MeldingerSokStore.prototype);

MeldingerSokStore.prototype.onChange = function onChange(event) {
    this.state.fritekst = event.target.value;

    hentSokeresultater.bind(this)(this.state.fritekst);

    this.fireUpdate(this.listeners);
};

MeldingerSokStore.prototype.update = function update(props) {
    $.extend(this.state, props);
    $.ajax({
        async: false,
        url: '/modiabrukerdialog/rest/meldinger/' + this.state.fnr + '/indekser'
    });

    this.onChange({target: {value: this.state.fritekst}});

    this.fireUpdate(this.listeners);
};

MeldingerSokStore.prototype.traadChanged = function traadChanged(traad, tabliste) {
    this.state.valgtTraad = traad;

    updateScroll(tabliste, this.state.traader.indexOf(this.state.valgtTraad));

    this.fireUpdate(this.listeners);
};

function hentMelding(hentElement, elementer, valgtElement) {
    for (let i = 0; i < elementer.length; i++) {
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

MeldingerSokStore.prototype.onKeyDown = function onKeyDown(tabliste, event) {
    switch (event.keyCode) {
        case 38: /* pil opp */
            event.preventDefault();
            this.state.valgtTraad = hentMelding(forrigeMelding, this.state.traader, this.state.valgtTraad);

            updateScroll(tabliste, this.state.traader.indexOf(this.state.valgtTraad));

            this.fireUpdate(this.listeners);
            break;
        case 40: /* pil ned */
            event.preventDefault();
            this.state.valgtTraad = hentMelding(nesteMelding, this.state.traader, this.state.valgtTraad);

            updateScroll(tabliste, this.state.traader.indexOf(this.state.valgtTraad));

            this.fireUpdate(this.listeners);
            break;
        default:
    }
};

MeldingerSokStore.prototype.oppdaterTraadRefs = function oppdaterTraadRefs(traadMarkupIds) {
    this.state.traadMarkupIds = traadMarkupIds;
};

MeldingerSokStore.prototype.submit = function submit(afterSubmit, event) {
    event.preventDefault();
    $('#' + this.state.traadMarkupIds[this.state.valgtTraad.traadId]).click();
    afterSubmit();
};

module.exports = MeldingerSokStore;
