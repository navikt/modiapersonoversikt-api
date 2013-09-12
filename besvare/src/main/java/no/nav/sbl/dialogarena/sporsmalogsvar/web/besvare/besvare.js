$(document).ready(function() {
    var attachToggleHoydeListener = function() {
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

    var attachJusterTekstfeltListener = function() {
        var $tekstfelt = $('#tekstfelt');
        var orginalHoyde = $tekstfelt.height();
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
            attachListeners();
        });
    };

    var attachListeners = function() {
        attachJusterTekstfeltListener();
        attachToggleHoydeListener();
    };

    attachAjaxCompleteListener();
    attachListeners();
});
