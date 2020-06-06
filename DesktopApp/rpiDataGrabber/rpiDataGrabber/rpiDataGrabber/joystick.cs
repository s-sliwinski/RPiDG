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
    public partial class joystick : UserControl
    {
        #region Fields        
        public PlotModel DataPlotModel { get; set; }
        private int timeStamp = 0;
        private Timer RequestTimer;
        private IoTServer Server;
        const string filePath = @"C:\Users\Klient\Documents\GitHub\RPiDG\DesktopApp\rpiDataGrabber\rpiDataGrabber\config.json";
        configData deserialized = JsonConvert.DeserializeObject<configData>(File.ReadAllText(filePath));
        int yMax = 10, xMax = 10;
        #endregion

        public joystick()
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
                Minimum = -xMax,
                Maximum = xMax,
                Key = "Horizontal",
                Title = "X"
            });
            DataPlotModel.Axes.Add(new LinearAxis()
            {
                Position = AxisPosition.Left,
               /* Minimum = -yMax,
                Maximum = yMax,*/
                Key = "Vertical",
                Title = "Y"
            });

            plotView1.Model = DataPlotModel;

            Server = new IoTServer(ip, port);


            RequestTimer = new Timer(sampleTime);
            RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
            RequestTimer.Enabled = true;
            DataPlotModel.Series.Clear();
            DataPlotModel.Series.Add(new LineSeries() { Title = "Position", Color = OxyColor.Parse("#7386D5") });
            DataPlotModel.ResetAllAxes();
        }

        private void UpdatePlot(int x, int y)
        {

            LineSeries lineSeries = DataPlotModel.Series[0] as LineSeries;
            //punkt
            lineSeries.Points.Add(new DataPoint(x,y));

           // lineSeries.Points.RemoveAt(0);

            //osie
            if (x >= xMax)
            {
                xMax = x;
                DataPlotModel.Axes[0].Minimum = xMax - 21;
                DataPlotModel.Axes[0].Maximum = xMax + 1 ;
            }
            if (x <= -xMax)
            {
                xMax = -x;
                DataPlotModel.Axes[0].Minimum = -xMax - 1;
                DataPlotModel.Axes[0].Maximum = -xMax + 21;
            }/*
            if (y >= yMax)
            {
                yMax = y;
                DataPlotModel.Axes[0].Minimum = yMax - 21;
                DataPlotModel.Axes[0].Maximum = yMax + 1;
            }
            if (y <= -yMax)
            {
                yMax = -y;
                DataPlotModel.Axes[0].Minimum = -yMax - 1;
                DataPlotModel.Axes[0].Maximum = -yMax + 21;
            }
            */
            DataPlotModel.InvalidatePlot(true);
        }

        private async void UpdatePlotWithServerResponse()
        {
            string responseText = await Server.GETwithClient();
            ServerData resposneJson = JsonConvert.DeserializeObject<ServerData>(responseText);
           
            UpdatePlot(resposneJson.x, resposneJson.y);

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
