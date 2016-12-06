var changed = false;
var map;
var circlesDrawed = false;

$(document).ready(function() {
	var $body = $('body'),
	$content = $('#content'),
	$mapForm = $content.find('#mapForm');

	$("input").uniform();
	$("input").bind("change", function () {
		changed = true;
	});

	$mapForm.wl_Form({
		ajax:true,
		status:false,
		confirmSend: false,
		onBeforeSubmit: function(data){
			$mapForm.wl_Form('set','sent',false);
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


	map = new GMaps({
		div: '#map',
		lat: 0,
		lng: 0,
		width: "100%",
		height: "550px"
	});

	map.setZoom(1);

	var xhr = $.getJSON('/main-be/mapResources');
	xhr.done(loadResults);

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
								content: '<p>' + item.userid + '<br/><strong>' + item.username + '</strong><br/>' + item.lastupdate + '</p>'
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

					} else if( item.kind == 'store' ) {
						var icon = '/main-be/css/images/icon_shopping.png';
						markers_data.push({
							lat : item.latitude,
							lng : item.longitude,
							title : item.name,
							infoWindow: {
								content: '<p><strong><a href="/main-be/store/' + item.identifier + '">' + item.name + '</a></p>'
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
								fillColor : '#0000ff',
								fillOpacity : 0.2,
								strokeColor : '#0000ff',
								strokeWeight : 1,
								radius : item.fenceSize
							});

							map.drawCircle({
								lat : item.latitude,
								lng : item.longitude,
								fillColor : '#0000ff',
								fillOpacity : 0.2,
								strokeColor : '#0000ff',
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

		$('#totalActiveUsersCount')[0].innerHTML = data.totalActiveUsersCount;
		$('#totalDevices')[0].innerHTML = data.totalDevices;
		$('#totalShoppings')[0].innerHTML = data.totalShoppings;
		$('#totalStreetStores')[0].innerHTML = data.totalStreetStores;

		$.msg($.i18n._('app.refreshedData'),{header:$.i18n._('js.wl.alert.header')});
	}

	function rand() {
		return Math.random().toString(36).substr(2)
		+ Math.random().toString(36).substr(2)
		+ Math.random().toString(36).substr(2)
		+ Math.random().toString(36).substr(2);
	}

	setInterval(function() {
		var xhr = $.getJSON('/main-be/mapResources?datetime='+rand());
		xhr.done(loadResults);
	}, 30000);

	$('a.fancybox').fancybox();
});

