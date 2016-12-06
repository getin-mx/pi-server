var initialized = false;

$(document).ready(function() {

	$("input").uniform();
	$('#viewType').change(function() {
		refreshDatatable();
	});
	refreshDatatable();

});

function refreshDatatable() {
//	var viewType = $("#viewType").val();
//	var ajaxSource = viewType == "all" ? "getOfferRecords" : "getActiveOfferRecords"; 
	var ajaxSource = "getCampaignSpecialRecords"; 
	var config = $.extend( dataTableDefaults, {
		"sAjaxSource": ajaxSource,
		"bDestroy": true,
		"aoColumns" : [
		               {"sWidth" : "30px", "bSortable": false},
		               {"sWidth" : "25%", "bSortable": false},
		               {"sWidth" : "16%", "bSortable": false},
		               {"sWidth" : "10%", "bSortable": false},
		               {"sWidth" : "10%", "bSortable": false},
		               {"sWidth" : "7%", "bSortable": false},
		               {"sWidth" : "7%", "bSortable": false},
		               {"sWidth" : "7%", "bSortable": false},
		               {"sWidth" : "5%", "bSortable": false},
		               ]
	});
	if( initialized ) {
		var oTable = $('#offerRecords').dataTable();
		var newconfig = oTable.fnSettings(null);
		newconfig.sAjaxSource = ajaxSource;
		oTable.fnSetSettings(null, newconfig);
		oTable.fnClearTable();
		$('#campaignSpecialRecords').dataTable(config);
	} else {
		$('#campaignSpecialRecords').dataTable(config);
		initialized = true;
	}
	
}
