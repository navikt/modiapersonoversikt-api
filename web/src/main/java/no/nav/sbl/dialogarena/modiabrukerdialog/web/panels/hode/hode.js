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
function getMe() {
    return fetch('/modiabrukerdialog/rest/hode/me', { credentials: 'same-origin'})
        .then(fetchOkStatus)
        .then(toJson);
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
    if (!fnr || fnr.length === 0) {
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
    oppdaterContextEnhet(valgtEnhet);
}

function lastInnBruker(fnr) {
    window.location = 'https://modapp' + finnMiljoStreng() + '.adeo.no/modiabrukerdialog/hentPerson/' + fnr;
}

function getCookie(name) {
    var value = ";" + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length === 2) {
        return parts.pop().split(";").shift();
    }
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
        nullstillContext()
            .then(function(){
                sendToWicket && sendToWicket.send('fjernperson');
            });
    }
}

function lagDecoratorConfig(fnr, initiellEnhet, autoSubmit, feilmelding) {
    const config = {
        config: {
            dataSources: {
                veileder: '/modiabrukerdialog/rest/hode/me',
                enheter: '/modiabrukerdialog/rest/hode/enheter'
            },
            toggles: { visEnhet: false, visEnhetVelger: true, visSokefelt: true, visVeileder: true },
            applicationName: 'Modia personoversikt',
            initiellEnhet: initiellEnhet,
            handleChangeEnhet: oppdatertValgtEnhet,
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

    return config;
}
const websocketCallbackMap = {
    'NY_AKTIV_BRUKER': function(markupId, fnr, redirectModal) {
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
    'NY_AKTIV_ENHET': function(markupId, fnr) {
        hentContextEnhet()
            .then(function(data) {
                window.rerenderHode(markupId, lagDecoratorConfig(fnr, data.aktivEnhet, false, ''));
            });
    },
};

// Hode init
(function () {
    let decoratorConfig = null; // Må legges hit for å kunne deles mellom 'initHode' og 'update'
    let redirectModal = null;

    window.initHode = function initHode(callbackUrl, markupId, fnr, autoSubmit, feilmelding) {
        // Setter opp wicket-callback-handler
        const sendToWicket = new JSWicket(callbackUrl, markupId);
        document.addEventListener('dekorator-hode-personsok', sokOppFnr(sendToWicket));
        document.addEventListener('dekorator-hode-fjernperson', resetBruker(sendToWicket));

        // Ikke bruk contextholder lokalt
        if (window.location.hostname.indexOf('modapp') >= 0) {
            // Laster inn/Oppdater bruker ved sidelast
            Promise.all([hentContextBruker(), hentContextEnhet(), getMe()])
                .then(function(data) {
                    var brukerData = data[0];
                    var enhetData = data[1];
                    var me = data[2];

                    console.log('brukerIContext', brukerData, enhetData, me);
                    if (brukerData.aktivBruker !== fnr) {
                        if (fnr && fnr.length > 0) {
                            oppdaterContextBruker(fnr);
                        } else if (brukerData.aktivBruker) {
                            lastInnBruker(brukerData.aktivBruker);
                        }
                    }

                    const valgtEnhet = enhetData.aktivEnhet || getCookie('saksbehandlerinnstillinger-'+me.ident);
                    oppdatertValgtEnhet(valgtEnhet);

                    // Lager decorator og redirect modal
                    decoratorConfig = lagDecoratorConfig(fnr, valgtEnhet, autoSubmit, feilmelding);
                    redirectModal = lagRedirectModal();
                    console.log('config', decoratorConfig);
                    window.renderDecoratorHead(decoratorConfig, markupId);
                });


            // Setter opp context-lyttere
            opprettWebSocket(function(event) {
                const type = event.data;
                const handler = websocketCallbackMap[type] || (function() { console.warn('Ukjent event fra contextholder', event); });

                handler(markupId, fnr, redirectModal);
            });
        }
    };
    window.rerenderHode = function(markupId, config) {
        console.log('update config', config, markupId);
        window.renderDecoratorHead(config, markupId);
    };
    window.updateHode = function (markupId, feilmelding) {
        let config = decoratorConfig;
        if (feilmelding && feilmelding.length > 0) {
            config.config.feilmelding = feilmelding;
        }
        window.rerenderHode(markupId, config);
    };
})();