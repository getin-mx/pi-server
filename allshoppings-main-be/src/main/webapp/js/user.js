var changed = false;
var map;

$(document).ready(function() {

	var $content = $('#content'),
	$contactForm = $content.find('#contactForm'),
	$locationForm = $content.find('#locationForm'),
	$messagingForm = $content.find('#messagingForm'),
	$permissionsForm = $content.find('#permissionsForm');
	
	var $breadcrumb = $('#headbreadcrumb');

	$breadcrumb.wl_Breadcrumb({
			onChange: function(element, id) {
				if( element[0].id == 'bcLastLocation') {
					map = new GMaps({
						div: '#map',
						lat: 0,
						lng: 0,
						width: "100%",
						height: "550px"
					});

					map.setZoom(1);

					var xhr = $.getJSON('/main-be/getLastUserLocation?userId=' + $('#userId').val() + '&datetime='+rand());
					xhr.done(loadResults);
				}
			},
	});
	
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
				if( document.getElementById('crudtype').value == 'create') {
					window.location = '/main-be/user/' + obj.identifier;
				}
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
			$messagingForm.wl_Form('set','sent',false);
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

	$permissionsForm.wl_Form({
		ajax:true,
		status:false,
		confirmSend: false,
		onBeforeSubmit: function(data){
			$permissionsForm.wl_Form('set','sent',false);
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

	$.extend( dataTableDefaults, {
		"sAjaxSource": "getDeviceInfoRecords?userId=" + $('#userid').val(),
		"aoColumns" : [
		               {"sWidth" : "15%", "bSortable": false},
		               {"sWidth" : "15%", "bSortable": false},
		               {"sWidth" : "15%", "bSortable": false},
		               {"sWidth" : "15%", "bSortable": false},
		               {"sWidth" : "15%", "bSortable": false},
		               {"sWidth" : "15%", "bSortable": false},
		               {"sWidth" : "15%", "bSortable": false},
		               {"sWidth" : "5%", "bSortable": false},
		               ]
	});
	$('#userDevices').dataTable(dataTableDefaults);

	$.extend( dataTableDefaults, {
		"sAjaxSource": "getUserDeliveredMessagesRecords?userId=" + $('#userid').val(),
		"aoColumns" : [
		               {"sWidth" : "25%", "bSortable": false},
		               {"sWidth" : "20%", "bSortable": false},
		               {"sWidth" : "25%", "bSortable": false},
		               {"sWidth" : "25%", "bSortable": false},
		               ]
	});
	$('#userDeliveredMessages').dataTable(dataTableDefaults);

	$( '#role' ).change(function() {
		doRoleChange();
	});

	doRoleChange();

});

function doRoleChange() {
	if( $('#role').val() == 3 ) {
		$('#availableCountriesSection').show();
		$('#shoppingsSection').hide();
		$('#brandsSection').hide();
		$('#financialEntitiesSection').hide();
	} else if ($('#role').val() == 5 ) {
		$('#availableCountriesSection').hide();
		$('#shoppingsSection').show();
		$('#brandsSection').hide();
		$('#financialEntitiesSection').hide();
	} else if ($('#role').val() == 7 ) {
		$('#availableCountriesSection').hide();
		$('#shoppingsSection').hide();
		$('#brandsSection').show();
		$('#financialEntitiesSection').hide();
	} else if ($('#role').val() == 9 ) {
		$('#availableCountriesSection').hide();
		$('#shoppingsSection').hide();
		$('#brandsSection').hide();
		$('#financialEntitiesSection').show();
	} else if ($('#role').val() == 11 ) {
		$('#availableCountriesSection').show();
		$('#shoppingsSection').hide();
		$('#brandsSection').hide();
		$('#financialEntitiesSection').hide();
	} else if ($('#role').val() == 13 ) {
		$('#availableCountriesSection').show();
		$('#shoppingsSection').hide();
		$('#brandsSection').hide();
		$('#financialEntitiesSection').hide();
	} else {
		$('#availableCountriesSection').hide();
		$('#shoppingsSection').hide();
		$('#brandsSection').hide();
		$('#financialEntitiesSection').hide();
	}
}

function loadResults(data) {
	var items, markers_data = [];
	if (data.data.length > 0) {
		items = data.data;

		for (var i = 0; i < items.length; i++) {
			var item = items[i];

			if (item.latitude != undefined && item.longitude != undefined) {
				if( item.kind == 'user' ) {
					var icon = '/main-be/css/images/icon_user.png';
					markers_data.push({
						lat : item.latitude,
						lng : item.longitude,
						title : item.userid,
						infoWindow: {
							content: '<p><strong>' + item.username + '</strong><br/>' + item.devicePlatform + ' ' + item.deviceName + '<br/>Vers: ' + item.appVersion + '<br/>' + item.lastupdate + '</p>'
						},
						icon : {
							size : new google.maps.Size(32, 32),
							url : icon
						}
					});
				} else if( item.kind == 'shopping' ) {
					var icon = '/main-be/css/images/icon_shopping.png';
					markers_data.push({
						lat : item.latitude,
						lng : item.longitude,
						title : item.name,
						infoWindow: {
							content: '<p><strong><a href="/main-be/shopping/' + item.identifier + '">' + item.name + '</a></p>'
						},
						icon : {
							size : new google.maps.Size(32, 32),
							url : icon
						}
					});

					if( !circlesDrawed ) {
						map.drawCircle({
							lat : item.latitude,
							lng : item.longitude,
							fillColor : '#ff0000',
							fillOpacity : 0.2,
							strokeColor : '#ff0000',
							strokeWeight : 1,
							radius : item.fenceSize
						});

						map.drawCircle({
							lat : item.latitude,
							lng : item.longitude,
							fillColor : '#00ff00',
							fillOpacity : 0.2,
							strokeColor : '#00ff00',
							strokeWeight : 1,
							radius : item.checkinAreaSize
						});
					}
				}
			}
		}
	}
	map.removeMarkers();
	map.addMarkers(markers_data);
	circlesDrawed = true;
}

function rand() {
	return Math.random().toString(36).substr(2)
	+ Math.random().toString(36).substr(2)
	+ Math.random().toString(36).substr(2)
	+ Math.random().toString(36).substr(2);
}

