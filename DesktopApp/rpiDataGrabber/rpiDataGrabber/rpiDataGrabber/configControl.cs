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

namespace rpiDataGrabber
{
    
    public partial class configControl : UserControl
    {
        //sciezka do pliku config
        const string filePath = @"C:\Users\Klient\Documents\GitHub\RPiDG\DesktopApp\rpiDataGrabber\rpiDataGrabber\config.json";


        //funkcja do kodowania json 
        public static void Serialize(object obj)
        {
            var serializer = new JsonSerializer();

            using (var sw = new StreamWriter(filePath))
            using (JsonWriter writer = new JsonTextWriter(sw))
            {
                serializer.Serialize(writer, obj);
            }
        }
        

        public configControl()
        {
            InitializeComponent();
            configData deserialized = JsonConvert.DeserializeObject<configData>(File.ReadAllText(filePath));
            textBox1.Text = deserialized.ip;
            textBox2.Text = deserialized.port;
            textBox3.Text = deserialized.sampleTime.ToString();
            textBox4.Text = deserialized.maxSamples.ToString();
            textBox5.Text = deserialized.decimalPlaces.ToString();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            var conf = new configData
            {
                ip = textBox1.Text,
                port = textBox2.Text,
                sampleTime = Int16.Parse(textBox3.Text),
                maxSamples = Int16.Parse(textBox4.Text),
                decimalPlaces = Int16.Parse(textBox5.Text)
            };
            Serialize(conf);

            // Inicjalizacja zmiennych do przekazania do metody MessageBox.Show.
            string message = "Your configuration have been successfully saved.";
            string caption = "Data Confirmation";
            MessageBoxButtons buttons = MessageBoxButtons.OK;

            // Wyswietl MessageBox.
            MessageBox.Show(message, caption, buttons);
        }
    }
}
