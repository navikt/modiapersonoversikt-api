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

    const connection = new WebSocket('wss://veilederflatehendelser' + finnMiljoStreng() + '.adeo.no/modiaeventdistribution/websocket');
    connection.onmessage = callback;

    connection.onerror = function onerror(error) {
        console.error(error);
    };

    connection.onclose = function onclose() {
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
function toJson(resp) {
    return resp.json();
}

function nullstillContext() {
    return fetch('https://modapp' + finnMiljoStreng() + '.adeo.no/modiacontextholder/api/context/nullstill', {
        credentials: 'same-origin',
        method: 'DELETE'
    });
}

function hentContextBruker() {
    return fetch('https://modapp' + finnMiljoStreng() + '.adeo.no/modiacontextholder/api/context/aktivbruker', { credentials: 'same-origin' })
        .then(fetchOkStatus)
        .then(toJson);
}

function hentContextEnhet() {
    return fetch('https://modapp' + finnMiljoStreng() + '.adeo.no/modiacontextholder/api/context/aktivenhet', { credentials: 'same-origin' })
        .then(fetchOkStatus)
        .then(toJson);
}

function oppdaterContextBruker(fnr) {
    if (!fnr || fnr.lengt === 0) {
        return nullstillContext();
    } else {
        return fetch('https://modapp' + finnMiljoStreng() + '.adeo.no/modiacontextholder/api/context', {
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
    return fetch('https://modapp' + finnMiljoStreng() + '.adeo.no/modiacontextholder/api/context', {
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

function lastInnBruker(fnr) {
    window.location = 'https://modapp' + finnMiljoStreng() + '.adeo.no/modiabrukerdialog/hentPerson/' + fnr;
}

// Wicket integration
function JSWicket(url, component) {
    this.url = url;
    this.component = component;
}

JSWicket.prototype.send = function send(action, data) {
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

function sokOppFnr(sendToWicket) {
    return function(event) {
        const fnr = event.fodselsnummer;
        sendToWicket && sendToWicket.send('sokperson', fnr);
    }
}
function resetBruker(sendToWicket) {
    return function() {
        sendToWicket && sendToWicket.send('fjernperson');
    }
}
function byttEnhet(enhet) {
    console.log('byttet enhet', enhet);
}

function lagDecoratorConfig(fnr, autoSubmit, feilmelding) {
    const config = {
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

    if (fnr && fnr.length > 0) {
        config.config.fnr = fnr;
        config.config.autoSubmit = autoSubmit;
    }
    if (feilmelding && feilmelding.length > 0) {
        config.config.feilmelding = feilmelding;
    }

    config.config.handleChangeEnhet = oppdatertValgtEnhet;

    return config;
}
const websocketCallbackMap = {
    'NY_AKTIV_BRUKER': function(fnr, redirectModal) {
        hentContextBruker()
            .then(function(data) {
                if (data.aktivBruker === fnr) {
                    return;
                }
                const endreCallback = function() {
                    lastInnBruker(data.aktivBruker);
                };
                const beholdCallback = function() {
                    redirectModal.skjul();
                    oppdaterContextBruker(fnr);
                };

                redirectModal.vis(data.aktivBruker, endreCallback, beholdCallback);
            });
    },
    'NY_AKTIV_ENHET': function(fnr, redirectModal) {
        hentContextEnhet()
            .then(function(data) { console.log('data', data); });
    },
};

// Hode init
(function () {
    let decoratorConfig = null; // Må legges hit for å kunne deles mellom 'initHode' og 'update'

    window.initHode = function initHode(callbackUrl, markupId, fnr, autoSubmit, feilmelding) {
        // Setter opp wicket-callback-handler
        const sendToWicket = new JSWicket(callbackUrl, markupId);
        document.addEventListener('dekorator-hode-personsok', sokOppFnr(sendToWicket));
        document.addEventListener('dekorator-hode-fjernperson', resetBruker(sendToWicket));

        // Lager decorator og redirect modal
        decoratorConfig = lagDecoratorConfig(fnr, autoSubmit, feilmelding);
        const redirectModal = lagRedirectModal();
        console.log('config', decoratorConfig);
        window.renderDecoratorHead(decoratorConfig, markupId);

        // Laster inn/Oppdater bruker ved sidelast
        hentContextBruker().then(function(data) {
            console.log('brukerIContext', data);
            if (data.aktivBruker !== fnr) {
                if (fnr && fnr.length > 0) {
                    oppdaterContextBruker(fnr);
                } else {
                    lastInnBruker(data.aktivBruker);
                }
            }
        });


        // Setter opp context-lyttere
        opprettWebSocket(function(event) {
            const type = event.data;
            const handler = websocketCallbackMap[type] || (function() { console.warn('Ukjent event fra contextholder', event); });

            handler(fnr, redirectModal);
        });
    };

    window.updateHode = function (markupId, feilmelding) {
        let config = decoratorConfig;
        if (feilmelding && feilmelding.length > 0) {
            config.config.feilmelding = feilmelding;
        }
        console.log('update config', config, markupId);
        window.renderDecoratorHead(config, markupId);
    };
})();