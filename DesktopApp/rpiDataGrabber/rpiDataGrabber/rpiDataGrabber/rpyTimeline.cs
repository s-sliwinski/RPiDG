using System;
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
    public partial class rpyTimeline : UserControl
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

        public rpyTimeline()
        {
            InitializeComponent();

            DataPlotModel = new PlotModel { Title = "RPY Timeline" };
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

            plotView1.Model = DataPlotModel;


            Server = new IoTServer(ip, port);
        }
        //roll
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
                buttonFlag = "Roll";
                RequestTimer = new Timer(sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;
                DataPlotModel.Series.Clear();
                DataPlotModel.Series.Add(new LineSeries() { Title = "Roll", Color = OxyColor.Parse("#7386D5") });
                DataPlotModel.ResetAllAxes();
            }


        }
        //pitch
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
                buttonFlag = "Pitch";
                RequestTimer = new Timer(sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;
                DataPlotModel.Series.Clear();
                DataPlotModel.Series.Add(new LineSeries() { Title = "Pitch", Color = OxyColor.Parse("#7386D5") });
                DataPlotModel.ResetAllAxes();
            }


        }
        //yaw
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
                buttonFlag = "Yaw";
                RequestTimer = new Timer(sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;
                DataPlotModel.Series.Clear();
                DataPlotModel.Series.Add(new LineSeries() { Title = "Yaw", Color = OxyColor.Parse("#7386D5") });
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
            if (buttonFlag == "Roll")
            {
                DataPlotModel.Axes[1].Unit = "DGR";
                DataPlotModel.Axes[1].Title = "Roll";
            }
            if (buttonFlag == "Pitch")
            {
                DataPlotModel.Axes[1].Unit = "DGR";
                DataPlotModel.Axes[1].Title = "Pitch";
            }
            if (buttonFlag == "Yaw")
            {
                DataPlotModel.Axes[1].Unit = "DGR";
                DataPlotModel.Axes[1].Title = "Yaw";
            }

            DataPlotModel.InvalidatePlot(true);
        }

        private async void UpdatePlotWithServerResponse()
        {
            string responseText = await Server.GETwithClient();
            ServerData resposneJson = JsonConvert.DeserializeObject<ServerData>(responseText);
            if (buttonFlag == "Roll")
            {
                UpdatePlot(timeStamp / 1000.0, resposneJson.roll);
            }
            if (buttonFlag == "Pitch")
            {
                UpdatePlot(timeStamp / 1000.0, resposneJson.pitch);
            }
            if (buttonFlag == "Yaw")
            {
                UpdatePlot(timeStamp / 1000.0, resposneJson.yaw);
            }

            timeStamp += deserialized.sampleTime;
        }

        private void RequestTimerElapsed(object sender, ElapsedEventArgs e)
        {
            UpdatePlotWithServerResponse();
        }


        private void StartTimer()
        {
            if (RequestTimer == null)
            {
                RequestTimer = new Timer(deserialized.sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;

                if (DataPlotModel == null)
                {
                    PlotModel DataPlotModel = new PlotModel { Title = "RPY Timeline" };
                    DataPlotModel.Axes.Add(new LinearAxis()
                    {
                        Position = AxisPosition.Bottom,
                        Minimum = 0,
                        Maximum = deserialized.maxSamples,
                        Key = "Horizontal",
                        Unit = "sec",
                        Title = "Time"
                    });


                    DataPlotModel.Axes.Add(new LinearAxis()
                    {
                        Position = AxisPosition.Left,
                        Key = "Vertical",
                        Unit = "",
                        Title = ""
                    });

                    //DataPlotModel.Series.Add(new LineSeries() { Title = "", Color = OxyColor.Parse("#FFFF0000") });

                    plotView1.Model = DataPlotModel;
                    DataPlotModel.ResetAllAxes();
                }
                else DataPlotModel.ResetAllAxes();
            }
        }

        /**
         * @brief RequestTimer stop procedure.
         */
        private void StopTimer()
        {
            if (RequestTimer != null)
            {
                RequestTimer.Enabled = false;
                RequestTimer = null;
            }
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
