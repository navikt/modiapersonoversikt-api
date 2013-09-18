jQuery(document).ready(function ($) {
	'use strict';

	Modig.shortcutListener.on({ alt: true, keyCode: 112 }, toggleKjerneinfo);   // F1

	createTabHandler("modiabrukerdialog");

	$('#toggle-kjerneinfo, .kjerneinfo .lukk').on('click', toggleKjerneinfo);

	$('.brukerprofillink').on('click', closeSidebar);

	$('.sidebar').on('click', function () {
		var sidebar = $(this);
        if (sidebar.css('right') == '-385px') {
            openSidebar();
        } else {
            closeSidebar();
        }
	});


	$('.sidebar > *').on('click', function (e) {
		e.stopPropagation();
	});

	$('#toggle-personsok').on('click', checkIfToggleAvansertSok);
	Modig.shortcutListener.on({key: 'A'}, checkIfToggleAvansertSok);

	Modig.shortcutListener.on({alt: true, keyCode: 114}, focusSearchField); // F3
	Modig.shortcutListener.on({alt: true, keyCode: 116}, closeResetPerson); // F5
	Modig.shortcutListener.on({alt: true, keyCode: 117}, focusLamellHead);  // F6
	Modig.shortcutListener.on({alt: true, keyCode: 118}, closeLamellHead);  // F7

	$('body').on('click', '.lamell .lamellhode > a', function () {
		if ($('.main > .personsok').is(':visible')) {
			toggleAvansertSok();
		}
	});

	Modig.shortcutListener.on({alt: false, shift: true, keyCode: 13}, gjennomfoerAvansertSok);

	// IE9 fiks til søk-knapp i personsøk pga bugg med :hover på anet en A-element
	$('.search-intern input[type=button]').hover(function () {
		$(this).addClass('hover');
	}, function () {
		$(this).removeClass('hover');
	});

	/** Registrer ajax loadere **/

	Modig.ajaxLoader.register({
		urlPattern: /utvidetPersonsokForm-hiddenSumbitLink/,
		placeElement: '#personsokResult',
		imageUrl: '/modiabrukerdialog/img/ajaxloader/svart/loader_svart_96.gif',
		css: 'margin-bottom: 20px;'
	});

    if ($('#submitFodselsnummer').length > 0) {
        var scaling = 0.8;
        var height = $('#submitFodselsnummer').innerHeight();
        var posDiff = (height*(1-scaling)/2);
        var top = $('#submitFodselsnummer').position().top + posDiff + "px";
        var left = $('#submitFodselsnummer').position().left + posDiff + "px";
        height = height*scaling;

        Modig.ajaxLoader.register({
            urlPattern: /searchPanel-hentPersonForm-submitFodselsnummer/,
            placeElement: '#submitFodselsnummer',
            placement: 'hide',
            imageUrl: '/modiabrukerdialog/img/ajaxloader/hvit/loader_hvit_48.gif',
            css: 'position: absolute; top:' + top + '; left:' + left + '; height:' + height + 'px'
        });
    }

    detectWidthChange();
});

function focusSearchField() {
	$('#foedselsnummerInput').focus()
}

function focusLamellHead() {
	$(document.activeElement).parents('.lamell').find('.lamellhode a').focus();
}

function closeLamellHead() {
	$(document.activeElement).parents('.lamell').find('button.close').click();
}

function closeResetPerson() {
	$('.nullstill-button').click();
}

function detectWidthChange() {
    var win = $(window);

    /*
     * Media queries i JS fungerer ikke i IE9. Vi vil oppdage når sidebaren glir ut til siden,
     * som skjer når den er absolutt posisjonert. Sjekker derfor for dette.
     */
    var sidebarPositionedAbsolute = isSidebarPositionAbsolute();

    win.resize(function () {
        if (!sidebarPositionedAbsolute) {
            widthChanged();
        }
        sidebarPositionedAbsolute = isSidebarPositionAbsolute();
    });
    widthChanged();
}

function widthChanged() {
    var sidebar = $('aside.sidebar');
    if (sidebar.css('position') == 'absolute') {
        sidebar.addClass('expanded');
        sidebar.css('right', '0px');
    }
}

function openSidebar() {
    var sidebar = $('aside.sidebar');
    if (isSidebarPositionAbsolute()) {
        sidebar.addClass('expanded');
        sidebar.animate({right: '0'}, '25');
    }
}

function closeSidebar() {
    var sidebar = $('aside.sidebar');
    if (isSidebarPositionAbsolute()) {
        sidebar.removeClass('expanded');
        sidebar.animate({right: '-385px'}, '25');
    }
}

