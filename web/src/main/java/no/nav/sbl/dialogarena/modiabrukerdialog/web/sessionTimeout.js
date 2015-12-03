jQuery(document).ready(function ($) {
    'use strict';

    var timeout;
    var interval;

    setSessionTimeoutBox();

    function setSessionTimeoutBox() {
        resetSessionTimeoutBox();

        dialogMedBrukerPing();

        Wicket.Event.subscribe('/ajax/call/after', function () {
            resetSessionTimeoutBox();
        });
    }

    function resetSessionTimeoutBox() {
        var timeoutValue = 1000 * 60 * 55; // Oppdatering her må korrespondere med session-timeout-verdien i web.xml.
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            createTimeoutBox();
        }, timeoutValue);
    }

    function dialogMedBrukerPing() {
        var intervalValue = 1000 * 60 * 10;
        var currentTextLength;
        interval = setInterval(function () {
            if (currentTextLength === undefined) {
                currentTextLength = getTextAreaLength();
            }
            var textLength = getTextAreaLength();
            if (textLength > -1 && currentTextLength != textLength) {
                currentTextLength = textLength;
                $.ajax("/modiabrukerdialog/internal/isAlive");
                resetSessionTimeoutBox();
            }
        }, intervalValue);
    }

    function getTextAreaLength() {
        var textArea = $('.sidebar-hoyre').find('.expandingtextarea');
        if (textArea.length > 0) {
            return textArea[0].value.length;
        } else {
            return -1;
        }
    }

    function createTimeoutBox() {
        if (!$('.wicket-mask-dark')[0]) {
            $('body').append($('<div/>').addClass('wicket-mask-dark'));
        }
        $('.informasjonsboks.timeout').show();
    }
});