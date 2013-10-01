$(document).ready(function() {

    var distanseFraToppen = 0;

    var attachHenvendelseListener = function() {
        $(document).on('click', '.melding', function() {
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

    var attachTilInnboksListener = function() {
        $('.tilbake-til-innboks-link').on('click', function() {
            attachAjaxCompleteListener();
        });
    };

    var attachToggleHoydeListener = function() {
        var $tidligereHenvendelseTekst = $('.tidligere-henvendelse p');
        var minHoyde = parseInt($tidligereHenvendelseTekst.css('line-height')) * 2;

        $tidligereHenvendelseTekst.each(function() {
            $(this).data('height', $(this).height());
        });

        $tidligereHenvendelseTekst.height(minHoyde);

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
        attachTilInnboksListener();
        attachToggleHoydeListener();
    };

    var adjustInnboksHeight = function() {
        var bodyHeight = $('body').outerHeight();
        var restHeight = $('.footer').outerHeight() + $('.innstillinger-innlogget').outerHeight() +
            $('.rad-logo').outerHeight() + $('#innboks-top').outerHeight();
        $('#innboks-container').height(bodyHeight - restHeight);
    };

    attachListeners();
    adjustInnboksHeight();
});
