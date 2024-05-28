
function setup() {
//        console.log("Kai: " + window.document.documentElement.clientHeight);
        console.log("Kai: " + window.lastPos);

        if(lastPos!=0){
        console.log("Kai: " + lastPos);
        window.scrollTo(0, lastPos);
        }
}
