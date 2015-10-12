import Utils from './../utils/utils-module';
import Store from './../utils/store';
import WicketSender from './../react-wicket-mixin/wicket-sender';


class MeldingerSokStore extends Store {
    constructor(...args) {
        super(args[0]);
        if (this.state.traader.length > 0) {
            this.state.valgtTraad = this.state.traader[0];
        }
        this.state.initialisert = false;
        this.state.feilet = false;
        this.sendToWicket = WicketSender.bind(this, this.state.wicketurl, this.state.wicketcomponent);
    }

    update(props) {
        Object.assign(this, props);

        this.onChange({target: {value: this.state.fritekst}});

        this.fireUpdate(this.listeners);
    }

    onChange(event) {
        this.state.fritekst = event.target.value;

        hentSokeresultater.bind(this)(this.state.fritekst);

        this.fireUpdate(this.listeners);
    }

    traadChanged(traad, tabliste) {
        this.state.valgtTraad = traad;

        updateScroll(tabliste, this.state.traader.indexOf(this.state.valgtTraad));

        this.fireUpdate(this.listeners);

    }

    onKeyDown(tabliste, event) {
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
        }
    }

    oppdaterTraadRefs(traadMarkupIds) {
        this.state.traadMarkupIds = traadMarkupIds;
    }

    submit(afterSubmit, event) {
        event.preventDefault();
        $('#' + this.state.traadMarkupIds[this.state.valgtTraad.traadId]).click();
        afterSubmit();
    }
}

function nesteMelding(elementer, index) {
    return index === elementer.length - 1 ? elementer[elementer.length - 1] : elementer[index + 1];
}

function updateScroll(tabliste, valgtIndex) {
    var $parent = $(tabliste);
    var $valgt = $parent.find('.sok-element').eq(valgtIndex);

    Utils.adjustScroll($parent, $valgt);
}

var sok = (fnr, query) => {
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

var hentSokeresultater =
    Utils.debounce(function (fritekst) {
        sok(this.state.fnr, fritekst)
            .done(function (traader) {
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
            .fail(function (jqXHR) {
                if (jqXHR.status === 403) {
                    this.sendToWicket('reindekser');
                } else {
                    this.state.feilet = true;
                    this.fireUpdate(this.listeners);
                }
            }.bind(this));
    }, 150);

export default MeldingerSokStore;