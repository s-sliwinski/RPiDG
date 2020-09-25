const columnsNumber = 8;
const rowNumber = 8;
var btnHolder = [];
var currentColor = [0,0,0]
const url = "/WebApp/scripts/led_display.php"
function generateBtns(){
    for(let i = 0; i < rowNumber; i++){
        for(let j = 0; j < columnsNumber; j++){
            var btn = document.createElement("button");
            var btnId = [i,j,0,0,0]
            btn.setAttribute("id", btnId);
            btn.onclick = function (){
                changeColor(this.id);
                sendToServer(this.id);
            }
            document.getElementById('button-holder').appendChild(btn);
            btnHolder.push(btnId);
        }
    }
}
function changeColor(str){
    clickedBtn = document.getElementById(str)
    var newId = (str.substr(0,4) + currentColor[0] + ',' + currentColor[1] + ',' + currentColor[2]);
    clickedBtn.setAttribute("id", newId);
    clickedBtn.style.backgroundColor  = ('rgb(' + currentColor[0] + ',' + currentColor[1] + ',' + currentColor[2] + ')');

//FUUUUJ
    for(let i = 0; i<columnsNumber*rowNumber; i++){
        if(btnHolder[i][0] == newId[0] && btnHolder[i][1] == newId[2]){
            btnHolder[i][2] = currentColor[0];
            btnHolder[i][3] = currentColor[1];
            btnHolder[i][4] = currentColor[2];
        }
    }
}
function sendToServer(str){
    console.log(str)
    console.log(btnHolder)
    var jsonObj = btnHolder
    console.log(jsonObj);
    var jsonStr = (JSON.stringify(jsonObj));
    console.log(jsonStr);
    $.ajax({
        url: url,
        data: {data: jsonStr},
        type: 'POST',
        success: function(response) {
	           console.log('success');
           },
           error: function(response) {
               alert(response);
               console.log('fail');
           }

       });

}
function pickColor(){
    var hex = document.getElementById("color-picker").value
    console.log(hex);
    var red = parseInt(hex.substr(1,2) , 16)
    var green = parseInt(hex.substr(3,2) , 16)
    var blue = parseInt(hex.substr(5,2) , 16)
    console.log(red,green,blue)
    currentColor[0] = red;
    currentColor[1] = green;
    currentColor[2] = blue;
    console.log(currentColor)
}
function resetLeds(){
    buttons = document.getElementById("button-holder");
    console.log(buttons.childNodes[1])
    for(let i = 0; i<columnsNumber*rowNumber; i++){
        buttons.childNodes[i+1].style.background = "rgb(255,255,255)"
        btnHolder[i][2] = 0;
        btnHolder[i][3] = 0;
        btnHolder[i][4] = 0;
    }
    sendToServer()
}
$(document).ready(function (){
    generateBtns();
    $('#reset-button').click(resetLeds);
});
