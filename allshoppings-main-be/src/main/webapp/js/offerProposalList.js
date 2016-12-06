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
	var ajaxSource = "getOfferProposalRecords"; 
	var config = $.extend( dataTableDefaults, {
		"sAjaxSource": ajaxSource,
		"bDestroy": true,
		"aoColumns" : [
		               {"sWidth" : "80%", "bSortable": false},
		               {"sWidth" : "7%", "bSortable": false},
		               {"sWidth" : "7%", "bSortable": false},
		               {"sWidth" : "5%", "bSortable": false},
		               ]
	});
	if( initialized ) {
		var oTable = $('#offerProposalRecords').dataTable();
		var newconfig = oTable.fnSettings(null);
		newconfig.sAjaxSource = ajaxSource;
		oTable.fnSetSettings(null, newconfig);
		oTable.fnClearTable();
		$('#offerProposalRecords').dataTable(config);
	} else {
		$('#offerProposalRecords').dataTable(config);
		initialized = true;
	}
	
}
