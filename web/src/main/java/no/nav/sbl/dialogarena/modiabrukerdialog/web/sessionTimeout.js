    var timeout;
    var interval;

    function setSessionTimeoutBox(timeoutBox) {
        resetSessionTimeoutBox(timeoutBox);

        dialogMedBrukerPing(timeoutBox);

        Wicket.Event.subscribe('/ajax/call/after', function () {
            resetSessionTimeoutBox(timeoutBox);
        });
    }

    function resetSessionTimeoutBox(timeoutBox) {
        var timeoutValue = 1000 * 60 * 55; // Oppdatering her må korrespondere med session-timeout-verdien i web.xml.
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            timeoutBox.vis();
        }, timeoutValue);
    }

    function dialogMedBrukerPing(timeoutBox) {
        var intervalValue = 1000 * 60 * 5;
        var currentTextLength;
        interval = setInterval(function () {
            if (currentTextLength === undefined) {
                currentTextLength = getTextAreaLength();
            }
            var textLength = getTextAreaLength();
            if (textLength > -1 && currentTextLength != textLength) {
                currentTextLength = textLength;
                $.ajax("/modiabrukerdialog/internal/isAlive");
                resetSessionTimeoutBox(timeoutBox);
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
