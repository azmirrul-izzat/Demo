"use strict";
use(['/apps/usgb/widgets/utils/utils.js'], function (utils) {
    var contactinfoproperties = {};

    contactinfoproperties['columndesktop'] = 12 / (granite.resource.properties['columndesktop'] || 1);
    contactinfoproperties['columntablet'] = 12 / (granite.resource.properties['columntablet'] || 1);
    contactinfoproperties['columnmobile'] = 12 / (granite.resource.properties['columnmobile'] || 1);

    contactinfoproperties['filterposition'] = 'right';

    return contactinfoproperties; 
}); 