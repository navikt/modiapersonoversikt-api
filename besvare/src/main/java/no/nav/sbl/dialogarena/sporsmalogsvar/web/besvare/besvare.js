$(document).ready(function() {
    function attachToggleHoydeListener() {
        var $dialogInnholdTekst = $('.dialog-innhold p');
        var minHoyde = parseInt($dialogInnholdTekst.css('line-height')) * 2;

        $dialogInnholdTekst.each(function() {
            $(this).data('height', $(this).height());
        });

        $dialogInnholdTekst.height(minHoyde);

        $('.dialog-innhold').on('click', function() {
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

    function attachJusterTekstfeltListener() {
        $('#tekstfelt').on('keyup', function() {
            this.style.height = 'auto';
            $(this).height(this.scrollHeight);

            var $svarKnapp = $('.send-svar-knapp');
            this.value.length == 0 ? $svarKnapp.hide() : $svarKnapp.show();
        });
    }

    attachJusterTekstfeltListener();
    attachToggleHoydeListener();
});

