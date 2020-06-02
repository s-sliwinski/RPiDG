var ip;
var port;
var stime;
var maxsamples;
var dplaces;

function subFunction(){
        ip = document.getElementById("ip").value;
        port = document.getElementById("port").value;
        stime = document.getElementById("stime").value;
        maxsamples = document.getElementById("maxsamples").value;
        dplaces = document.getElementById("dplaces").value;
        sendToServer()

};

function sendToServer(){
    var jsonObj = {ip: ip, port: port, stime: stime, maxsamples: maxsamples, dplaces: dplaces};
    var jsonStr = JSON.stringify(jsonObj);
    console.log(jsonStr);
    console.log(jsonObj);
}
