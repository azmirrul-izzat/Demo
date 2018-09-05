"use strict";

use(function () {
    var props = granite.resource.properties;
    
    var mgnCls = [];
    
    var mapping = {
		"dmargintop": "m-md-top-",
		"dmarginbottom": "m-md-bottom-",
		"tmargintop": "m-sm-top-",
		"tmarginbottom": "m-sm-bottom-",
		"mmargintop": "m-top-",
		"mmarginbottom": "m-bottom-",
        "cssClass": ""
    }
    
    for(var key in mapping){
    	var val = props[key];
    	if(val){
    		mgnCls.push(mapping[key]+val);
    	}
    }    
    
    return { mgnCls:mgnCls };
    
});
