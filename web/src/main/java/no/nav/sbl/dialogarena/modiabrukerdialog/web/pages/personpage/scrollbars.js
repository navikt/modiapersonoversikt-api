window.Modig = window.Modig || {};

Modig.scrollOptions = {
    theme: '3d',
    scrollbarPosition: 'outside',
    alwaysShowScrollbar: 0,
    live: 'true',
    scrollInertia: 100,
    keyboard: false,
    advanced: {
        autoScrollOnFocus: "input,textarea,select,button,datalist,keygen,a[tabindex],area,object,[contenteditable='true'],article[tabindex], ul[tabindex], div[tabindex]"
    }
};

$.fn.getCursorPosition = function () {
    var el = $(this).get(0), pos = 0;
    if ("selectionStart" in el) {
        pos = el.selectionStart;
    } else if ("selection" in document) {
        el.focus();
        var sel = document.selection.createRange(), selLength = document.selection.createRange().text.length;
        sel.moveStart("character", -el.value.length);
        pos = sel.text.length - selLength;
    }
    return pos;
};

$.fn.setCursorAtStart = function () {
    if (document.selection) {
        var selection = document.selection.createRange();
        selection.moveStart('character', -this.get(0).value.length);
        selection.moveEnd('character', -this.get(0).value.length);
        selection.select();
    }
};

Modig.scrollHackForTextareas = function ($textareaWrapper, scrollOptions) {
    var $textarea = $textareaWrapper.find('.expandingtextarea');
    var $textareaClone = $textarea.siblings('.textareamirror');
    var textareaLineHeight = parseFloat($textarea.css('line-height')) || 16;
    var TAB = 9;


    $textarea.on('input keyup focus', function (e) {
        var $this = $(this);
        if (!$this.is(':focus') || (e.keyCode === TAB && !e.shiftKey))return;

        var textareaContent = $this.val();
        var textareaContentLength = textareaContent.length;
        var cursorPosition = $textarea.getCursorPosition();

        textareaContent = "<span>" + textareaContent.substr(0, cursorPosition) + "</span>" + textareaContent.substr(cursorPosition, textareaContent.length);
        textareaContent = textareaContent.replace(/\n/g, "<br />");

        $textareaClone.html(textareaContent + "<br />");
        var textareaCloneSpan = $textareaClone.children("span"), textareaCloneSpanOffset = 0,
            viewLimitBottom = (parseInt($textareaClone.css("min-height"))) - textareaCloneSpanOffset, viewLimitTop = textareaCloneSpanOffset,
            viewRatio = Math.round(textareaCloneSpan.height() + $textareaWrapper.find(".mCSB_container").position().top);
        if (viewRatio > viewLimitBottom || viewRatio < viewLimitTop) {
            if ((textareaCloneSpan.height() - textareaCloneSpanOffset) > 0) {
                $textareaWrapper.mCustomScrollbar("scrollTo", textareaCloneSpan.height() - textareaCloneSpanOffset - textareaLineHeight);
            } else {
                $textareaWrapper.mCustomScrollbar("scrollTo", "top");
            }
        }
    });


    scrollOptions.snapAmount = textareaLineHeight;
    $textareaWrapper.mCustomScrollbar(scrollOptions);
};

Modig.lagScrollbars = function () {
    $('.sidebar-venstre').mCustomScrollbar(Modig.scrollOptions);
    $('#personsok').mCustomScrollbar(Modig.scrollOptions);

    Modig.scrollHackForTextareas($('.sidebar-hoyre'), $.extend({}, Modig.scrollOptions, {
        advanced: {
            autoScrollOnFocus: "input,button"
        },
        keyboard: {enable: false}
    }));

    //Alle lerret med unntak av meldinger, saksoversikt, og utbetalinger.
    $('.lamell:not(.meldinger):not(.saksoversikt):not(.utbetalinger) .lerret').mCustomScrollbar(Modig.scrollOptions);

    //For meldinger lamell
    $('.lamell.meldinger .traad-liste-visning').mCustomScrollbar(Modig.scrollOptions);
    $('.lamell.meldinger .traadvisning').mCustomScrollbar($.extend({}, Modig.scrollOptions, {
        advanced: {
            autoScrollOnFocus: "input:not([type=submit]),select,button,a[role=button],datalist,keygen,a[tabindex],area,object,[contenteditable='true'],article[tabindex]"
        }
    }));

    //For saksoversikt lamell
    $('.lamell.saksoversikt .sak-navigering').mCustomScrollbar(Modig.scrollOptions);
    $('.lamell.saksoversikt .sak-informasjon').mCustomScrollbar(Modig.scrollOptions);
};