function isSidebarPositionAbsolute() {
    return $('aside.sidebar').css('position') == 'absolute';
}

function toggleKjerneinfo() {
	var kjerneInfoElement = $('.main > .kjerneinfo');
    var toggleElement = $('#toggle-kjerneinfo');
    var visittkort = $('aside.sidebar > .visittkort');

	if (kjerneInfoElement.is(':visible')) {
		kjerneInfoElement.hide();
		toggleElement.attr('title', (toggleElement.attr('data-show-text')));
		visittkort.removeClass('active');
	} else {
		kjerneInfoElement.show();
		toggleElement.attr('title', (toggleElement.attr('data-hide-text')));
		visittkort.addClass('active');
        closeSidebar();

		if ($('#personsokPanel').is(':visible')) {
			toggleAvansertSok();
		}
	}
}

function movePersonsok() {
	var navbar = $('.navbar');
	var logo = $('.modia-logo');
	var nullstill = $('INPUT[name=nullstillSok]');
    var error = $('.feedbackPanelERROR');
    error.remove();

	if (navbar[0].style.marginTop == '1%') {
		toggleAvansertSok();
		navbar.animate({marginTop: '8%'}, 300, 'linear');
		navbar.css('marginBottom', '0');
		logo.css('display', 'block');
		// Nullstiller søket
		nullstill.click();
	} else {
		if ($('.main').hasClass('hentperson')) {
			navbar.animate({marginTop: '1%'}, 400, 'linear', toggleAvansertSok);
			navbar.css('marginBottom', '1.1%');
		} else {
			if ($('#personsokPanel').is(':visible')) {
				toggleAvansertSok();
			} else {
				$('#personsok').slideDown(400);

			}
		}
		logo.css('display', 'none');
	}
}

function checkIfToggleAvansertSok() {
	if (!$('#toggle-personsok').hasClass('personsok-movable')) {
		toggleAvansertSok();
	} else {
		movePersonsok();
	}
}

function toggleAvansertSok() {
	var personsokElement = $('#personsok'), toppmeny = $('.main > .menu-intern'),
			fornavnInput = $('#utvidetPersonsokForm').find('input[name="fornavn"]'), sokKnapp = $('#toggle-personsok'),
			dataApne = sokKnapp.attr('data-apne'),
			dataLukke = sokKnapp.attr('data-lukke');
	if (personsokElement.is(':visible')) {
		personsokElement.hide();
		toppmeny.removeClass('active');
		sokKnapp.removeClass('active');
		sokKnapp.attr('title', dataApne);
		sokKnapp.attr('aria-label', dataApne);
	} else {
		personsokElement.show();
		toppmeny.addClass('active');
		sokKnapp.addClass('active');
		sokKnapp.attr('title', dataLukke);
		sokKnapp.attr('aria-label', dataLukke);

		if (fornavnInput.length != 0) {
			fornavnInput.focus();
		}

		if ($('.main > .kjerneinfo').is(':visible')) {
			toggleKjerneinfo();
		}
	}
}

function gjennomfoerAvansertSok() {
	var personsokElement = $('.main > .personsok');
	if (personsokElement.is(":visible")) {
		$('#utvidetPersonsokForm:visible').submit();
	}
}

function setSessionTimeoutBox() {
    var timeoutValue = 1000 * 60 * 55;
    var timeout = setTimeout(function () {
        createTimeoutBox();
    }, timeoutValue);

    Wicket.Event.subscribe('/ajax/call/after', function () {
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            createTimeoutBox();
        }, timeoutValue);
    });
}

function createTimeoutBox() {
    if (!$('.wicket-mask-dark')[0]) {
        $('body').append($('<div/>').addClass('wicket-mask-dark'));
    }
    $('.informasjonsboks.timeout').show();
}

