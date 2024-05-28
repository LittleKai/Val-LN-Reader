
function setup() {
//    console.log("Kai: " + $(window).height());
//        console.log("Kai: " + window.document.documentElement.clientHeight);
        lastPos = window.document.documentElement.clientHeight * ratio;
        window.scrollTo(0, lastPos);
//        console.log("Kai: " + lastPos);
}
