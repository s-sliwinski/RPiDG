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
    public partial class dataTable : UserControl
    {
        #region Fields        
        private Timer RequestTimer;
        private IoTServer Server;
        const string filePath = @"C:\Users\Klient\Documents\GitHub\RPiDG\DesktopApp\rpiDataGrabber\rpiDataGrabber\config.json";
        configData deserialized = JsonConvert.DeserializeObject<configData>(File.ReadAllText(filePath));
        public string unitFlag = "basicUnits";
        public double farenheit, mmhg, humi01;
        public double rRad, pRad, yRad;
        #endregion

        public dataTable()
        {
            InitializeComponent();

            string ip = deserialized.ip.ToString();
            string port = deserialized.port.ToString();
            double maxSamples = deserialized.maxSamples;
            double sampleTime = deserialized.sampleTime;

            label10.Text = "[°C]";
            label11.Text = "[hPa]";
            label12.Text = "[%rH]";
            label13.Text = "[DRG]";
            label14.Text = "[DGR]";
            label15.Text = "[DGR]";

            Server = new IoTServer(ip, port);
        }

        #region Buttons
        //start
        private void button1_Click(object sender, EventArgs e)
        {
            if (RequestTimer == null)
            {
                double sampleTime = deserialized.sampleTime;
                RequestTimer = new Timer(sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;
            }
        }
        //stop
        private void button2_Click(object sender, EventArgs e)
        {
            if (RequestTimer != null)
            {
                RequestTimer.Enabled = false;
                RequestTimer = null;
            }
        }
        //change unit to basic
        private void button3_Click(object sender, EventArgs e)
        {
            if (unitFlag == "otherUnits")
            {
                unitFlag = "basicUnits";
                label10.Text = "[°C]";
                label10.Refresh();
                label11.Text = "[hPa]";
                label11.Refresh();
                label12.Text = "[%rH]";
                label12.Refresh();
                label13.Text = "[DRG]";
                label13.Refresh();
                label14.Text = "[DGR]";
                label14.Refresh();
                label15.Text = "[DGR]";
                label15.Refresh();
            }
        }
        //change unit to other
        private void button4_Click(object sender, EventArgs e)
        {
            if (unitFlag == "basicUnits")
            {
                unitFlag = "otherUnits";
                label10.Text = "[°F]";
                label10.Refresh();
                label11.Text = "[mmHg]";
                label11.Refresh();
                label12.Text = "[0-1]";
                label12.Refresh();
                label13.Text = "[RAD]";
                label13.Refresh();
                label14.Text = "[RAD]";
                label14.Refresh();
                label15.Text = "[RAD]";
                label15.Refresh();
            }
        }
        #endregion

        #region Functions

        private void UpdateTable(string flag, ServerData resposneJson)
        {
            if (flag == "basicUnits")
            {
                label16.Text = resposneJson.temperature.ToString();
                label16.Refresh();
                label17.Text = resposneJson.pressure.ToString();
                label17.Refresh();
                label18.Text = resposneJson.humidity.ToString();
                label18.Refresh();
                label19.Text = resposneJson.roll.ToString();
                label19.Refresh();
                label20.Text = resposneJson.pitch.ToString();
                label20.Refresh();
                label21.Text = resposneJson.yaw.ToString();
                label21.Refresh();
                label22.Text = resposneJson.x.ToString();
                label22.Refresh();
                label23.Text = resposneJson.y.ToString();
                label23.Refresh();
                label24.Text = resposneJson.mid.ToString();
                label24.Refresh();
            }
            if (flag == "otherUnits")
            {
                farenheit = resposneJson.temperature * 1.8 + 32;
                label16.Text = farenheit.ToString();
                label16.Refresh();

                mmhg = resposneJson.pressure * 0.750062;
                label17.Text = mmhg.ToString();
                label17.Refresh();

                humi01 = resposneJson.humidity / 100;
                label18.Text = humi01.ToString();
                label18.Refresh();

                rRad = resposneJson.roll * Math.PI / 180;
                label19.Text = rRad.ToString();
                label19.Refresh();

                pRad = resposneJson.pitch * Math.PI / 180;
                label20.Text = pRad.ToString();
                label20.Refresh();

                yRad = resposneJson.yaw * Math.PI / 180;
                label21.Text = yRad.ToString();
                label21.Refresh();

                label22.Text = resposneJson.x.ToString();
                label22.Refresh();
                label23.Text = resposneJson.y.ToString();
                label23.Refresh();
                label24.Text = resposneJson.mid.ToString();
                label24.Refresh();
            }
        }

        private async void UpdateTableWithServerResponse()
        {
            string responseText = await Server.GETwithClient();
            ServerData resposneJson = JsonConvert.DeserializeObject<ServerData>(responseText);
            UpdateTable(unitFlag, resposneJson);

        }

        private void RequestTimerElapsed(object sender, ElapsedEventArgs e)
        {
            UpdateTableWithServerResponse();
        }

        #endregion

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
