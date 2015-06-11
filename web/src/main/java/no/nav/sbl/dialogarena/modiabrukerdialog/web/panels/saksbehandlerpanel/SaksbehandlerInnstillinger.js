(function () {
    var SaksbehandlerInnstillinger = {};

    function focusValgEnhet() {
        var $valg = $('.saksbehandlerinnstillinger .enhetsvalg');
        $valg.find(':checked').focus();
    }
    function delay(func, time) {
        return function(){
            setTimeout(function(){
                func();
            }, time);
        }
    }

    SaksbehandlerInnstillinger.focus = delay(focusValgEnhet, 0);

    window.SaksbehandlerInnstillinger = SaksbehandlerInnstillinger;
})();

$(document).ready(function () {
    var ENTER = 13;

    $(document).on('keydown', '.saksbehandlerinnstillinger > .enhetsform > .enhetsvalg', function (e) {
        if (e.keyCode == ENTER) {
            $(e.currentTarget).closest('.enhetsform').find('a.send').click();
            $('#foedselsnummerInput').focus();
            e.preventDefault();
        }
    });

});