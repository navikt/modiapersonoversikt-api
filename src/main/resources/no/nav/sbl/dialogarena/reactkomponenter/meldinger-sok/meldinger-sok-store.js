import Utils from './../utils/utils-module';
import Store from './../utils/store';
import WicketSender from './../react-wicket-mixin/wicket-sender';
import Ajax from './../utils/ajax';

class MeldingerSokStore extends Store {
    constructor(props) {
        super(props);
        if (this.state.traader.length > 0) {
            this.state.valgtTraad = this.state.traader[0];
        }

        this.state.initialisert = false;
        this.state.feilet = false;
        this.sendToWicket = WicketSender.bind(this, this.state.wicketurl, this.state.wicketcomponent);
    }

    initializeVisning() {
        return Ajax.get('/modiabrukerdialog/rest/meldinger/' + this.state.fnr + '/indekser').then(
            () => {
                this.update();
            },
            () => {
                this.state.feilet = true;
                this.update();
            });
    }

    update() {
        this.onChange({target: {value: this.state.fritekst}});

        this.fireUpdate(this.listeners);
    }

    onChange(event) {
        this.state.fritekst = event.target.value;

        MeldingerSokStore.hentSokeresultater.bind(this)(this.state.fritekst);

        this.fireUpdate(this.listeners);
    }

    traadChanged(traad) {
        const tabliste = document.getElementById(this.state.listePanelId);
        this.state.valgtTraad = traad;

        MeldingerSokStore._updateScroll(tabliste, this.state.traader.indexOf(this.state.valgtTraad));

        this.fireUpdate(this.listeners);
    }

    onKeyDown(tabliste, event) {
        switch (event.keyCode) {
            case 38:
                event.preventDefault();
                this.state.valgtTraad = MeldingerSokStore.hentMelding(MeldingerSokStore.forrigeMelding, this.state.traader, this.state.valgtTraad);

                MeldingerSokStore._updateScroll(tabliste, this.state.traader.indexOf(this.state.valgtTraad));

                this.fireUpdate(this.listeners);
                break;
            case 40:
                event.preventDefault();
                this.state.valgtTraad = MeldingerSokStore.hentMelding(MeldingerSokStore.nesteMelding, this.state.traader, this.state.valgtTraad);

                MeldingerSokStore._updateScroll(tabliste, this.state.traader.indexOf(this.state.valgtTraad));

                this.fireUpdate(this.listeners);
                break;
            default:
        }
    }

    oppdaterTraadRefs(traadMarkupIds) {
        this.state.traadMarkupIds = traadMarkupIds;
    }

    submit(afterSubmit, event) {
        event.preventDefault();
        document.getElementById(this.state.traadMarkupIds[this.state.valgtTraad.traadId]).click();
        afterSubmit();
    }

    static _updateScroll(tabliste, valgtIndex) {
        const element = tabliste.querySelectorAll('.sok-element').item(valgtIndex);
        Utils.adjustScroll(tabliste, element);
    }

    static _sok(fnr, query = '') {
        const processedQuery = query.replace(/\./g, '');
        const url = '/modiabrukerdialog/rest/meldinger/' + fnr + '/sok/' + encodeURIComponent(processedQuery);
        return Ajax.get(url);
    }

    static hentMelding(hentElement, elementer, valgtElement) {
        for (let i = 0; i < elementer.length; i++) {
            if (elementer[i].key === valgtElement.key) {
                return hentElement(elementer, i);
            }
        }
    }

    static nesteMelding(elementer, index) {
        return index === elementer.length - 1 ? elementer[elementer.length - 1] : elementer[index + 1];
    }

    static forrigeMelding(elementer, index) {
        return index === 0 ? elementer[0] : elementer[index - 1];
    }
}

function onFulfilled(traader) {
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
    MeldingerSokStore._updateScroll(this.container.querySelector('.sok-liste'), 0);
    this.fireUpdate(this.listeners);
}

function onRejected(error) {
    if (error[0].status === 403) {
        this.sendToWicket('oppdater');
        this.initializeVisning();
    } else {
        this.state.feilet = true;
        this.fireUpdate(this.listeners);
    }
}


MeldingerSokStore.hentSokeresultater =
    Utils.debounce(function doSok(fritekst) {
        MeldingerSokStore._sok(this.state.fnr, fritekst).done(onFulfilled.bind(this), onRejected.bind(this));
    }, 150);

export default MeldingerSokStore;