function createTabHandler(application) {
	var localStorageId = application + ".activeTab";
	var sessionStorageId = application + ".tabGuid";
	var isReloadingId = application + ".reloading";
	var greyBackground = '<div class="wicket-mask-dark" style="z-index: 20000; background-image: none; position: absolute; top: 0px; left: 0px;"></div>';
	var modalDialog = '<div class="wicket-modal" id="_wicket_window_0" tabindex="-1" role="dialog" style="position: absolute; width: 600px; left: 660px; top: 180.5px; visibility: visible;"><form><div class="w_content_1" ><div class="w_caption" id="_wicket_window_1"><a class="w_close" role="button" id="closeModalButton" style="z-index:1" href="#"></a></div><div id="_wicket_window_2" class="w_content_container" style="overflow: auto; height: 300px;"><div id="content5c"><section class="bekreft-dialog"><h1 class="robust-ikon-hjelp-strek">Du har allerede et vindu med ModiaBrukerDialog åpent. Hvis du fortsetter i dette vinduet så vil du miste ulagret arbeide i det andre vinduet. Ønsker du å fortsette med dette vinduet?</h1><ul><li><submit class="knapp-stor" id="confirmCloseTab" >Avbryt, jeg vil ikke miste ulagret arbeide</submit></li><li><a id="confirmActivateTab" href="#">Fortsett med dette vinduet</a></li></ul></section></div></div></div></form></div>';

	/**
	 * Generates a GUID string, according to RFC4122 standards.
	 * @returns {String} The generated guid
	 * @example eff5d22c-74fa-7139-d4e8-6879736b04fa
	 * @author Slavik Meltser (slavik@meltser.info)
	 * @link http://slavik.meltser.info/?p=142
	 */
	var createGuid = function () {
		function _p8(s) {
			var p = (Math.random().toString(16) + "000000000").substr(2, 8);
			return s ? "-" + p.substr(0, 4) + "-" + p.substr(4, 4) : p;
		}

		return _p8(false) + _p8(true) + _p8(true) + _p8(false);
	};


	var getTabGuid = function () {
		var tabGuid = window.sessionStorage.getItem(sessionStorageId);
		if (!tabGuid) {
			tabGuid = createGuid();
			window.sessionStorage.setItem(sessionStorageId, tabGuid);
		}
		return tabGuid;
	};

	var getCurrentActiveTab = function () {
		return window.localStorage.getItem(localStorageId);
	};

	var setActiveTab = function (tabGuid) {
		window.localStorage.setItem(localStorageId, tabGuid);
	};

	var activateTab = function () {
		removeModalDialog();
		setActiveTab(getTabGuid());
		setIsReloadingFlag();
		window.location.reload();
	};

	var closeTab = function () {
		//funker i IE og Chrome
		//funker ikke i Firefox
		window.open('', '_self', '');
		window.close();
	};

	var isReloading = function () {
		var isReloading = window.sessionStorage.getItem(isReloadingId);
		return isReloading === "true";
	};

	var setIsReloadingFlag = function () {
		window.sessionStorage.setItem(isReloadingId, "true");
	};

	var clearIsReloadingFlag = function () {
		window.sessionStorage.removeItem(isReloadingId);
	};

	var isModalDialogVisible = function () {
		var backGround = $('.wicket-mask-dark');
		return backGround.length != 0;

	};

	var createModalDialog = function () {
		$("body").append(modalDialog).append(greyBackground);
		$("#confirmActivateTab").click(activateTab);
		$('#confirmCloseTab').click(closeTab);
		$('#closeModalButton').click(closeTab);
	};

	var removeModalDialog = function () {
		$('.wicket-mask-dark').remove();
		$('wicket-modal').remove();
	};

	var storageEventListener = function (e) {
		var tabGuid;
		if (e.key === localStorageId) { // bryr oss bare om localStorage events
			if (!e.newValue) { // hvis ny verdi er null eller tom, ignorer event
				return;
			}
			tabGuid = getTabGuid();
			if (e.newValue !== tabGuid && !isModalDialogVisible() && !isReloading()) {  // dette er en forskjellig verdi og vi ikke viser dialog fra før, og vi ikke er i en refresh, vis dialog
				createModalDialog();
			}
		}
	};

	var unloadListener = function () {
		var tabGuid = getTabGuid();
		var currentActiveTab = getCurrentActiveTab();
		if (tabGuid == currentActiveTab) { //hvis det er aktiv tab som lukkes, slette fra localStorage
			window.localStorage.removeItem(localStorageId);
		}
	};

	var init = function () {
		var tabGuid = getTabGuid();
		var currentActiveTab = getCurrentActiveTab();
		if (isReloading()) {
			setActiveTab(tabGuid);
		} else {
			if (!currentActiveTab) { //ingen aktiv tab fra før, setter denne som aktiv
				setActiveTab(tabGuid);
				currentActiveTab = tabGuid;
			}
			if (currentActiveTab !== tabGuid) {//Aktiv tab er en annen en denne. Spør bruker om vi skal bruke denne tab
				createModalDialog();
			}
		}
		window.addEventListener("storage", storageEventListener);
		window.addEventListener("unload", unloadListener);
		clearIsReloadingFlag();
	};

	init();
}
