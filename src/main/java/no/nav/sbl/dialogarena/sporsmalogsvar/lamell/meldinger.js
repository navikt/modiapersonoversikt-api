var Meldinger = (function () {
    var PIL_NED = 40;
    var PIL_OPP = 38;
    var TAB = 9;

    var addKeyNavigation = function () {
        $('.meldinglamell').on('keydown', '.meldingsforhandsvisning', function (e) {
            var eventHandled = true;
            if (e.keyCode === PIL_OPP) {
                $(e.currentTarget).prev().click();
            } else if (e.keyCode === PIL_NED) {
                $(e.currentTarget).next().click();
            } else if (e.keyCode === TAB && e.shiftKey) {
                $('.meldinglamell .innboksSokToggle').focus();
            } else if (e.keyCode === TAB) {
                $('.haandter-meldinger a').first().focus();
            } else {
                eventHandled = false;
            }

            if (eventHandled) {
                e.preventDefault();
            }
        });
    };

    var focusOnSelectedElement = function () {
        setTimeout(function(){
            $('.melding.valgt').focus();
            $(document).scrollTop(0);
        },0);
    };

    return {
        addKeyNavigation: addKeyNavigation,
        focusOnSelectedElement: focusOnSelectedElement
    };
})();