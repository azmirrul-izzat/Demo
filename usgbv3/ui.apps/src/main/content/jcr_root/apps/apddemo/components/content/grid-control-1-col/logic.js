"use strict";

/**
 * 
 */
use(["/apps/apddemo/widgets/utils/MarginUtils.js",'/apps/apddemo/widgets/utils/utils.js'], function (MarginUtils, utils) {

    var padding = utils.getPaddingClasses(
        granite.resource.properties['dhidepadding'],
        granite.resource.properties['thidepadding'],
        granite.resource.properties['mhidepadding']
    ), rowpadding = utils.getRowPaddingClasses(
        granite.resource.properties['dhidepadding'],
        granite.resource.properties['thidepadding'],
        granite.resource.properties['mhidepadding'],
        false,
        false,
        false,
        false,
        false,
        false
    );

    return {
    	mgnCls: MarginUtils.mgnCls.join(" "),
        padding: padding,
        rowpadding: rowpadding
    };
    
});
/*
	Lorem Ipsum
*/