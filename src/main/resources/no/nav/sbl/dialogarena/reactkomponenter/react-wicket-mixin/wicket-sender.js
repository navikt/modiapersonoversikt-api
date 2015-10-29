import Q from 'q';

const deferDict = {};
let setup = false;

function sendToWicket(url, component, action, data) {
    //Setting up wicket-event-listeners with the first invocation of method
    if (!setup) {
        Wicket.Event.subscribe(Wicket.Event.Topic.AJAX_CALL_SUCCESS, ok);
        Wicket.Event.subscribe(Wicket.Event.Topic.AJAX_CALL_FAILURE, failure);
        setup = true;
    }

    const deferKey = url;
    const defer = Q.defer();

    if (deferDict.hasOwnProperty(deferKey)) {
        throw "Duplicate ajaxrequest: " + deferKey;
    } else {
        deferDict[deferKey] = defer;
    }


    Wicket.Ajax.ajax({
        "u": url,
        "c": component,
        "ep": [
            {"name": action, "value": JSON.stringify(data)}
        ]
    });

    return defer.promise;
}
function ok(jqEvent, wicketConfig) {
    const deferKey = wicketConfig.u;
    const defer = deferDict[deferKey];

    if (defer){
        delete deferDict[deferKey];
        defer.resolve();
    }
}

function failure() {

    const deferKey = wicketConfig.u;
    const defer = deferDict[deferKey];

    if (defer) {
        delete deferDict[deferKey];
        defer.reject();
    }
}

export default sendToWicket;
