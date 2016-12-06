var changed = false;

$(document).ready(function() {
						   
	var $body = $('body'),
		$content = $('#content'),
		$contactForm = $content.find('#contactForm'),
		$locationForm = $content.find('#locationForm'),
		$messagingForm = $content.find('#messagingForm');
			
		$("input").uniform();
		$("input").bind("change", function () {
		    changed = true;
		});

		$contactForm.wl_Form({
			ajax:true,
			status:false,
			confirmSend: false,
			onBeforeSubmit: function(data){
				$contactForm.wl_Form('set','sent',false);
			},
			onSuccess: function (data, textStatus, jqXHR) {
				var obj = jQuery.parseJSON(data);
				if( obj.response == 'fail') {
					$.msg($.i18n._(obj.message),{header:$.i18n._('js.wl.alert.header')});
				} else {
					$.msg($.i18n._('app.dataSaved'),{header:$.i18n._('js.wl.alert.header')});
				}
			},
			onError: function (data, textStatus, jqXHR) {
				window.location = '/error-pages/error_500.html';
			}
		});

		$locationForm.wl_Form({
			ajax:true,
			status:false,
			confirmSend: false,
			onBeforeSubmit: function(data){
				$locationForm.wl_Form('set','sent',false);
			},
			onSuccess: function (data, textStatus, jqXHR) {
				var obj = jQuery.parseJSON(data);
				if( obj.response == 'fail') {
					$.msg($.i18n._(obj.message),{header:$.i18n._('js.wl.alert.header')});
				} else {
					$.msg($.i18n._('app.dataSaved'),{header:$.i18n._('js.wl.alert.header')});
				}
			},
			onError: function (data, textStatus, jqXHR) {
				window.location = '/error-pages/error_500.html';
			}
		});

		$messagingForm.wl_Form({
			ajax:true,
			status:false,
			confirmSend: false,
			onBeforeSubmit: function(data){
				$locationForm.wl_Form('set','sent',false);
			},
			onSuccess: function (data, textStatus, jqXHR) {
				var obj = jQuery.parseJSON(data);
				if( obj.response == 'fail') {
					$.msg($.i18n._(obj.message),{header:$.i18n._('js.wl.alert.header')});
				} else {
					$.msg($.i18n._('app.dataSaved'),{header:$.i18n._('js.wl.alert.header')});
				}
			},
			onError: function (data, textStatus, jqXHR) {
				window.location = '/error-pages/error_500.html';
			}
		});

		$('a.fancybox').fancybox();
		
		$('#avatar').wl_File({
			autoUpload:true,
			multiple:false,
			onDone: function(e, data){
				var avatarImage = document.getElementById('avatarImage');
				var avatarAnchor = avatarImage.parentElement;
				var obj = data.result;
				$("#avatarId").val(obj.name);
				avatarImage.src = '/img/' + obj.name;
				avatarAnchor.href = '/img/' + obj.name;
			}
		});
	
		$.get( "/main-be/session/role?unique=" + new Date().getUTCMilliseconds(), function( data ) {});
		$.get( "/main-be/session/availableCountries?unique=" + new Date().getUTCMilliseconds(), function( data ) {});
		$.get( "/main-be/session/shopping?unique=" + new Date().getUTCMilliseconds(), function( data ) {});
		$.get( "/main-be/session/brand?unique=" + new Date().getUTCMilliseconds(), function( data ) {});
		$.get( "/main-be/session/financialEntity?unique=" + new Date().getUTCMilliseconds(), function( data ) {});

});
