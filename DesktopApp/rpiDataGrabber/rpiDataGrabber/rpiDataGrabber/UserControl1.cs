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
    //struktura danych json
    public class configData
    {
        public string ip { get; set; }
        public string port { get; set; }
        public int sampleTime { get; set; }
        public int maxSamples { get; set; }
    }


    public partial class UserControl1 : UserControl
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

     
        public UserControl1()
        {
            InitializeComponent();
            //przypisanie textBoxom poczatkowych wartosci
            configData deserialized = JsonConvert.DeserializeObject<configData>(File.ReadAllText(filePath));
            textBox2.Text = deserialized.ip;
            textBox3.Text = deserialized.port;
            textBox4.Text = deserialized.sampleTime.ToString();
            textBox1.Text = deserialized.maxSamples.ToString();

        }

        private void button4_Click(object sender, EventArgs e)
        {
            var conf = new configData
            {
                ip = textBox2.Text,
                port = textBox3.Text,
                sampleTime = Int16.Parse(textBox4.Text),
                maxSamples = Int16.Parse(textBox1.Text)
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
