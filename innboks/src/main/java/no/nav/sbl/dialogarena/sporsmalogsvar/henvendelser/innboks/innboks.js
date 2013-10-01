$(document).ready(function() {

    var distanseFraToppen = 0;

    var attachHenvendelseListener = function() {
        $('.melding').on('click', function() {
            distanseFraToppen = $('#meldinger').scrollTop();
            attachAjaxCompleteListener();
        });
    };

    var attachAjaxCompleteListener = function() {
        $(document).one('ajaxComplete', function() {
            $('#meldinger').scrollTop(distanseFraToppen);
            attachListeners();
        });
    };

    var attachToggleHoydeListener = function() {
        var $tidligereHenvendelseTekst = $('.tidligere-henvendelse p');
        var minHoyde = parseInt($tidligereHenvendelseTekst.css('line-height')) * 2;

        $tidligereHenvendelseTekst.each(function() {
            if (!$(this).data('height') && $(this).height() >= minHoyde) {
                $(this).data('height', $(this).height());
            }
        });

        $tidligereHenvendelseTekst.height(minHoyde);

        // I tilfelle AJAX events fra andre deler av Modia
        $('.tidligere-henvendelse article').off('click');

        $('.tidligere-henvendelse article').on('click', function() {
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
        attachHenvendelseListener();
        attachToggleHoydeListener();
        attachAjaxCompleteListener();
    };

    attachListeners();
});
