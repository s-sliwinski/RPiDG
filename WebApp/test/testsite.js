const url = 'Access-Control-Allow-Origin: http://192.168.1.15/cgi-bin/hello.py';

function sendReq(){
    $.get(url, function(data) {
   // Get JSON data from Python script
   if (data){
      console.log("Data returned:", data)
   }
   jobDataJSON = JSON.parse(data)
})
}
window.onload = function (){
    $('#submitbtn').click(sendReq)
}
