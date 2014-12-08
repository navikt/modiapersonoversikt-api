var Meldinger = (function () {
    var PIL_NED = 40;
    var PIL_OPP = 38;
    var TAB = 9;

    var addKeyNavigation = function () {
        $('.meldinglamell').on('keydown', '.melding', function (e) {
            var eventHandled = true;
            if (e.keyCode === PIL_OPP) {
                $(e.currentTarget).prev().click();
            } else if (e.keyCode === PIL_NED) {
                $(e.currentTarget).next().click();
            } else if (e.keyCode === TAB && e.shiftKey) {
                $('.meldinger .lamellhode ~ .close').focus();
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
        setTimeout(function(){$('.melding.valgt').focus()},0);
    };

    return {
        addKeyNavigation: addKeyNavigation,
        focusOnSelectedElement: focusOnSelectedElement
    };
})();