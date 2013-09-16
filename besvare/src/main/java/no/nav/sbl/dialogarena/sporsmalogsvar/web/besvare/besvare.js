$(document).ready(function() {
    var attachToggleHoydeListener = function() {
        var $dialogInnholdTekst = $('.tidligere-dialog .dialog-innhold p');
        var minHoyde = parseInt($dialogInnholdTekst.css('line-height')) * 2;

        $dialogInnholdTekst.each(function() {
            if(!$(this).data('height') && $(this).height() >= minHoyde) {
                $(this).data('height', $(this).height());
            }
        });

        $dialogInnholdTekst.height(minHoyde);

        var $dialogInnhold = $('.tidligere-dialog .dialog-innhold');

        // I tilfelle AJAX events fra andre deler av Modia
        $dialogInnhold.off('click');

        $dialogInnhold.on('click', function() {
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

    var attachJusterTekstfeltListener = function() {
        var $tekstfelt = $('#tekstfelt');
        var orginalHoyde = $tekstfelt.height();

        // I tilfelle AJAX events fra andre deler av Modia
        $tekstfelt.off('keyup focusout');

        $tekstfelt.on('keyup', function() {
            this.style.height = 'auto';
            $(this).height(this.scrollHeight);
        });

        $tekstfelt.on('focusout', function() {
            if(this.value.length == 0) {
                $(this).height(orginalHoyde);
            }
        });
    };

    var attachAjaxCompleteListener = function() {
        $(document).on('ajaxComplete', function() {
            attachJusterTekstfeltListener();
            attachToggleHoydeListener();
        });
    };

    attachAjaxCompleteListener();
    attachJusterTekstfeltListener();
    attachToggleHoydeListener();
});
