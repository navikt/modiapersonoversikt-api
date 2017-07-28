// Wicket integration
function JSWicket(url, component) {
    this.url = url;
    this.component = component;
}

JSWicket.prototype.send = function (action, data) {
    console.log('sending', this.url, this.component, action, data);
    Wicket.Ajax.ajax({
        'u': this.url,
        'c': this.component,
        'ep': [
            { 'name': action, 'value': JSON.stringify(data) }
        ]
    });
};

// Hode init
(function () {
    let sendToWicket = null;
    const hodeConfig = {
        config: {
            dataSources: {
                veileder: '/modiabrukerdialog/rest/hode/me',
                enheter: '/modiabrukerdialog/rest/hode/enheter'
            },
            toggles: { visEnhet: false, visEnhetVelger: true, visSokefelt: true, visVeileder: true },
            applicationName: 'Modiabrukerdialog',
            handleChangeEnhet: byttEnhet,
            extraMarkup: {
                etterSokefelt: '<button class="personsok-button" id="toggle-personsok" aria-label="Åpne avansert søk" title="Åpne avansert søk" data-apne="Åpne avansert søk" data-lukke="Lukk avansert søk"> <span> A <span class="personsok-pil"></span> </span> </button>'
            }
        }
    };

    function sokOppFnr(event) {
        const fnr = event.fodselsnummer;
        sendToWicket && sendToWicket.send('sokperson', fnr);
    }

    function resetBruker(event) {
        sendToWicket && sendToWicket.send('fjernperson');
    }

    function byttEnhet(enhet) {
        console.log('byttet enhet', enhet);
    }

    console.log('hode lastet....');
    document.addEventListener('dekorator-hode-personsok', sokOppFnr);
    document.addEventListener('dekorator-hode-fjernperson', resetBruker);


    window.initHode = function (callbackUrl, markupId, fnr, feilmelding) {
        let config = hodeConfig;
        if (fnr && fnr.length > 0) {
            config = JSON.parse(JSON.stringify(hodeConfig));
            config.config.fnr = fnr;
        }
        if (feilmelding && feilmelding.length > 0) {
            config.config.feilmelding = feilmelding;
        }
        console.log('config', hodeConfig, config, markupId);
        window.renderDecoratorHead(config, markupId);
        sendToWicket = new JSWicket(callbackUrl, markupId);
    };

    window.updateHode = function(markupId, feilmelding) {
        let config = hodeConfig;
        if (feilmelding && feilmelding.length > 0) {
            config.config.feilmelding = feilmelding;
        }
        console.log('update config', hodeConfig, config, markupId);
        window.renderDecoratorHead(config, markupId);
    }
})();
