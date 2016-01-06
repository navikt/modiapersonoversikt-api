var Meldinger = (function () {

    var focusOnSelectedElement = function () {
        $('.meldingsforhandsvisning.valgt input[type=radio]').focus();
    };

    var scrollToValgtMelding = function () {
        var $element = $('.traad-liste-visning .meldingsforhandsvisning.valgt');
        var $parent = $element.closest('.traad-liste-visning');

        var elementTop = $element.position().top;
        var elementBottom = elementTop + $element.outerHeight();

        if (elementTop < 0) {
            $parent.scrollTop($parent.scrollTop() + elementTop);
        } else if (elementBottom > $parent.outerHeight()) {
            $parent.scrollTop($parent.scrollTop() + (elementBottom - $parent.outerHeight()));
        }
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
        focusOnSelectedElement: focusOnSelectedElement,
        scrollToValgtMelding: scrollToValgtMelding
    };
})();