(function () {
    var SaksbehandlerInnstillinger = {};

    function focusValgEnhet() {
        var $valg = $('.saksbehandlerinnstillinger .enhetsvalg');
        $valg.find(':checked').focus();
    }

    SaksbehandlerInnstillinger.focus = focusValgEnhet;

    window.SaksbehandlerInnstillinger = SaksbehandlerInnstillinger;
})();

$(document).ready(function () {

    $(document).on('keydown', '.saksbehandlerinnstillinger-toggle', function (e) {
        if (e.keyCode == 13 || e.keyCode === 32) {
            $(e.currentTarget).click();
        }
    });
    $(document).on('keydown', '.saksbehandlerinnstillinger > .enhetsform > .enhetsvalg', function (e) {
        if (e.keyCode == 13) {
            $(e.currentTarget).closest('.enhetsform').find('a.send').click();
            $('#foedselsnummerInput').focus();
            e.preventDefault();
        }
    });

});