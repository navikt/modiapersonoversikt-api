// Websocket
function finnMiljoStreng() {
    const host = window.location.host;
    const bindestrekIndex = host.indexOf('-');
    if (bindestrekIndex === -1) {
        return '';
    }
    const dotIndex = host.indexOf('.');
    return host.substring(bindestrekIndex, dotIndex);
}

function opprettWebSocket(callback) {
    if (window.location.hostname.indexOf('modapp') === -1) {
        return;
    }

    const connection = new WebSocket(`wss://veilederflatehendelser${finnMiljoStreng()}.adeo.no/modiaeventdistribution/websocket`);
    connection.onmessage = callback;

    connection.onerror = function (error) {
        console.error(error);
    };

    connection.onclose = function () {
        setTimeout(function () {
            opprettWebSocket(callback);
        }, 1000);
    }
}

function fetchOkStatus(resp) {
    if (!resp.ok) {
        throw new Error(resp);
    }
    return resp;
}

function nullstillContext() {
    return fetch(`https://modapp${finnMiljoStreng()}.adeo.no/modiacontextholder/api/context/nullstill`, {
        credentials: 'same-origin',
        method: 'DELETE'
    });
}

function hentContextBruker() {
    return fetch(`https://modapp${finnMiljoStreng()}.adeo.no/modiacontextholder/api/context/aktivbruker`, { credentials: 'same-origin' })
        .then(fetchOkStatus)
        .then((resp) => resp.json());
}

function hentContextEnhet() {
    return fetch(`https://modapp${finnMiljoStreng()}.adeo.no/modiacontextholder/api/context/aktivenhet`, { credentials: 'same-origin' })
        .then(fetchOkStatus)
        .then((resp) => resp.json());
}

function oppdaterContextBruker(fnr) {
    if (!fnr || fnr.lengt === 0) {
        return nullstillContext();
    } else {
        return fetch(`https://modapp${finnMiljoStreng()}.adeo.no/modiacontextholder/api/context`, {
            credentials: 'same-origin',
            method: 'POST',
            body: JSON.stringify({ eventType: 'NY_AKTIV_BRUKER', verdi: fnr }),
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }
}

function oppdaterContextEnhet(enhet) {
    return fetch(`https://modapp${finnMiljoStreng()}.adeo.no/modiacontextholder/api/context`, {
        credentials: 'same-origin',
        method: 'POST',
        body: JSON.stringify({ eventType: 'NY_AKTIV_ENHET', verdi: enhet }),
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

function oppdatertValgtEnhet(valgtEnhet) {
    fetch('/modiabrukerdialog/rest/hode/velgenhet', {
        credentials: 'same-origin',
        method: 'POST',
        body: valgtEnhet,
        headers: {
            'Content-Type': 'application/json'
        }
    });
}

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

// Redirect modal
function lagRedirectModal(endreCallback, beholdCallback) {
    const modalComponent = ModiaJS.React.createElement(ModiaJS.Components.RedirectModal, {});
    return ModiaJS.ReactDOM.render(modalComponent, document.getElementById("feilmeldingsmodaler"));
}

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


    window.initHode = function (callbackUrl, markupId, fnr, autoSubmit, feilmelding) {
        const redirectModal = lagRedirectModal();
        let config = hodeConfig;
        if (fnr && fnr.length > 0) {
            config = JSON.parse(JSON.stringify(hodeConfig));
            config.config.fnr = fnr;
            config.config.autoSubmit = autoSubmit;
        }
        if (feilmelding && feilmelding.length > 0) {
            config.config.feilmelding = feilmelding;
        }

        config.config.handleChangeEnhet = oppdatertValgtEnhet;

        console.log('config', hodeConfig, config, markupId);
        sendToWicket = new JSWicket(callbackUrl, markupId);
        window.renderDecoratorHead(config, markupId);

        hentContextBruker().then((data) => {
            if (data.aktivBruker !== fnr) {
                oppdaterContextBruker(fnr);
            }
        });

        opprettWebSocket((event) => {
            const type = event.data;
            if (type === 'NY_AKTIV_BRUKER') {
                hentContextBruker()
                    .then((data) => {
                        if (data.aktivBruker === fnr) {
                            return;
                        }
                        const endreCallback = function() {
                            window.location = `https://modapp${finnMiljoStreng()}.adeo.no/modiabrukerdialog/hentPerson/${data.aktivBruker}`;
                        };
                        const beholdCallback = function() {
                            redirectModal.skjul();
                            oppdaterContextBruker(fnr);
                        };

                        redirectModal.vis(data.aktivBruker, endreCallback, beholdCallback);
                    });
            } else if (type === 'NY_AKTIV_ENHET') {
                hentContextEnhet()
                    .then((data) => console.log('data', data));
            } else {
                console.warn('Ukjent event fra contextholder', event);
            }
        });
    };

    window.updateHode = function (markupId, feilmelding) {
        let config = hodeConfig;
        if (feilmelding && feilmelding.length > 0) {
            config.config.feilmelding = feilmelding;
        }
        console.log('update config', hodeConfig, config, markupId);
        window.renderDecoratorHead(config, markupId);
    };
})();