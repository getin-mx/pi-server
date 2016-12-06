var initialized = false;

$(document).ready(function() {

	$("input").uniform();
	$('#viewType').change(function() {
		refreshDatatable();
	});
	refreshDatatable();

});

function refreshDatatable() {
	var viewType = $("#viewType").val();
	var ajaxSource = viewType == "all" ? "getRewardRecords" : "getActiveRewardRecords"; 
	var config = $.extend( dataTableDefaults, {
		"sAjaxSource": ajaxSource,
		"bDestroy": true,
		"aoColumns" : [
		               {"sWidth" : "25%"},
		               {"sWidth" : "10%", "bSortable": true},
		               {"sWidth" : "10%", "bSortable": true},
		               {"sWidth" : "7%", "bSortable": true},
		               {"sWidth" : "7%", "bSortable": true},
		               {"sWidth" : "7%", "bSortable": true},
		               {"sWidth" : "5%", "bSortable": false},
		               ]
	});
	if( initialized ) {
		var oTable = $('#rewardRecords').dataTable();
		var newconfig = oTable.fnSettings(null);
		newconfig.sAjaxSource = ajaxSource;
		oTable.fnSetSettings(null, newconfig);
		oTable.fnClearTable();
		$('#rewardRecords').dataTable(config);
	} else {
		$('#rewardRecords').dataTable(config);
		initialized = true;
	}
	
}
