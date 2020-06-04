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
        //change unit
        private void button3_Click(object sender, EventArgs e)
        {
            if (unitFlag == "basicUnits")
            {
                unitFlag = "otherUnits";
                label10.Text = "[°F]";
                label11.Text = "[mmHg]";
                label12.Text = "[0-1]";
                label13.Text = "[RAD]";
                label14.Text = "[RAD]";
                label15.Text = "[RAD]";
            }
            if (unitFlag == "otherUnits")
            {
                unitFlag = "basicUnits";
                label10.Text = "[°C]";
                label11.Text = "[hPa]";
                label12.Text = "[%rH]";
                label13.Text = "[DRG]";
                label14.Text = "[DGR]";
                label15.Text = "[DGR]";
            }
        }
        #endregion

        #region Functions
        private async void UpdateTableWithServerResponse()
        {

            string responseText = await Server.GETwithClient();
            ServerData resposneJson = JsonConvert.DeserializeObject<ServerData>(responseText);
            if (unitFlag == "basicUnits")
            {
                textBox1.Text = resposneJson.temperature.ToString();
                textBox2.Text = resposneJson.pressure.ToString();
                textBox3.Text = resposneJson.humidity.ToString();
                textBox4.Text = resposneJson.roll.ToString();
                textBox5.Text = resposneJson.pitch.ToString();
                textBox6.Text = resposneJson.yaw.ToString();
                textBox7.Text = resposneJson.x.ToString();
                textBox8.Text = resposneJson.y.ToString();
                textBox9.Text = resposneJson.mid.ToString();
            }
            if (unitFlag == "otherUnits")
            {
                farenheit = resposneJson.temperature * 1.8 + 32;
                textBox1.Text = farenheit.ToString();

                mmhg = resposneJson.pressure * 0.750062;
                textBox2.Text = mmhg.ToString();

                humi01 = resposneJson.humidity / 100;
                textBox3.Text = humi01.ToString();

                rRad = resposneJson.roll * Math.PI / 180;
                textBox4.Text = rRad.ToString();

                pRad = resposneJson.pitch * Math.PI / 180;
                textBox5.Text = pRad.ToString();

                yRad = resposneJson.yaw * Math.PI / 180;
                textBox6.Text = yRad.ToString();

                textBox7.Text = resposneJson.x.ToString();
                textBox8.Text = resposneJson.y.ToString();
                textBox9.Text = resposneJson.mid.ToString();
            }

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
