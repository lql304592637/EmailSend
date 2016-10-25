/*
console.log('Hello, world!');
phantom.exit();
*/

var system = require('system');
var targetPage;
var pageWidth;
var pageHeight=1600;
if (system.args.length === 1) {
    console.log('Try to pass some args when invoking this script!');
} else {
    system.args.forEach(function(arg, i) {
        console.log(i + ': ' + arg);
    });
    targetPage=system.args[1];
    fileName=system.args[2];
    pageWidth=system.args[3];
    if(system.args.length>=5){
        pageHeight = system.args[4]
    }
}

var page = require('webpage').create();
page.viewportSize = { width: pageWidth,height:pageHeight };
page.customHeaders = {
  "Accept-Language": "zh-cn,en-DE,en;q=0.5"
};

page.open(targetPage, function(status) {
    console.log("Status: " + status);
    if (status === "success") {
        page.evaluate(function() {
            if(document.getElementsByClassName("col-8").length>0)
            {
                document.getElementsByTagName("body")[0].setAttribute('class',document.getElementsByTagName("body")[0].getAttribute('class')+" phantomjs");
            }
        });


        window.setTimeout(function() {
            console.log('timeout');

            page.render(fileName,{quality:90});
            phantom.exit();

        }, 10000);

    } else {
        phantom.exit();

    }
});