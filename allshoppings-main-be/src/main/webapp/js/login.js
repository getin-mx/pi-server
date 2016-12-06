$(document).ready(function() {
						   
	var $body = $('body'),
		$content = $('#content'),
		$form = $content.find('#loginform'),
		myAlert;
		
		//IE doen't like that fadein
		if(!$.browser.msie) $body.fadeTo(0,0.0).delay(500).fadeTo(1000, 1);
		
		$("input").uniform();

		$form.wl_Form({
			ajax:true,
			status:false,
			confirmSend: false,
			onBeforeSubmit: function(data){
				if( myAlert ) $.fn.wl_Alert.methods.close.call(myAlert);
				$form.wl_Form('set','sent',false);
				if(!data.username) {
					myAlert = $.wl_Alert($.i18n._('app.login.missingUsername'), 'info', '#content');
					return false;
				}
				if(!data.password) {
					myAlert = $.wl_Alert($.i18n._('app.login.missingPassword'), 'info', '#content');
					return false;
				}
				$('.submit').hide();
			},
			onSuccess: function (data, textStatus, jqXHR) {
				var obj = jQuery.parseJSON(data);
				if( obj.response == 'fail') {
					myAlert = $.wl_Alert($.i18n._(obj.message), 'info', '#content');
					$('.submit').show();
				} else {
					window.location = obj.redirect;
				}
			},
			onError: function (data, textStatus, jqXHR) {
				window.location = '/error-pages/error_500.html';
			}
		});
		
		if( $.fn.postLoad ) {
			$.fn.postLoad();
		}
		
});