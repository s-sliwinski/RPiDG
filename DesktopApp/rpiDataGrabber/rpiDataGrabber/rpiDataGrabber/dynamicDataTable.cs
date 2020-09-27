using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Newtonsoft.Json;
using System.IO;
using System.Timers;
using Timer = System.Timers.Timer;

namespace rpiDataGrabber
{
    public partial class dynamicDataTable : UserControl
    {
         #region Fields        
         private Timer RequestTimer;
         private IoTServer Server;
         const string filePath = @"C:\Users\Klient\Documents\GitHub\RPiDG\DesktopApp\rpiDataGrabber\rpiDataGrabber\config.json";
         configData deserialized = JsonConvert.DeserializeObject<configData>(File.ReadAllText(filePath));
       
         public string unitFlag = "basicUnits";
         public double farenheit, mmhg, humi01;
         public double rRad, pRad, yRad;
         int dec;
         int labelFlag;
         string tempString;

        #endregion

        delegate void LabelDelegate(string message);
        public dynamicDataTable()
         {
             InitializeComponent();


             string ip = deserialized.ip.ToString();
             string port = deserialized.port.ToString();
             dec = deserialized.decimalPlaces;


            Server = new IoTServer(ip, port);
         }

         #region Buttons
         
        //change unit to basic
        private void button1_Click(object sender, EventArgs e)
        {        
            if (RequestTimer != null)
             {
                 RequestTimer.Enabled = false;
                 RequestTimer = null;
                 label1.Text = "";
                 label2.Text = "";
                 label3.Text = "";
                 label4.Text = "";
                 label5.Text = "";
                 label6.Text = "";
                 label7.Text = "";
                 label8.Text = "";
                 label9.Text = "";
            }

             if (RequestTimer == null)
             {
                 double sampleTime = deserialized.sampleTime;
                 RequestTimer = new Timer(sampleTime);
                 RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                 RequestTimer.Enabled = true;
             }

             unitFlag = "basicUnits";
            
        }
               
           
        //change unit to other
        private void button2_Click(object sender, EventArgs e)
         {
            if (RequestTimer != null)
            {
                RequestTimer.Enabled = false;
                RequestTimer = null;
                label1.Text = "";
                label2.Text = "";
                label3.Text = "";
                label4.Text = "";
                label5.Text = "";
                label6.Text = "";
                label7.Text = "";
                label8.Text = "";
                label9.Text = "";
            }

            if (RequestTimer == null)
            {
                double sampleTime = deserialized.sampleTime;
                RequestTimer = new Timer(sampleTime);
                RequestTimer.Elapsed += new ElapsedEventHandler(RequestTimerElapsed);
                RequestTimer.Enabled = true;
            }
                     
            unitFlag = "otherUnits";


        }
        #endregion

         #region LabelUpdateSystem
    

        private void UpdateString(string msg)
         {//invoke zeby spoza UI thread updatowac labela
            if(labelFlag == 1) {
                if (this.label1.InvokeRequired)
                    this.label1.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label1.Text = msg; 
            }
            if (labelFlag == 2)
            {
                if (this.label2.InvokeRequired)
                    label2.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label2.Text = msg;
            }
            if (labelFlag == 3)
            {
                if (this.label3.InvokeRequired)
                    label3.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label3.Text = msg;
            }
            if (labelFlag == 4)
            {
                if (this.label4.InvokeRequired)
                    label4.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label4.Text = msg;
            }
            if (labelFlag == 5)
            {
                if (this.label5.InvokeRequired)
                    label5.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label5.Text = msg;
            }
            if (labelFlag == 6)
            {
                if (this.label6.InvokeRequired)
                    label6.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label6.Text = msg;
            }
            if (labelFlag == 7)
            {
                if (this.label7.InvokeRequired)
                    label7.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label7.Text = msg;
            }
            if (labelFlag == 8)
            {
                if (this.label8.InvokeRequired)
                    label8.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label8.Text = msg;
            }
            if (labelFlag == 9)
            {
                if (this.label9.InvokeRequired)
                    label9.Invoke(new LabelDelegate(UpdateString), new object[] { msg });
                else
                    this.label9.Text = msg;
            }


        }

      
        #endregion

