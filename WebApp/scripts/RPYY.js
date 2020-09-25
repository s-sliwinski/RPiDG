
var sampleTimeSec = 0.1; 					///< sample time in sec
var sampleTimeMsec = 1000*sampleTimeSec;	///< sample time in msec
var maxSamplesNumber = 100;				///< maximum number of samples

var lastTimeStamp; ///< most recent time stamp
var xdata; ///< x-axis labels array: time stamps
var ydata; ///< y-axis data array: random value

var xdata2; ///< x-axis labels array: time stamps
var ydata2; ///< y-axis data array: random value

var xdata3; ///< x-axis labels array: time stamps
var ydat3a; ///< y-axis data array: random value

var chartContext;  ///< chart context i.e. object that "owns" chart
var chart; ///< Chart.js object

var chartContext2;  ///< chart context i.e. object that "owns" chart
var chart2; ///< Chart.js object

var chartContext3;  ///< chart context i.e. object that "owns" chart
var chart3; ///< Chart.js object

var timer;
var timer2;
var timer3;

const  url ='http://192.168.1.15/ServerScripts/chartdata.json'; ///< server app with JSON API
const curl ='scripts/config.json';
/**
* @brief Add new value to next data point.
* @param y New y-axis value
*/
getConfig()
function getConfig(){
	/// Send request
	$.ajax(
	{
		url: curl,
		type: 'GET',
		dataType: 'json',
		/// success callback
		success: function(responseJSON, status, xhr) {
			console.log(JSON.stringify(responseJSON));
			ip = responseJSON.ip;
			port = responseJSON.port;
			maxSamplesNumber = responseJSON.maxsamples;
			sampleTimeSec = responseJSON.stime;
			console.log(ip,port,maxSamplesNumber,sampleTimeSec);

		},
		/// error callback
		error: function(response){
		alert(response)}
	});
}
function addData(y){
	if(ydata.length > maxSamplesNumber)
	{
		removeOldData();
		lastTimeStamp += sampleTimeSec;
		xdata.push(lastTimeStamp.toFixed(1));
	}
	ydata.push(y);
	chart.update();
}
function addData2(y){
	if(ydata2.length > maxSamplesNumber)
	{
		removeOldData2();
		lastTimeStamp += sampleTimeSec;
		xdata2.push(lastTimeStamp.toFixed(1));
	}
	ydata2.push(y);
	chart2.update();
}
function addData3(y){
	if(ydata3.length > maxSamplesNumber)
	{
		removeOldData3();
		lastTimeStamp += sampleTimeSec;
		xdata3.push(lastTimeStamp.toFixed(1));
	}
	ydata3.push(y);
	chart3.update();
}

/**
* @brief Remove oldest data point.
*/
function removeOldData(){
	xdata.splice(1,1);
	ydata.splice(1,1);
}
function removeOldData2(){
	xdata2.splice(0,1);
	ydata2.splice(0,1);
}
function removeOldData3(){
	xdata3.splice(0,1);
	ydata3.splice(0,1);
}

function startTimer(){
	timer = setInterval(ajaxJSON, sampleTimeMsec);
}
function startTimer2(){
	timer2 = setInterval(ajaxJSON2, sampleTimeMsec);
}
function startTimer3(){
	timer3 = setInterval(ajaxJSON3, sampleTimeMsec);
}


