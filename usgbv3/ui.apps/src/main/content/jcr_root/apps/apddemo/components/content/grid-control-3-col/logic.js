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
    ), c3padding = utils.getPaddingClasses(
        granite.resource.properties['dc3hidepadding'],
        granite.resource.properties['tc3hidepadding'],
        granite.resource.properties['mc3hidepadding']
    ), rowpadding = utils.getRowPaddingClasses(
        granite.resource.properties['dc1hidepadding'],
        granite.resource.properties['tc1hidepadding'],
        granite.resource.properties['mc1hidepadding'],
        granite.resource.properties['dc2hidepadding'],
        granite.resource.properties['tc2hidepadding'],
        granite.resource.properties['mc2hidepadding'],
        granite.resource.properties['dc3hidepadding'],
        granite.resource.properties['tc3hidepadding'],
        granite.resource.properties['mc3hidepadding']
    )

    return {
    	mgnCls: MarginUtils.mgnCls.join(" "),
        c1padding: c1padding,
        c2padding: c2padding,
        c3padding: c3padding,
        rowpadding: rowpadding
    };
    
});
