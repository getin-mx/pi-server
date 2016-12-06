var mediaPhotos = Array();
var mediaPhotoCount = 0;
var mediaPhotosDeleted = Array();
var mediaPhotoDeletedCount = 0;
var changed = false;

var mediaPhotoDelete = {
		onDelete: function (element, href, title) {
			element.fadeOut();
			var parts = href.split('/');
			var photo = parts[parts.length - 1];
			mediaPhotosDeleted[mediaPhotoDeletedCount] = photo;
			mediaPhotoDeletedCount++;
			$.post("/main-be/doRemoveRewardPhoto", { rewardid: document.getElementById('rewardid').value, image: photo });
		}
};

$(document).ready(function() {

	$.getJSON('/main-be/getRewardMedia/' + document.getElementById('rewardid').value, function(data) {
		var i;
		for( i = 0; i < data.photos.length; i++ ) {
			mediaPhotos[i] = data.photos[i];
			mediaPhotoCount++;
		}
	});

	var $content = $('#content'),
	$contactForm = $content.find('#contactForm'),
	$policiesForm = $content.find('#policiesForm'),
	$mediaForm = $content.find('#mediaForm');

	$("input").uniform();
	$("input").bind("change", function () {
	    changed = true;
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
					window.location = '/main-be/reward/' + obj.identifier;
				}
			}
		},
		onError: function (data, textStatus, jqXHR) {
			window.location = '/error-pages/error_500.html';
		}
	});

	$policiesForm.wl_Form({
		ajax:true,
		status:false,
		confirmSend: false,
		onBeforeSubmit: function(data){
			$policiesForm.wl_Form('set','sent',false);
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

	$('#validFrom').wl_Date({
		mousewheel: true,
		dateFormat: "dd/mm/yy"
	});

	$('#validTo').wl_Date({
		mousewheel: true,
		dateFormat: "dd/mm/yy"
	});

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
			var i, j;
			
			var gal = document.getElementById('mediaGallery');
			var line = '';
			for( i = 0; i < mediaPhotoCount; i++ ) {
				var deleted = false;
				for(j = 0; j < mediaPhotoDeletedCount; j++ ) {
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
	
	$('#levelBrands').click(function() {
		$('#levelBrands').text($.i18n._('app.button.wait'));
		$.ajax({
			url: '/main-be/doLevelBrands',
			type: 'post',
			data: 'objs='+ encodeURI($('#shoppings').val())
		}).done(function(data) {
			var parts = data.split(',');
			var i;
			var ar = new Array();
			for( i = 0; i < parts.length; i++) {
				ar.push(parts[i]);
			}
			$('#levelBrands').text($.i18n._('app.button.levelBrands'));
			$('#brands').wl_Multiselect('select', ar);
		});
	});

	$('#levelShoppings').click(function() {
		$('#levelShoppings').text($.i18n._('app.button.wait'));
		$.ajax({
			url: '/main-be/doLevelShoppings',
			type: 'post',
			data: 'objs='+ encodeURI($('#brands').val())
		}).done(function(data) {
			var parts = data.split(',');
			var i;
			var ar = new Array();
			for( i = 0; i < parts.length; i++) {
				ar.push(parts[i]);
			}
			$('#levelShoppings').text($.i18n._('app.button.levelShoppings'));
			$('#shoppings').wl_Multiselect('select', ar);
		});
	});

	$('#levelAreas').click(function() {
		$('#levelAreas').text($.i18n._('app.button.wait'));
		$.ajax({
			url: '/main-be/doLevelAreas',
			type: 'post',
			data: 'objs='+ encodeURI($('#brands').val())
		}).done(function(data) {
			var parts = data.split(',');
			var i;
			var ar = new Array();
			for( i = 0; i < parts.length; i++) {
				ar.push(parts[i]);
			}
			$('#levelAreas').text($.i18n._('app.button.levelAreas'));
			$('#areas').wl_Multiselect('select', ar);
		});
	});

	$.extend( dataTableDefaults, {
		"sAjaxSource": "getRewardDeliveredMessagesRecords?reward=" + $('#rewardid').val(),
		"aoColumns" : [
		               {"sWidth" : "45%", "bSortable": false},
		               {"sWidth" : "25%", "bSortable": false},
		               {"sWidth" : "25%", "bSortable": false},
		               ]
	});
	$('#rewardDeliveredMessages').dataTable(dataTableDefaults);
});