function stopTimer(){
	clearInterval(timer);
}
function stopTimer2(){
	clearInterval(timer2);
}
function stopTimer3(){
	clearInterval(timer3);
}
/**
* @brief Send HTTP GET request to IoT server
*/
function ajaxJSON() {
	$.ajax(url, {
		type: 'GET', dataType: 'json',
		success: function(responseJSON, status, xhr) {
			addData(+responseJSON.roll);

		}
	});
}
function ajaxJSON2() {
	$.ajax(url, {
		type: 'GET', dataType: 'json',
		success: function(responseJSON, status, xhr) {
			addData2(+responseJSON.pitch);

		}
	});
}
function ajaxJSON3() {
	$.ajax(url, {
		type: 'GET', dataType: 'json',
		success: function(responseJSON, status, xhr) {
			addData3(+responseJSON.yaw);

		}
	});
}
/**
* @brief Chart initialization
*/
function chartInit()
{
	// array with consecutive integers: <0, maxSamplesNumber-1>
	xdata = [...Array(maxSamplesNumber).keys()];
	// scaling all values ​​times the sample time
	xdata.forEach(function(p, i) {this[i] = (this[i]*sampleTimeSec).toFixed(1);}, xdata);

	// last value of 'xdata'
	lastTimeStamp = +xdata[xdata.length-1];

	// empty array
	ydata = [];

	// get chart context from 'canvas' element
	chartContext = $("#chart")[0].getContext('2d');

	chart = new Chart(chartContext, {
		// The type of chart: linear plot
		type: 'line',

		// Dataset: 'xdata' as labels, 'ydata' as dataset.data
		data: {
			labels: xdata,
			datasets: [{
				fill: false,
				label: 'Roll',
				backgroundColor: 'rgb(255, 0, 0)',
				borderColor: 'rgb(255, 0, 0)',
				data: ydata,
				lineTension: 0
			}]
		},

		// Configuration options
		options: {
			responsive: true,
			maintainAspectRatio: false,
			animation: false,
			scales: {
				yAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Roll [°]'
					}
				}],
				xAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});

	ydata = chart.data.datasets[0].data;
	xdata = chart.data.labels;
}

function chartInit2()
{
	xdata2 = [...Array(maxSamplesNumber).keys()];
	xdata2.forEach(function(p, i) {this[i] = (this[i]*sampleTimeSec).toFixed(1);}, xdata2);
	lastTimeStamp = +xdata2[xdata2.length-1];
	ydata2 = [];
	chartContext2 = $("#chart2")[0].getContext('2d');
	chart2 = new Chart(chartContext2, {
		type: 'line',
		data: {
			labels: xdata2,
			datasets: [{
				fill: false,
				label: 'Pitch',
				backgroundColor: 'rgb(0, 255, 0)',
				borderColor: 'rgb(0, 255, 0)',
				data: ydata2,
				lineTension: 0
			}]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			animation: false,
			scales: {
				yAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Pitch [°]'
					}
				}],
				xAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});

	ydata2 = chart2.data.datasets[0].data;
	xdata2 = chart2.data.labels;
}

function chartInit3()
{
	xdata3 = [...Array(maxSamplesNumber).keys()];
	xdata3.forEach(function(p, i) {this[i] = (this[i]*sampleTimeSec).toFixed(1);}, xdata3);
	lastTimeStamp = +xdata3[xdata3.length-1];
	ydata3 = [];
	chartContext3 = $("#chart3")[0].getContext('2d');
	chart3 = new Chart(chartContext3, {
		type: 'line',
		data: {
			labels: xdata3,
			datasets: [{
				fill: false,
				label: 'Yaw',
				backgroundColor: 'rgb(0, 0, 255)',
				borderColor: 'rgb(0, 0, 255)',
				data: ydata3,
				lineTension: 0
			}]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			animation: false,
			scales: {
				yAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Yaw [°]'
					}
				}],
				xAxes: [{
					scaleLabel: {
						display: true,
						labelString: 'Time [s]'
					}
				}]
			}
		}
	});

	ydata3 = chart3.data.datasets[0].data;
	xdata3 = chart3.data.labels;
}

$(document).ready(() => {

	chartInit();
	chartInit2();
	chartInit3();

	$("#start").click(startTimer);
	$("#stop").click(stopTimer);

	$("#start2").click(startTimer2);
	$("#stop2").click(stopTimer2);

	$("#start3").click(startTimer3);
	$("#stop3").click(stopTimer3);

	$("#sampletime").text(sampleTimeMsec.toString());
	$("#samplenumber").text(maxSamplesNumber.toString());
	$("#sampletime2").text(sampleTimeMsec.toString());
	$("#samplenumber2").text(maxSamplesNumber.toString());
	$("#sampletime3").text(sampleTimeMsec.toString());
	$("#samplenumber3").text(maxSamplesNumber.toString());
});
