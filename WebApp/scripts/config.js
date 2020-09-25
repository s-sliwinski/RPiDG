var ip;
var port;
var stime;
var maxsamples;
var dplaces;
const url = 'http://192.168.1.15/WebApp/scripts/config.php';

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
   data: {data: jsonStr},
   type: 'POST',
   success: function(response) {
      alert(response);
	  console.log('success');
   },
   error: function(response) {
      alert(response);
      console.log('fail');
   }
   
});
	
};

window.onload = function (){
    $('#submitbtn').click(subFunction)
}
