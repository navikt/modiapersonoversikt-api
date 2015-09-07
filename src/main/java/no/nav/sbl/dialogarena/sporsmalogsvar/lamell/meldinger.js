var Meldinger = (function () {

    var focusOnSelectedElement = function () {
        $('.meldingsforhandsvisning.valgt input[type=radio]').focus();
    };

    return {
        focusOnSelectedElement: focusOnSelectedElement
    };
})();