var changed = false;

$(document).ready(function() {
						   
	var $content = $('#content'),
		$contactForm = $content.find('#contactForm');
		$requestForm = $content.find('#requestForm');
			
		$("input").uniform();
		
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

		$requestForm.wl_Form({
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

		$('a.fancybox').fancybox();
});
