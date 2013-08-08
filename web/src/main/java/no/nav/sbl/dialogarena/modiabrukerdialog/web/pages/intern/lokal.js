jQuery(document).ready(function ($) {
	'use strict';

	Modig.shortcutListener.on({ alt: true, shift: false, key: 'V' }, toggleKjerneinfo);

    createTabHandler("modiabrukerdialog");

	$('.brukerprofil').on('keydown', function (e) {
		var keyCode = e.keyCode || e.which;
		if (keyCode === 13) {
			if (!e.shiftKey) {
				e.preventDefault();
			}
		}
	});

	$('#toggle-kjerneinfo').on('click', toggleKjerneinfo);

	$('.sidebar').on('click', function () {
		var sidebar = $(this);
		if (sidebar.css('position') == 'absolute') {
			if (sidebar.css('right') == '-385px') {
				sidebar.animate({right: '0'}, '25');
			} else {
				sidebar.animate({right: '-385px'}, '25');
			}
		}
	});

	$('.sidebar > *').on('click', function (e) {
		e.stopPropagation();
	});

	$('#toggle-personsok').on('click', checkIfToggleAvansertSok);
	Modig.shortcutListener.on({alt: true, shift: true, key: 'A'}, checkIfToggleAvansertSok);

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

	Modig.ajaxLoader.register({
		urlPattern: /searchPanel-hentPersonForm-submitFodselsnummer/,
		placeElement: '.search-intern',
		placement: 'after',
		imageUrl: '/modiabrukerdialog/img/ajaxloader/svart/loader_svart_32.gif',
		css: 'margin: 10px 0 10px 10px; float: left;'
	});
});

function toggleKjerneinfo() {
	var kjerneInfoElement = $('.main > .kjerneinfo'), toggleElement = $('#toggle-kjerneinfo'), visittkort = $('aside.sidebar > .visittkort');
	if (kjerneInfoElement.is(':visible')) {
		kjerneInfoElement.hide();
		toggleElement.html(toggleElement.attr('data-show-text'));
		visittkort.removeClass('active');
	} else {
		kjerneInfoElement.show();
		toggleElement.html(toggleElement.attr('data-hide-text'));
		visittkort.addClass('active');

		if ($('#personsokPanel').is(':visible')) {
			toggleAvansertSok();
		}
	}
}

