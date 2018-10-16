"use strict";

/**
 * 
 */
use(["/apps/apddemo/widgets/utils/MarginUtils.js",'/apps/apddemo/widgets/utils/utils.js'], function (MarginUtils, utils) {

    var c1padding = utils.getPaddingClasses(
        granite.resource.properties['dc1hidepadding'],
        granite.resource.properties['tc1hidepadding'],
        granite.resource.properties['mc1hidepadding']
    ), c2padding = utils.getPaddingClasses(
        granite.resource.properties['dc2hidepadding'],
        granite.resource.properties['tc2hidepadding'],
        granite.resource.properties['mc2hidepadding']
    ), rowpadding = utils.getRowPaddingClasses(
        granite.resource.properties['dc1hidepadding'],
        granite.resource.properties['tc1hidepadding'],
        granite.resource.properties['mc1hidepadding'],
        granite.resource.properties['dc2hidepadding'],
        granite.resource.properties['tc2hidepadding'],
        granite.resource.properties['mc2hidepadding'],
        false,
        false,
        false
    )
    var tabColWidth1="", tabColWidth2="" ;
    var mobColWidth1="", mobColWidth2="";
    // tabs hide or arranging

    if( granite.resource.properties['tcolumn1width'] == "hide"){

		tabColWidth1 = "hidden-sm";
    }else{
		tabColWidth1 = "col-sm-"+granite.resource.properties['tcolumn1width'];
    }
    if( granite.resource.properties['tcolumn2width'] == "hide"){
		tabColWidth2 = "hidden-sm";
    }else{
		tabColWidth2 = "col-sm-"+granite.resource.properties['tcolumn2width'];
    }
	// mobile hide or arranging
    if( granite.resource.properties['mcolumn1width'] == "hide"){

		mobColWidth1 = "hidden-xs";
    }else{
		mobColWidth1 = "col-xs-"+granite.resource.properties['mcolumn1width'];
    }
    if( granite.resource.properties['mcolumn2width'] == "hide"){
 
		mobColWidth2 = "hidden-xs";
    }else{
		mobColWidth2 = "col-xs-"+granite.resource.properties['mcolumn2width'];
    }

    return {
    	mgnCls: MarginUtils.mgnCls.join(" "),
        c1padding: c1padding,
        c2padding: c2padding,
        tabColWidth1: tabColWidth1,
        tabColWidth2: tabColWidth2,
        mobColWidth1: mobColWidth1,
        mobColWidth2: mobColWidth2,
        rowpadding: rowpadding
    };
    
});
