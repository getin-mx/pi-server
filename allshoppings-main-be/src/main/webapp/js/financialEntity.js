var mediaPhotos = Array();
var mediaPhotoCount = 0;
var mediaPhotosDeleted = Array();
var mediaPhotoDeletedCount = 0;
var lastBreadCrumb = 0;
var changed = false;

var mediaPhotoDelete = {
		onDelete: function (element, href, title) {
			element.fadeOut();
			var parts = href.split('/');
			var photo = parts[parts.length - 1];
			mediaPhotosDeleted[mediaPhotoDeletedCount] = photo;
			mediaPhotoDeletedCount++;
			$.post("/main-be/doRemoveFinancialEntityPhoto", { shoppingid: document.getElementById('financialEntityid').value, image: photo });
		}
};

$(document).ready(function() {

	$.getJSON('/main-be/getFinancialEntityMedia/' + document.getElementById('financialEntityid').value, function(data) {
		for( i = 0; i < data.photos.length; i++ ) {
			mediaPhotos[i] = data.photos[i];
			mediaPhotoCount++;
		}
	});

	var $body = $('body'),
	$content = $('#content'),
	$contactForm = $content.find('#contactForm'),
	$mediaForm = $content.find('#mediaForm'),
	$breadCrumb = $content.find('#headbreadcrumb');

	$("input").uniform();
	$("input").bind("change", function () {
	    changed = true;
	});

	$breadCrumb.wl_Breadcrumb({
		connect: 'breadcrumbcontent',
		onChange: function (element, id) {
			// Hack to resize the HighCharts reports to the actual screen size
			$(window).resize();
		}
	});


	$contactForm.wl_Form({
		ajax:true,
		status:false,
		confirmSend: false,
		onBeforeSubmit: function(data){
			if($('#description').val().length > 480 ) {
				$.msg($.i18n._('El contenido del campo descripcion es demasiado largo'),{header:$.i18n._('js.wl.alert.header')});
				return false;
			}
			$contactForm.wl_Form('set','sent',false);
		},
		onSuccess: function (data, textStatus, jqXHR) {
			var obj = jQuery.parseJSON(data);
			if( obj.response == 'fail') {
				$.msg($.i18n._(obj.message),{header:$.i18n._('js.wl.alert.header')});
			} else {
				$.msg($.i18n._('app.dataSaved'),{header:$.i18n._('js.wl.alert.header')});
				if( document.getElementById('crudtype').value == 'create') {
					window.location = '/main-be/financialEntity/' + obj.identifier;
				}
			}
		},
		onError: function (data, textStatus, jqXHR) {
			window.location = '/error-pages/error_500.html';
		}
	});

	$mediaForm.wl_Form({
		ajax:true,
		status:false,
		confirmSend: false,
		onBeforeSubmit: function(data){
			$mediaForm.wl_Form('set','sent',false);
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

	$('#mediaUpload').wl_File({
		autoUpload:true,
		multiple:true,
		onDone: function(e, data){
			var obj = data.result;
			mediaPhotos[mediaPhotoCount] = obj.name;
			mediaPhotoCount++;

			var gal = document.getElementById('mediaGallery');
			var line = '';
			for( var i = 0; i < mediaPhotoCount; i++ ) {
				var deleted = false;
				for(var j = 0; j < mediaPhotoDeletedCount; j++ ) {
					if( mediaPhotos[i] == mediaPhotosDeleted[j]) {
						deleted = true;
					}
				}
				if(!deleted) line += '<li><a href="/img/' + mediaPhotos[i] + '"><img src="/img/' + mediaPhotos[i] + '" width="116" height="116" alt=""></a></li>';
			}
			gal.innerHTML = line;
			$('#mediaGallery').data('wl_Gallery', null);
			$('#mediaGallery').wl_Gallery(mediaPhotoDelete);
		}
	});

	$('#mediaGallery').wl_Gallery(mediaPhotoDelete);
});