function movePersonsok() {
	var navbar = $('.navbar');
	var logo = $('.modia-logo');
	var nullstill = $('INPUT[name=nullstillSok]');

	if (navbar[0].style.marginTop == '1%') {
		toggleAvansertSok();
		navbar.animate({marginTop: '8%'}, 300, 'linear');
		navbar.css('marginBottom', '0');
		logo.css('display', 'block');
		// Nullstiller søket
		nullstill.click();
	} else {
		navbar.animate({marginTop: '1%'}, 400, 'linear', toggleAvansertSok);
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

function createTabHandler(application){
    var localStorageId = application + ".activeTab";
    var sessionStorageId = application + ".tabGuid";
    var isReloadingId = application  + ".reloading";
    var greyBackground = '<div class="wicket-mask-dark" style="z-index: 20000; background-image: none; position: absolute; top: 0px; left: 0px;"></div>';
    var modalDialog ='<div class="wicket-modal" id="_wicket_window_0" tabindex="-1" role="dialog" style="position: absolute; width: 600px; left: 660px; top: 180.5px; visibility: visible;"><form><div class="w_content_1" ><div class="w_caption" id="_wicket_window_1"><a class="w_close" role="button" id="closeModalButton" style="z-index:1" href="#"></a></div><div id="_wicket_window_2" class="w_content_container" style="overflow: auto; height: 248px;"><div id="content5c"><section class="bekreft-dialog"><h1 class="robust-ikon-hjelp-strek">Du har allerede et vindu med ModiaBrukerDialog åpent. Hvis du fortsetter i dette vinduet så vil du miste ulagret arbeide i det andre vinduet. Ønsker du å fortsette med dette vinduet?</h1><ul><li><submit class="knapp-stor" id="confirmCloseTab" >Avbryt, jeg vil ikke miste ulagret arbeide</submit></li><li><a id="confirmActivateTab" href="#">Fortsett med dette vinduet</a></li></ul></section></div></div></div></form></div>';

    /**
     * Generates a GUID string, according to RFC4122 standards.
     * @returns {String} The generated guid
     * @example eff5d22c-74fa-7139-d4e8-6879736b04fa
     * @author Slavik Meltser (slavik@meltser.info)
     * @link http://slavik.meltser.info/?p=142
     */
    var createGuid = function(){
        function _p8(s){
            var p =(Math.random().toString(16)+"000000000").substr(2,8);
            return s ? "-" + p.substr(0,4) + "-" + p.substr(4,4) : p;
        }
        return _p8(false) + _p8(true) + _p8(true) + _p8(false);
    };


    var getTabGuid = function(){
        var tabGuid = window.sessionStorage.getItem(sessionStorageId);
        if(!tabGuid){
            tabGuid = createGuid();
            window.sessionStorage.setItem(sessionStorageId, tabGuid);
        }
        return tabGuid;
    };

    var getCurrentActiveTab = function(){
        var currentActiveTab = window.localStorage.getItem(localStorageId);
        if(!currentActiveTab){
            var tabGuid = getTabGuid();
            console.log("Ingen aktiv tab, setter den nå. Guid = " + tabGuid);
            setActiveTab(tabGuid);
            currentActiveTab = tabGuid;
        }
        return currentActiveTab;
    };

    var setActiveTab = function(tabGuid){
        console.log("Setter aktiv tab: "+ tabGuid);
        window.localStorage.setItem(localStorageId, tabGuid);
    };

    var activateTab = function(){
        removeModalDialog();
        setActiveTab(getTabGuid());
        setIsReloadingFlag();
        window.location.reload();
    };

    var closeTab = function(){
        window.close();
    };

    var isReloading = function(){
        var isReloading = window.sessionStorage.getItem(isReloadingId);
        if(isReloading === "true")
            return true;
        return false;
    };

    var setIsReloadingFlag = function(){
        window.sessionStorage.setItem(isReloadingId, "true");
    }

    var clearIsReloadingFlag = function(){
        window.sessionStorage.removeItem(isReloadingId);
    }

    var isModalDialogVisible = function(){
        var backGround = $('.wicket-mask-dark');
        if(backGround.length == 0){
            return false;
        }
        return true;
    };

    var createModalDialog = function() {
        $("body").append(modalDialog).append(greyBackground);
        $("#confirmActivateTab").click(activateTab);
        $('#confirmCloseTab').click(closeTab);
        $('#closeModalButton').click(closeTab);
    };

    var removeModalDialog = function(){
        $('.wicket-mask-dark').remove();
        $('wicket-modal').remove();
    };

    var storageEventListener = function(e){
        if(e.key === localStorageId){
            if(e.newValue === null || e.newValue === ""){
                return;
            }
            var tabGuid = getTabGuid();
            if(e.newValue !== tabGuid && !isModalDialogVisible() && !isReloading()){
                createModalDialog();
            }
        }
    };

    var unloadListener = function(){
        console.log("Unload");
        var tabGuid = getTabGuid();
        var currentActiveTab = getCurrentActiveTab();
        if(tabGuid == currentActiveTab){
            window.localStorage.removeItem(localStorageId);
        }
    };

    var init = function(){
        var tabGuid = getTabGuid();
        var currentActiveTab = getCurrentActiveTab();

        if(!currentActiveTab){
            setActiveTab(tabGuid);
            currentActiveTab = tabGuid;
        }
        if(currentActiveTab !== tabGuid && !isReloading()){
            console.log("Dette er ikke aktiv tab. Viser dialog")
            createModalDialog();
        }else{
            setActiveTab(tabGuid);
        }
        window.addEventListener("storage", storageEventListener);
        window.addEventListener("unload", unloadListener);
        clearIsReloadingFlag();
    };

    init();
}
