$(document).ready(function() {

    var distanseFraToppen = 0;

    var attachAjaxCompleteListener = function() {
        $(document).one('ajaxComplete', function() {
            $('#meldinger').scrollTop(distanseFraToppen);
            attachListeners();
        });
    };

    var attachToggleHoydeListener = function() {
        var $tidligereMeldingTekst = $('.tidligere-melding p');
        var minHoyde = parseInt($tidligereMeldingTekst.css('line-height')) * 2;

        $tidligereMeldingTekst.each(function() {
            if (!$(this).data('height') && $(this).height() >= minHoyde) {
                $(this).data('height', $(this).height());
            }
        });

        $tidligereMeldingTekst.height(minHoyde);

        // I tilfelle AJAX events fra andre deler av Modia
        $('.tidligere-melding article').off('click');

        $('.tidligere-melding article').on('click', function() {
            var $tekstFelt = $(this).find('p');
            var animasjonsHastighet = 100;

            if($tekstFelt.height() == minHoyde) {
                $tekstFelt.animate({height: $tekstFelt.data('height')}, animasjonsHastighet);
            } else {
                $tekstFelt.animate({height: minHoyde}, animasjonsHastighet);
            }
            $(this).find('.utvide-tekst-pil').toggleClass('rotert');
        });
    };

    var attachListeners = function() {
        attachToggleHoydeListener();
        attachAjaxCompleteListener();
    };

    attachListeners();
});
