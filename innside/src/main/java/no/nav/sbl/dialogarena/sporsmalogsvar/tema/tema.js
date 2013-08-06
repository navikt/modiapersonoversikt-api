const TAB = 9;
const ENTER = 13;
const ESC = 27;
const LEFT_ARROW = 37;
const UP_ARROW = 38;
const RIGHT_ARROW = 39;
const DOWN_ARROW = 40;

function temavelger() {
    var $alleTema = $('li.temastruktur, li.tema');
    $alleTema.hide();
    var $tekstfelt = $('#tema-textfield');
    var expanded = 'expanded';

    $tekstfelt.focusin(function() {
        $alleTema.removeClass(expanded);
        oppdaterTemavisning(this);
    });

    $('body').click(function(e) {
        if ($(e.target).hasClass('temastruktur') || $(e.target).hasClass('tema')){

        } else if (e.target.id !== 'tema-textfield') {
            $alleTema.hide();
        }
    });

    $tekstfelt.keydown(function(e) {
        var keyCode = e.keyCode;
        if ([ENTER, UP_ARROW, DOWN_ARROW].indexOf(keyCode) !== -1) {
            e.preventDefault();
        } else if (keyCode == TAB) {
            $alleTema.hide();
        }
    });

    $tekstfelt.keyup(function(e) {
        var $selected = $('li.selected');
        switch (e.keyCode) {
            case ENTER:
                this.value = $selected.text();
                $alleTema.hide();
                break;
            case LEFT_ARROW:
                $selected.removeClass(expanded);
                break;
            case UP_ARROW:
                var prev = $selected.prevAll(':visible:first');
                if (prev.length) {
                    $selected.removeClass('selected');
                    prev.addClass('selected');
                }
                break;
            case RIGHT_ARROW:
                if ($selected.hasClass('temastruktur')) {
                    $selected.addClass(expanded);
                }
                break;
            case DOWN_ARROW:
                var next = $selected.nextAll(':visible:first');
                if (next.length) {
                    $selected.removeClass('selected');
                    next.addClass('selected');
                }
                break;
            case ESC:
                $alleTema.hide();
                break;
            default:
                oppdaterTemavisning(this);
        }
    });

    $alleTema.click(function(e) {
        $tekstfelt.focus();
        velg($(e.target));
    });

    $alleTema.dblclick(function(e) {
        $tekstfelt.val($(e.target).text());
        $alleTema.hide();

    });

    function oppdaterTemavisning(tekstfelt) {
        for (var i = 0; i < $alleTema.length; i++) {
            var tekst = $alleTema.eq(i).text();
            var index = tekst.toLowerCase().indexOf(tekstfelt.value.toLowerCase());
            if (index == -1) {
                $alleTema.eq(i).hide();
            } else {
                $alleTema.eq(i).html(tekst.substring(0, index) +
                    '<span style="font-weight: bold">' + tekst.substr(index, tekstfelt.value.length) + '</span>' +
                    tekst.substring(index + tekstfelt.value.length, tekst.length));
                $alleTema.eq(i).show();
            }
        }
        velg($('li.temastruktur:visible, li.tema:visible').eq(0));
    }

    function velg(tema) {
        $alleTema.removeClass('selected');
        tema.addClass('selected');
    }

}