$(document).ready(function () {
    (function (config) {
        var appKey = "";
        if (window.location.href.indexOf("modapp.adeo.no") > -1 || window.location.href.indexOf("app.adeo.no") > -1) {
            appKey = 'EUM-AAB-AUN';
        } else if (window.location.href.indexOf("modapp-q1.adeo.no") > -1 || window.location.href.indexOf("app-q1.adeo.no") > -1) {
            appKey = 'EUM-AAB-AUP';
        } else if (window.location.href.indexOf("modapp-q0.adeo.no") > -1 || window.location.href.indexOf("app-q0.adeo.no") > -1) {
            appKey = 'EUM-AAB-AUP';
        } else if (window.location.href.indexOf("modapp-t6.adeo.no") > -1 || window.location.href.indexOf("app-t6.adeo.no") > -1) {
            appKey = 'EUM-AAB-AUR';
        }
        config.appKey = appKey;
        config.adrumExtUrlHttp = 'http://jsagent.adeo.no';
        config.adrumExtUrlHttps = 'https://jsagent.adeo.no';
        config.beaconUrlHttp = 'http://eumgw.adeo.no';
        config.beaconUrlHttps = 'https://eumgw.adeo.no';
        config.xd = {enable: false};
        config.spa = {
            "spa2": true
        };
    })(window['adrum-config'] || (window['adrum-config'] = {}));
});
