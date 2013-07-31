function temavelger() {
    var $alleTema = $('li.temastruktur, li.tema');
    $alleTema.hide();
    var $tekstfelt = $('#tema-textfield');

    $tekstfelt.focusin(function() {
        oppdaterTemavisning(this);
    });

    $tekstfelt.focusout(function() {
        $alleTema.hide();
    });

    $tekstfelt.keypress(function(e) {
        // enter
        if (e.keyCode == '13') {
            e.preventDefault();
        }
    });

    $tekstfelt.keyup(function(e) {
        var index = $('li.selected').index();
        // enter
        if (e.keyCode == '13') {
            this.value = $('li.selected').eq(0).text();
            $alleTema.hide();
        }
        // up arrow
        else if (e.keyCode == '38') {
            $alleTema.eq(index).removeClass('selected');
            $alleTema.eq(index > 0 ? --index : $alleTema.length - 1).addClass('selected');
        }
        // down arrow
        else if (e.keyCode == '40') {
            $alleTema.eq(index).removeClass('selected');
            $alleTema.eq(index < ($alleTema.length - 1) ? ++index : 0).addClass('selected');
        }
        else {
            oppdaterTemavisning(this);
        }
    });

    function oppdaterTemavisning(tekstfelt) {
        for (var i = 0; i < $alleTema.length; i++) {
            var tekst = $alleTema[i].innerText;
            var index = tekst.toLowerCase().indexOf(tekstfelt.value.toLowerCase());
            if (index == -1) {
                $($alleTema[i]).hide();
            } else {
                $($alleTema[i]).html(tekst.substring(0, index) +
                    '<span style="font-weight: bold">' + tekst.substr(index, tekstfelt.value.length) + '</span>' +
                    tekst.substring(index + tekstfelt.value.length, tekst.length));
                $($alleTema[i]).show();
            }
            $alleTema.eq(i).removeClass('selected');
        }
        $('li.temastruktur:visible, li.tema:visible').eq(0).addClass('selected');
    }
}