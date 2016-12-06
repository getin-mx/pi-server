var mediaPhotos = Array();
var mediaPhotoCount = 0;
var mediaPhotosDeleted = Array();
var mediaPhotoDeletedCount = 0;
var lastBreadCrumb = 0;
var changed = false;
var map;
var circle = null;
var circle2 = null;

var mediaPhotoDelete = {
		onDelete: function (element, href, title) {
			element.fadeOut();
			var parts = href.split('/');
			var photo = parts[parts.length - 1];
			mediaPhotosDeleted[mediaPhotoDeletedCount] = photo;
			mediaPhotoDeletedCount++;
			$.post("/main-be/doRemoveShoppingPhoto", { shoppingid: document.getElementById('shoppingid').value, image: photo });
		}
};

$(document).ready(function() {

	$.getJSON('/main-be/getShoppingMedia/' + document.getElementById('shoppingid').value, function(data) {
		for( var i = 0; i < data.photos.length; i++ ) {
			mediaPhotos[i] = data.photos[i];
			mediaPhotoCount++;
		}
	});

	var $content = $('#content'),
	$contactForm = $content.find('#contactForm'),
	$structureForm = $content.find('#structureForm'),
	$locationForm = $content.find('#locationForm'),
	$mediaForm = $content.find('#mediaForm'),
	$invoicingForm = $content.find('#invoicingForm'),
	$breadCrumb = $content.find('#headbreadcrumb'),
	
	$name = $contactForm.find('#name'),
	
	$lat = $locationForm.find('#latitude'),
	$lon = $locationForm.find('#longitude'),
	$fenceSize = $locationForm.find('#fenceSize'),
	$checkinAreaSize = $locationForm.find('#checkinAreaSize'),
	$updateCoordinates = $locationForm.find('#updateCoordinates');

	$.each($('.widget'), function(index, value) {
		$('#' + value.id).wl_Widget({
			collapsed: false,
			collapsible: false,
		});
	});

	$("input").uniform();
	$("input").bind("change", function () {
	    changed = true;
	});

	$breadCrumb.wl_Breadcrumb({
		connect: 'breadcrumbcontent',
		onChange: function (element, id) {
			if( element[0].id == 'bcLocation' ) {
				map.refresh();
				map.setCenter($lat.val(), $lon.val());
			} else if( element[0].id == 'bcFloorMaps') {
				w = $('#floor_map_container').css('width');
				$.each($('.floor_map_iframe'), function( index, value ) {
					value.width = w;
					value.contentWindow.location.reload();
				});
			} else {
				// Hack to resize the HighCharts reports to the actual screen size
				$(window).resize();
			}
		}
	});

	try {
		map = new GMaps({
			div: '#map',
			lat: $lat.val(),
			lng: $lon.val(),
			width: "100%",
			height: "400px"
		});
		updateMap(map, $lat, $lon, $fenceSize, $checkinAreaSize, $name);
		$lat.keyup(function() {updateMap(map, $lat, $lon, $fenceSize, $checkinAreaSize, $name);});
		$lon.keyup(function() {updateMap(map, $lat, $lon, $fenceSize, $checkinAreaSize, $name);});
		$fenceSize.keyup(function() {updateMap(map, $lat, $lon, $fenceSize, $checkinAreaSize, $name);});
		$checkinAreaSize.keyup(function() {updateMap(map, $lat, $lon, $fenceSize, $checkinAreaSize, $name);});
	} catch( exception ) {
		console.log(exception);
	}

	$updateCoordinates.click(function() {
		var $address = $locationForm.find('#streetName').val() + " " 
			+ $locationForm.find('#streetNumber').val() + ", "
			+ $locationForm.find('#city').val() + ", "
			+ $locationForm.find('#province').val() + ", "
			+ $locationForm.find('#country').val();
		
		$.ajax({
			type : "GET",
			url : 'doFindShoppingGeo?address=' + encodeURI($address),
			dataType : "json",
			success : function(data) {
				$lat.val(data.latitude);
				$lon.val(data.longitude);
				updateMap(map, $lat, $lon, $fenceSize, $checkinAreaSize, $name);
				$.msg($.i18n._('Geo localizacion actualizada. Por favor, presiona aceptar para salvar la informacion.'),{header:$.i18n._('js.wl.alert.header')});
			},
			error : function() {
				$.msg($.i18n._('Error de geo localizacion'),{header:$.i18n._('js.wl.alert.header')});
				return false;
			}
		});
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
					window.location = '/main-be/shopping/' + obj.identifier;
				}
			}
		},
		onError: function (data, textStatus, jqXHR) {
			window.location = '/error-pages/error_500.html';
		}
	});

	$invoicingForm.wl_Form({
		ajax:true,
		status:false,
		confirmSend: false,
		onBeforeSubmit: function(data){
			$invoicingForm.wl_Form('set','sent',false);
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
	
	$structureForm.wl_Form({
		ajax:true,
		status:false,
		confirmSend: false,
		onBeforeSubmit: function(data){
			$structureForm.wl_Form('set','sent',false);
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

	$('#frontImage').wl_File({
		autoUpload:true,
		multiple:false,
		onDone: function(e, data){
			var frontImageImg = document.getElementById('frontImageImg');
			var frontImageAnchor = frontImageImg.parentElement;
			var obj = data.result;
			$("#frontImageId").val(obj.name);
			frontImageImg.src = '/img/' + obj.name;
			frontImageAnchor.href = '/img/' + obj.name;
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

function updateMap(map, $lat, $lon, $fenceSize, $checkinAreaSize, $name) {
	try {
		if( circle != null ) circle.setMap(null);
		if( circle2 != null ) circle2.setMap(null);
		map.removeMarkers();
		map.addMarker({
			lat: $lat.val(),
			lng: $lon.val(),
			title: $name.val(),
			draggable : true,
			dragend : function(mouseevent) {
				$lat.val(this.getPosition().lat());
				$lon.val(this.getPosition().lng());
				updateMap(map, $lat, $lon, $fenceSize, $checkinAreaSize, $name);
			}
		});
		circle = map.drawCircle({
			lat : $lat.val(),
			lng : $lon.val(),
			fillColor : '#ff0000',
			fillOpacity : 0.2,
			strokeColor : '#ff0000',
			strokeWeight : 1,
			radius : parseInt($fenceSize.val())
		});
		circle2 = map.drawCircle({
			lat : $lat.val(),
			lng : $lon.val(),
			fillColor : '#00ff00',
			fillOpacity : 0.2,
			strokeColor : '#00ff00',
			strokeWeight : 1,
			radius : parseInt($checkinAreaSize.val())
		});
		map.setCenter($lat.val(), $lon.val());
	} catch( exception ) {
		console.log(exception);
	}
}