var Meldinger = (function() {

    var addKeyNavigation = function() {
        var $meldinglamell = $('.meldinglamell');

        $meldinglamell.on('keydown', '.melding', function(e) {
            if (e.keyCode === 38) {
                $(e.currentTarget).prev().focus();
            } else if (e.keyCode === 40) {
                $(e.currentTarget).next().focus();
            } else if (e.keyCode === 13) {
                $(e.currentTarget).click();
            } else if (e.keyCode === 9) {
                $('.haandter-meldinger a').first().focus();
            }
            e.preventDefault();
        });
    };

    var focusOnSelectedElement = function() {
        $('.melding.valgt').focus();
    };

    return {
        addKeyNavigation: addKeyNavigation,
        focusOnSelectedElement: focusOnSelectedElement
    };
})();