$(document).ready(function() {

    // Shortcuts
    $(document).on('keydown', function (e) {
        console.log(e.keyCode);
        // Fokuser tekstfeltet: alt + F1 (keyCode 112)
        if (e.altKey && e.keyCode == 112) {
            $('#tekstfelt').focus();
        }
        // Send svar: shift + enter (keyCode 13)
        else if (e.shiftKey && e.keyCode == 13) {
            $('.send-svar-knapp').click();
        }
        // Unfocus tekstfeltet: esc (keyCode 27)
        else if (e.keyCode == 27) {
            $('#tekstfelt').blur();
        }
    });

    function attachToggleHoydeListener() {
        var $dialogInnholdTekst = $('.tidligere-dialog .dialog-innhold p');
        var minHoyde = parseInt($dialogInnholdTekst.css('line-height')) * 2;

        $dialogInnholdTekst.each(function() {
            if (!$(this).data('height') && $(this).height() >= minHoyde) {
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

            if ($tekstFelt.height() == minHoyde) {
                $tekstFelt.animate({height: $tekstFelt.data('height')}, animasjonsHastighet);
            } else {
                $tekstFelt.animate({height: minHoyde}, animasjonsHastighet);
            }
            $(this).find('.utvide-tekst-pil').toggleClass('rotert');
        });
    }

    function attachSvarFormListener() {
        var $tekstfelt = $('#tekstfelt');
        var $sendSvarKnapp = $('.send-svar-knapp');

        var orginalHoyde = $tekstfelt.height();
        var utvidetHoyde = $tekstfelt.height() * 8;
        var padding = $tekstfelt.innerHeight() - $tekstfelt.height();

        // I tilfelle AJAX events fra andre deler av Modia
        $tekstfelt.off('input focusin focusout');

        $tekstfelt.on('input', function() {
            if (this.scrollHeight - padding > utvidetHoyde) {
                this.style.height = 'auto';
                $(this).height(this.scrollHeight);

                if (this.scrollHeight <= utvidetHoyde) {
                    $(this).height(utvidetHoyde);
                }
            }
        });

        $tekstfelt.on('focusin', function () {
            if ($(this).height() < utvidetHoyde) {
                $(this).height(utvidetHoyde);
            }
            $sendSvarKnapp.show();
        });

        $tekstfelt.on('focusout', function() {
            if (this.value.length == 0) {
                $(this).height(orginalHoyde);
                $sendSvarKnapp.hide();
            }
        });
    }

    function attachAjaxCompleteListener() {
        $(document).on('ajaxComplete', function() {
            attachSvarFormListener();
            attachToggleHoydeListener();
        });
    }

    attachAjaxCompleteListener();
    attachSvarFormListener();
    attachToggleHoydeListener();
});
