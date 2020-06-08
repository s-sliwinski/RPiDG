var ip;
var port;
var stime;
var maxsamples;
var dplaces;
const url = 'Access-Control-Allow-Origin: http://192.168.1.15/cgi-bin/config.py';

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


    $.ajax({
        url: url,
        type: "POST",
        dataType: "json",
        data: jsonStr,
        contentType: 'application/json;charset=UTF-8',
        success: function (response){
            alert(response.message);
            alert(response.keys);
        },
        error: function (xhr,status,error){
            alert(error.message);
            alert(error.response);
        }

    });
};

window.onload = function (){
    $('#submitbtn').click(subFunction)
}
