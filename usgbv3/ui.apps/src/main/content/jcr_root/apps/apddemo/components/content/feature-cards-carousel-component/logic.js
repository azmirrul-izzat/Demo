"use strict";
use(['/apps/apddemo/widgets/utils/utils.js', '/apps/apddemo/widgets/utils/ColorPickerUtil.js'], function (utils, ColorPickerUtil) {
    var featureproperties = {};
  //  featureproperties.divID = this.path;
    if(this.path != undefined){
         featureproperties.divID = "fp-" + utils.getHashCode(this.path);
        //featureproperties.divID =this.nodePath;
    }
   
    
    var color = this.color;
    var CONST = {
        COLOR_MAPPING_DESCRIPTION: "/cq:dialog/content/items/basic/items/column/items/leftlinks/field/items/column/items/descriptionfont-color/mappings"
    };

    // getting the header color class using colorpickerutils
    if(color != undefined && color != null){
       featureproperties.fontColorDescription = ColorPickerUtil.getFontColor(component.getPath() + CONST.COLOR_MAPPING_DESCRIPTION, color);
        
    }
   // log.info("fontColorDescription final::"+sbproperties.fontColorDescription);
    return featureproperties; 
});