        private async void UpdateLabelsWithServerResponse()
        {
             labelFlag = 1;
             string responseText = await Server.GETwithClient();
             //przypisanie wartości null co by mozna było je porównać
             nullableServerData responseJson = new nullableServerData();
             responseJson.temperature = null;
             responseJson.humidity = null;
             responseJson.pressure = null;
             responseJson.pitch = null;
             responseJson.roll = null;
             responseJson.yaw = null;
             responseJson.y = null;
             responseJson.x = null;
             responseJson.mid = null;

            responseJson = JsonConvert.DeserializeObject<nullableServerData>(responseText);

            #region calc&assignVals

            //temperature
            if (responseJson.temperature != null)
            {
                double normaDouble = responseJson.temperature ?? 0;
                double temporaryTemp = Math.Round(normaDouble, dec);
                
                if (unitFlag == "basicUnits")
                     tempString = "Temperature: " + temporaryTemp.ToString() +  " [°C]";
                if (unitFlag == "otherUnits")
                {
                    temporaryTemp = temporaryTemp * 1.8 + 32;
                    temporaryTemp = Math.Round(temporaryTemp, dec);
                    tempString = "Temperature: " + temporaryTemp.ToString() + " [°F]";
                }
                     
                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.temperature == null)
            { 
                //doNothing
            }

            //humidity
            if (responseJson.humidity != null)
            {
                double normaDouble = responseJson.humidity ?? 0;
                double temporary = Math.Round(normaDouble, dec);

                if (unitFlag == "basicUnits")
                    tempString = "Humidity: " + temporary.ToString() + " [%rH]";
                if (unitFlag == "otherUnits")
                {
                    temporary = temporary / 100;
                    temporary = Math.Round(temporary, dec);
                    tempString = "Humidity: " + temporary.ToString() + " [0-1]";
                }

                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.humidity == null)
            {
                //doNothing
            }

            //pressure
            if (responseJson.pressure != null)
            {
                double normaDouble = responseJson.pressure ?? 0;
                double temporary = Math.Round(normaDouble, dec);

                if (unitFlag == "basicUnits")
                    tempString = "Pressure: " + temporary.ToString() + " [hPa]";
                if (unitFlag == "otherUnits")
                {
                    temporary = temporary * 0.750062;
                    temporary = Math.Round(temporary, dec);
                    tempString = "Pressure: " + temporary.ToString() + " [mmHg]";
                }

                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.pressure == null)
            {
                //doNothing
            }

            //roll
            if (responseJson.roll != null)
            {
                double normaDouble = responseJson.roll ?? 0;
                double temporaryTemp = Math.Round(normaDouble, dec);

                if (unitFlag == "basicUnits")
                    tempString = "Roll: " + temporaryTemp.ToString() + " [DRG]";
                if (unitFlag == "otherUnits")
                {
                    temporaryTemp = temporaryTemp * Math.PI / 180;
                    temporaryTemp = Math.Round(temporaryTemp, dec);
                    tempString = "Roll: " + temporaryTemp.ToString() + " [RAD]";
                }

                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.roll == null)
            {
                //doNothing
            }

            //pitch
            if (responseJson.pitch != null)
            {
                double normaDouble = responseJson.pitch ?? 0;
                double temporaryTemp = Math.Round(normaDouble, dec);

                if (unitFlag == "basicUnits")
                    tempString = "Pitch: " + temporaryTemp.ToString() + " [DRG]";
                if (unitFlag == "otherUnits")
                {
                    temporaryTemp = temporaryTemp * Math.PI / 180;
                    temporaryTemp = Math.Round(temporaryTemp, dec);
                    tempString = "Pitch: " + temporaryTemp.ToString() + " [RAD]";
                }

                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.pitch == null)
            {
                //doNothing
            }

            //yaw
            if (responseJson.yaw != null)
            {
                double normaDouble = responseJson.yaw ?? 0;
                double temporaryTemp = Math.Round(normaDouble, dec);

                if (unitFlag == "basicUnits")
                    tempString = "Yaw: " + temporaryTemp.ToString() + " [DRG]";
                if (unitFlag == "otherUnits")
                {
                    temporaryTemp = temporaryTemp * Math.PI / 180;
                    temporaryTemp = Math.Round(temporaryTemp, dec);
                    tempString = "Yaw: " + temporaryTemp.ToString() + " [RAD]";
                }

                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.yaw == null)
            {
                //doNothing
            }

            //x
            if (responseJson.x != null)
            {
                int normalInt = responseJson.x ?? 0;

                tempString = "X: " + normalInt.ToString();

                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.x == null)
            {
                //doNothing
            }

            //y
            if (responseJson.y != null)
            {
                int normalInt = responseJson.y ?? 0;

                tempString = "Y: " + normalInt.ToString();

                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.y == null)
            {
                //doNothing
            }

            //mid
            if (responseJson.mid != null)
            {
                int normalInt = responseJson.mid ?? 0;

                tempString = "MID: " + normalInt.ToString();

                UpdateString(tempString);
                labelFlag++;
            }
            if (responseJson.mid == null)
            {
                //doNothing
            }
            #endregion

        }

        private void RequestTimerElapsed(object sender, ElapsedEventArgs e)
         {
             UpdateLabelsWithServerResponse();
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
