

$(document).ready(function() {
    var jsonResult;
    var table;
	retrievePageInfo("/content/demo/en_au");

	$('.countryDropdown').on('change', function() {
        retrievePageInfo(this.value);
    })


} );

function retrievePageInfo(countryPath){

    $.ajax({
		url: '/bin/usgb/searchPageService?countryPath='+countryPath
	})
	.done(function(resp) {
        //console.log('success = ', resp);
        console.log('header = ', resp.header);
        console.log('properties = ', resp.properties);
        buildTable(resp);
        implementDataTable();


	})
	.fail(function(resp) {
		console.error('Ajax failed.');
		console.log('resp = ', resp);
	});
}

function buildTable(tableInfo){
	jsonResult = tableInfo;

    var headerList = tableInfo.header;
    var propertiesList = tableInfo.properties;

    //Generate Table Header START
    var tableHeader = "";
    var openheaderTable = "<thead><tr>";
    var contentheaderTable = "";
    var closeheaderTable = "</tr></thead>";

    contentheaderTable += "<th>Path</th>";
    for (var i in headerList) {

        contentheaderTable += "<th>" + formatHeader(headerList[i]) + "</th>";
    }

	tableHeader += openheaderTable + contentheaderTable + closeheaderTable;
	//Generate Table Header END

    //Generate Table Body START 
    var bodyTable = "";
    var openbodyTable = "<tbody>";
    var contentbodyTable = "";
    var closebodyTable = "</tbody>";

    for (var i in propertiesList) {

		contentbodyTable += "<tr>";
		contentbodyTable += "<td>" + propertiesList[i]["path"] + "</td>";

        for (var j in headerList) {

			contentbodyTable += "<td>" + (propertiesList[i][headerList[j]] ? propertiesList[i][headerList[j]] : "&nbsp") + "</td>";
        }
        contentbodyTable += "</tr>";
    }

    bodyTable += openbodyTable + contentbodyTable + closebodyTable;
	//Generate Table Body START 

    //Generate Full Table START
    var completeTable = "";
    var headercompleteTable = "<table id='example' class='stripe row-border order-column' cellspacing='0' width='100%'>";
    var closecompleteTable = "</table>";
	completeTable += headercompleteTable + tableHeader + bodyTable + closecompleteTable;

	$('#countryTable').html(completeTable);
}

function formatHeader(str){

    var newString = "";

    if(str.indexOf(":") > -1){

        var res = str.split(":");
        newString = "(" + res[0] + ") " + toTitleCase(res[1]);;

    }else{

		newString = toTitleCase(str);
    }

    return newString;
}

function toTitleCase(str)
{
    return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
}


function implementDataTable(){


    table = $('#example').DataTable( {
        dom:            "Bfrtip",
        scrollY:        true,
        scrollX:        true,
        scrollCollapse: true,
        paging:         true,
        pageLength:     25,
        fixedColumns:	true,
        fixedHeaders: 	true,
        buttons:        [ 'colvis', 'copy', 'csv', 'excel']
    } );
}