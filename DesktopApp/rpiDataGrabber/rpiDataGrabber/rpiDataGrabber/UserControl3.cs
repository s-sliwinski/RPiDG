﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using OxyPlot;
using OxyPlot.Axes;
using OxyPlot.WindowsForms;
using OxyPlot.Series;
using Newtonsoft.Json;
using System.IO;
using System.Timers;
using Timer = System.Timers.Timer;

namespace rpiDataGrabber
{
    public partial class UserControl3 : UserControl
    {  
        #region Fields        
        public PlotModel DataPlotModel { get; set; }
        private int timeStamp = 0;
        private Timer RequestTimer;
        private IoTServer Server;
        const string filePath = @"C:\Users\Klient\Documents\GitHub\RPiDG\DesktopApp\rpiDataGrabber\rpiDataGrabber\config.json";
        configData deserialized = JsonConvert.DeserializeObject<configData>(File.ReadAllText(filePath));
        public string buttonFlag;
        #endregion


        public UserControl3()
        {
            InitializeComponent();

            DataPlotModel = new PlotModel { Title = "Weather Timeline" };
            string ip = deserialized.ip.ToString();
            string port = deserialized.port.ToString();
            double maxSamples = deserialized.maxSamples;
            double sampleTime = deserialized.sampleTime;
            double XAxisMax = maxSamples * sampleTime / 1000.0;

            DataPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Bottom,
                Minimum = 0,
                Maximum = XAxisMax,
                Key = "Horizontal",
                Unit = "sec",
                Title = "Time"
            });
            DataPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
                Key = "Vertical",
                Unit = "-",
                Title = "-"
            });

            plotView1.Model=DataPlotModel;

            Server = new IoTServer(ip, port);
        }

        //temp
        private void button2_Click(object sender, EventArgs e)
        {

            if (RequestTimer != null)
            {
                RequestTimer.Enabled = false;
                RequestTimer = null;
            }

            if (RequestTimer == null)
            {
                double sampleTime = deserialized.sampleTime;
                buttonFlag = "Temperature";
                RequestTimer = new Timer(sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;
                DataPlotModel.Series.Clear();
                DataPlotModel.Series.Add(new LineSeries() { Title = "Temperature", Color = OxyColor.Parse("#7386D5") });
                DataPlotModel.ResetAllAxes();
            }


        }
        //humi
        private void button3_Click(object sender, EventArgs e)
        {

            if (RequestTimer != null)
            {
                RequestTimer.Enabled = false;
                RequestTimer = null;
            }

            if (RequestTimer == null)
            {
                double sampleTime = deserialized.sampleTime;
                buttonFlag = "Humidity";
                RequestTimer = new Timer(sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;
                DataPlotModel.Series.Clear();
                DataPlotModel.Series.Add(new LineSeries() { Title = "Humidity", Color = OxyColor.Parse("#7386D5") });
                DataPlotModel.ResetAllAxes();
            }

        }
        //press
        private void button4_Click(object sender, EventArgs e)
        {

            if (RequestTimer != null)
            {
                RequestTimer.Enabled = false;
                RequestTimer = null;
            }

            if (RequestTimer == null)
            {
                double sampleTime = deserialized.sampleTime;
                buttonFlag = "Pressure";
                RequestTimer = new Timer(sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;
                DataPlotModel.Series.Clear();
                DataPlotModel.Series.Add(new LineSeries() { Title = "Pressure", Color = OxyColor.Parse("#7386D5") });
                DataPlotModel.ResetAllAxes();
            }


        }

        private void UpdatePlot(double t, double d)
        {
            double maxSamples = deserialized.maxSamples;
            double sampleTime = deserialized.sampleTime;
            double XAxisMax = maxSamples * sampleTime / 1000.0;

            LineSeries lineSeries = DataPlotModel.Series[0] as LineSeries;

            lineSeries.Points.Add(new DataPoint(t, d));

            if (lineSeries.Points.Count > maxSamples)
                lineSeries.Points.RemoveAt(0);

            if (t >= XAxisMax)
            {
                DataPlotModel.Axes[0].Minimum = (t - XAxisMax);
                DataPlotModel.Axes[0].Maximum = t + sampleTime / 1000.0; ;
            }
            if (buttonFlag == "Temperature")
            {
                DataPlotModel.Axes[1].Unit = "C";
                DataPlotModel.Axes[1].Title = "Temperature";
            }
            if (buttonFlag == "Pressure")
            {
                DataPlotModel.Axes[1].Unit = "hPa";
                DataPlotModel.Axes[1].Title = "Pressure";
            }
            if (buttonFlag == "Humidity")
            {
                DataPlotModel.Axes[1].Unit = "%";
                DataPlotModel.Axes[1].Title = "Humidity";
            }

            DataPlotModel.InvalidatePlot(true);
        }

        private async void UpdatePlotWithServerResponse()
        {
            string responseText = await Server.GETwithClient();
            ServerData resposneJson = JsonConvert.DeserializeObject<ServerData>(responseText);
            if (buttonFlag == "Temperature")
            {
                UpdatePlot(timeStamp / 1000.0, resposneJson.temperature);
            }
            if (buttonFlag == "Pressure")
            {
                UpdatePlot(timeStamp / 1000.0, resposneJson.pressure);
            }
            if (buttonFlag == "Humidity")
            {
                UpdatePlot(timeStamp / 1000.0, resposneJson.humidity);
            }

            timeStamp += deserialized.sampleTime;
        }

        private void RequestTimerElapsed(object sender, ElapsedEventArgs e)
        {
            UpdatePlotWithServerResponse();
        }

        #region PropertyChanged

        public event PropertyChangedEventHandler PropertyChanged;

        /**
         * @brief Simple function to trigger event handler
         * @params propertyName Name of ViewModel property as string
         */
        protected void OnPropertyChanged(string propertyName)
        {
            PropertyChangedEventHandler handler = PropertyChanged;
            if (handler != null) handler(this, new PropertyChangedEventArgs(propertyName));
        }



        #endregion

    }
}
