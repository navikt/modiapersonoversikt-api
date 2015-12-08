var Meldinger = (function () {

    var focusOnSelectedElement = function () {
        $('.meldingsforhandsvisning.valgt input[type=radio]').focus();
    };

    var init = function () {
        //Bruk delegate-pattern for å unngå å måtte lage listeners hver gang noe oppdaterer seg i listen.
        $('#lameller').on('focus', '.meldingsforhandsvisning input', function (e) {
            $(e.target).closest('.meldingsforhandsvisning').addClass('is-focused');
        }).on('blur', '.meldingsforhandsvisning input', function (e) {
            $(e.target).closest('.meldingsforhandsvisning').removeClass('is-focused');
        })
    };

    return {
        init: init,
        focusOnSelectedElement: focusOnSelectedElement
    };
})();