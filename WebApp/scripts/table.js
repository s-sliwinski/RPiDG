const url = 'http://192.168.1.15/ServerScripts/chartdata.json';
const curl = 'scripts/config.json'
var timer;
function getConfig(){
    $.ajax({
        url : curl,
		type: 'GET',
        dataType: 'json',
		success: function(responseJSON, status, xhr) {
			console.log(responseJSON.dplaces);

		},
        error: function(){
            console.log('error')
        }
	});
}
function getData(){
    $.ajax({
        url : url,
		type: 'GET',
        dataType: 'json',
		success: function(responseJSON, status, xhr) {
			console.log(responseJSON);
            var len = Object.keys(responseJSON).length;
            generateTable(responseJSON, len)
		},
        error: function(){
            console.log('error')
        }
	});
}
function generateTable(responseJSON, len){
    var tableH = document.getElementById('table-holder');
    tableH.removeChild(tableH.childNodes[0])
    tableC = document.createElement('div')
    tableC.setAttribute("id", 'table-content')
    tableH.appendChild(tableC)
    for(key in responseJSON){
        if(responseJSON[key] != 0){

            var tab = document.createElement("TR");
            tab.innerHTML = "<h4>" + key + "</h>" + ": "  + responseJSON[key].toFixed(2);
            document.getElementById('table-content').appendChild(tab);

        }
    }
}
function startTimer(){
	timer = setInterval(getData, 100);
}
function stopTimer(){
	clearInterval(timer);
}
$(document).ready(function (){
    $("#start").click(startTimer);
    $("#stop").click(stopTimer);

});
