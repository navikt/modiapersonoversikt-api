import Utils from './../utils/utils-module';
import Store from './../utils/store';
import Ajax from './../utils/ajax';

class SkrivstotteStore extends Store {
    constructor(props) {
        super(props);
        if (this.state.tekster.length > 0) {
            this.state.valgtTekst = this.state.tekster[0];
        }
    }

    tekstChanged(tekst) {
        const tabliste = document.getElementById(this.state.listePanelId);
        this.state.valgtTekst = tekst;

        if (!this.state.svaksynt) {
            SkrivstotteStore._updateScroll(tabliste, this.state.tekster.indexOf(this.state.valgtTekst));
        }
        this.fireUpdate(this.listeners);
    }

    leggTilKnagg(knagg) {
        this.state.knagger = this.state.knagger || [];
        this.state.knagger.push(knagg);

        if (!this.state.svaksynt) {
            SkrivstotteStore.hentSokeresultater.bind(this)(this.state.fritekst, this.state.knagger);
        }
        this.fireUpdate(this.listeners);
    }

    slettKnagg(knagg) {
        const nyeKnagger = this.state.knagger.slice(0);
        const index = nyeKnagger.indexOf(knagg);
        this.state.knagger.splice(index, 1);
        if (!this.state.svaksynt) {
            SkrivstotteStore.hentSokeresultater.bind(this)(this.state.fritekst, this.state.knagger);
        }
        this.fireUpdate(this.listeners);
    }

    setLocale(locale) {
        this.state.valgtLocale = locale;
        this.fireUpdate(this.listeners);
    }

    onChange(data) {
        this.state.fritekst = data.fritekst;
        this.state.knagger = data.knagger;
        if (!this.state.svaksynt) {
            SkrivstotteStore.hentSokeresultater.bind(this)(this.state.fritekst, this.state.knagger);
        }
        this.fireUpdate(this.listeners);
    }

    onKeyDown(tabliste, event) {
        switch (event.keyCode) {
            case 16:
                if (this.state.svaksynt) {
                    event.preventDefault();
                    SkrivstotteStore.hentSokeresultater.bind(this)(this.state.fritekst, this.state.knagger);

                    this.fireUpdate(this.listeners);
                }
                break;
            case 38:
                event.preventDefault();
                this.state.valgtTekst = SkrivstotteStore.hentTekst(
                    SkrivstotteStore.forrigeTekst,
                    this.state.tekster,
                    this.state.valgtTekst
                );

                SkrivstotteStore._updateScroll(tabliste, this.state.tekster.indexOf(this.state.valgtTekst));

                this.fireUpdate(this.listeners);
                break;
            case 40:
                event.preventDefault();
                this.state.valgtTekst = SkrivstotteStore.hentTekst(
                    SkrivstotteStore.nesteTekst,
                    this.state.tekster,
                    this.state.valgtTekst
                );

                SkrivstotteStore._updateScroll(tabliste, this.state.tekster.indexOf(this.state.valgtTekst));

                this.fireUpdate(this.listeners);
                break;
            default:
        }
    }

    submit(onSubmit, event) {
        event.preventDefault();
        const tekstfelt = document.getElementById(this.state.tekstfeltId);
        tekstfelt.focus();

        // Må ha en timeout for å få fokus til å fjerne placeholder-tekst i IE
        setTimeout(() => {
            let eksisterendeTekst = typeof tekstfelt.value === 'undefined' ? '' : tekstfelt.value;
            eksisterendeTekst += eksisterendeTekst.length === 0 ? '' : '\n';
            tekstfelt.focus();
            tekstfelt.value = eksisterendeTekst + SkrivstotteStore.autofullfor(
                    SkrivstotteStore.stripEmTags(
                        Utils.getInnhold(this.state.valgtTekst, this.state.valgtLocale)
                    ), this.state.autofullfor);
            const thisEvent = document.createEvent('Event');
            thisEvent.initEvent('input', true, true);
            thisEvent.simulated = true;
            tekstfelt.dispatchEvent(thisEvent);

            onSubmit();
        }, 0);
    }

    static _updateScroll(tabliste, valgtIndex) {
        const element = tabliste.getElementsByClassName('sok-element').item(valgtIndex);
        Utils.adjustScroll(tabliste, element);
    }

    static _sok(fritekst = '', knagger = []) {
        const processedFritekst = fritekst.replace(/^#*(.*)$/, '$1');

        let url = '/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + encodeURIComponent(processedFritekst);
        if (knagger.length !== 0) {
            url += '&tags=' + encodeURIComponent(knagger);
        }
        return Ajax.get(url);
    }

    static hentTekst(hentElement, elementer, valgtElement) {
        for (let i = 0; i < elementer.length; i++) {
            if (elementer[i].key === valgtElement.key) {
                return hentElement(elementer, i);
            }
        }
        return undefined;
    }

    static forrigeTekst(elementer, index) {
        return index === 0 ? elementer[0] : elementer[index - 1];
    }

    static nesteTekst(elementer, index) {
        return index === elementer.length - 1 ? elementer[elementer.length - 1] : elementer[index + 1];
    }

    static stripEmTags(tekst) {
        return tekst.replace(/<em>(.*?)<\/em>/g, '$1');
    }

    static autofullfor(tekst, autofullforMap) {
        const nokler = {
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

        return tekst.replace(/\[(.*?)]/g, (res, resultat) => {
            const verdi = nokler[resultat];
            if (typeof verdi === 'undefined') {
                return '[ukjent nøkkel]';
            }
            return nokler[resultat] || '[fant ingen verdi]';
        });
    }
}

SkrivstotteStore.hentSokeresultater =
    Utils.debounce(function debouncedSok(fritekst, knagger) {
        SkrivstotteStore._sok(fritekst, knagger).done((resultat) => {
            this.state.tekster = resultat;
            this.state.valgtTekst = resultat[0] || {};
            SkrivstotteStore._updateScroll(this.container.querySelector('.sok-liste'), 0);
            this.fireUpdate(this.listeners);
        });
    }, 150);

export default SkrivstotteStore;
