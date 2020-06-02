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
using System.Net;
using System.Net.Http;

namespace rpiDataGrabber
{
    public partial class UserControl2 : UserControl
    {
        public UserControl2()
        {
            InitializeComponent();
        }
        
        //actual picked color
        Color actColor = Color.Transparent;
        //wybieranie koloru
        private void button66_Click(object sender, EventArgs e)
        {
            // buttTag = int(button1.Name);
            ColorDialog colDial = new ColorDialog();
            // Keeps the user from selecting a custom color.
            colDial.AllowFullOpen = false;

            // Update the text box color if the user clicks OK 
            if (colDial.ShowDialog() == DialogResult.OK)
            {
                shownColor.BackColor = colDial.Color;
                actColor = colDial.Color;
            }
        }

        //dummy way to use buttons
        private void button1_Click(object sender, EventArgs e) => button1.BackColor = actColor;

        private void button2_Click(object sender, EventArgs e)
        {
            button2.BackColor = actColor;
        }

        private void button3_Click(object sender, EventArgs e)
        {
            button3.BackColor = actColor;
        }

        private void button5_Click(object sender, EventArgs e)
        {
            button5.BackColor = actColor;
        }

        private void button6_Click(object sender, EventArgs e)
        {
            button6.BackColor = actColor;
        }

        private void button7_Click(object sender, EventArgs e)
        {
            button7.BackColor = actColor;
        }

        private void button8_Click(object sender, EventArgs e)
        {
            button8.BackColor = actColor;
        }

        private void button9_Click(object sender, EventArgs e)
        {
            button9.BackColor = actColor;
        }

        private void button10_Click(object sender, EventArgs e)
        {
            button10.BackColor = actColor;
        }

        private void button11_Click(object sender, EventArgs e)
        {
            button11.BackColor = actColor;
        }

        private void button12_Click(object sender, EventArgs e)
        {
            button12.BackColor = actColor;
        }

        private void button13_Click(object sender, EventArgs e)
        {
            button13.BackColor = actColor;
        }

        private void button14_Click(object sender, EventArgs e)
        {
            button14.BackColor = actColor;
        }

        private void button15_Click(object sender, EventArgs e)
        {
            button15.BackColor = actColor;
        }

        private void button16_Click(object sender, EventArgs e)
        {
            button16.BackColor = actColor;
        }

        private void button17_Click(object sender, EventArgs e)
        {
            button17.BackColor = actColor;
        }

        private void button18_Click(object sender, EventArgs e)
        {
            button18.BackColor = actColor;
        }

        private void button19_Click(object sender, EventArgs e)
        {
            button19.BackColor = actColor;
        }

        private void button20_Click(object sender, EventArgs e)
        {
            button20.BackColor = actColor;
        }

        private void button21_Click(object sender, EventArgs e)
        {
            button21.BackColor = actColor;
        }

        private void button22_Click(object sender, EventArgs e)
        {
            button22.BackColor = actColor;
        }

        private void button23_Click(object sender, EventArgs e)
        {
            button23.BackColor = actColor;
        }

        private void button24_Click(object sender, EventArgs e)
        {
            button24.BackColor = actColor;
        }

        private void button25_Click(object sender, EventArgs e)
        {
            button25.BackColor = actColor;
        }

        private void button26_Click(object sender, EventArgs e)
        {
            button26.BackColor = actColor;
        }

        private void button27_Click(object sender, EventArgs e)
        {
            button27.BackColor = actColor;
        }

        private void button28_Click(object sender, EventArgs e)
        {
            button28.BackColor = actColor;
        }

        private void button29_Click(object sender, EventArgs e)
        {
            button29.BackColor = actColor;
        }

        private void button30_Click(object sender, EventArgs e)
        {
            button30.BackColor = actColor;
        }

        private void button31_Click(object sender, EventArgs e)
        {
            button31.BackColor = actColor;
        }

        private void button32_Click(object sender, EventArgs e)
        {
            button32.BackColor = actColor;
        }

        private void button33_Click(object sender, EventArgs e)
        {
            button33.BackColor = actColor;
        }

        private void button34_Click(object sender, EventArgs e)
        {
            button34.BackColor = actColor;
        }

        private void button35_Click(object sender, EventArgs e)
        {
            button35.BackColor = actColor;
        }

        private void button36_Click(object sender, EventArgs e)
        {
            button36.BackColor = actColor;
        }

        private void button37_Click(object sender, EventArgs e)
        {
            button37.BackColor = actColor;
        }

        private void button38_Click(object sender, EventArgs e)
        {
            button38.BackColor = actColor;
        }

        private void button39_Click(object sender, EventArgs e)
        {
            button39.BackColor = actColor;
        }

        private void button40_Click(object sender, EventArgs e)
        {
            button40.BackColor = actColor;
        }

        private void button41_Click(object sender, EventArgs e)
        {
            button41.BackColor = actColor;
        }

        private void button42_Click(object sender, EventArgs e)
        {
            button42.BackColor = actColor;
        }

        private void button43_Click(object sender, EventArgs e)
        {
            button43.BackColor = actColor;
        }

        private void button44_Click(object sender, EventArgs e)
        {
            button44.BackColor = actColor;
        }

        private void button45_Click(object sender, EventArgs e)
        {
            button45.BackColor = actColor;
        }

        private void button46_Click(object sender, EventArgs e)
        {
            button46.BackColor = actColor;
        }

        private void button47_Click(object sender, EventArgs e)
        {
            button47.BackColor = actColor;
        }

        private void button48_Click(object sender, EventArgs e)
        {
            button48.BackColor = actColor;
        }

        private void button49_Click(object sender, EventArgs e)
        {
            button49.BackColor = actColor;
        }

        private void button50_Click(object sender, EventArgs e)
        {
            button50.BackColor = actColor;
        }

        private void button51_Click(object sender, EventArgs e)
        {
            button51.BackColor = actColor;
        }

        private void button52_Click(object sender, EventArgs e)
        {
            button52.BackColor = actColor;
        }

        private void button53_Click(object sender, EventArgs e)
        {
            button53.BackColor = actColor;
        }

        private void button54_Click(object sender, EventArgs e)
        {
            button54.BackColor = actColor;
        }

        private void button55_Click(object sender, EventArgs e)
        {
            button55.BackColor = actColor;
        }

        private void button56_Click(object sender, EventArgs e)
        {
            button56.BackColor = actColor;
        }

        private void button57_Click(object sender, EventArgs e)
        {
            button57.BackColor = actColor;
        }

        private void button58_Click(object sender, EventArgs e)
        {
            button58.BackColor = actColor;
        }

        private void button59_Click(object sender, EventArgs e)
        {
            button59.BackColor = actColor;
        }

        private void button60_Click(object sender, EventArgs e)
        {
            button60.BackColor = actColor;
        }

        private void button61_Click(object sender, EventArgs e)
        {
            button61.BackColor = actColor;
        }

        private void button62_Click(object sender, EventArgs e)
        {
            button62.BackColor = actColor;
        }

        private void button63_Click(object sender, EventArgs e)
        {
            button63.BackColor = actColor;
        }

        private void button64_Click(object sender, EventArgs e)
        {
            button64.BackColor = actColor;
        }

        private void button65_Click(object sender, EventArgs e)
        {
            button65.BackColor = actColor;
        }


        //color {"x": int, "y": int, "r" : int, ....}
        public class colorTag
        {
            public int x { get; set; }
            public int y { get; set; }
            public int r { get; set; }
            public int g { get; set; }
            public int b { get; set; }
        }
        
        //SEND
        private void button4_Click(object sender, EventArgs e)
        {
            //lista
            List<colorTag> listOfColors = new List<colorTag>();
            //sprawdzanie koloru kazdego przycisku, jesli jest inny niz transparent to wpisz do listy
            foreach (Control c in tableLayoutPanel2.Controls)
            {
                
               /* if(c.Tag == i.ToString()+j.ToString())*/ if(c.BackColor != Color.Transparent) {
                    char[] characters = c.Tag.ToString().ToCharArray();  //zmieniamy tag na string na char[]
                                                                         //zeby zmienic go na xy
                                                                         //zeby zrobic wydobywanie pojedynczych cyfr
                                                                         //z typ object
                    var cT = new colorTag
                        {
                            x = Int16.Parse(characters[0].ToString()),
                            y = Int16.Parse(characters[1].ToString()),
                            r = c.BackColor.R,
                            g = c.BackColor.G,
                            b = c.BackColor.B
                        };
                        listOfColors.Add(cT);
                    }
             
            }
            //tworzenie wynikowego json'a
            string json = JsonConvert.SerializeObject(listOfColors, Formatting.Indented);
            //odczytanie ip i portu z pliku konfiguracyjnego
            //sciezka do pliku config
            const string filePath = @"C:\Users\Klient\Documents\GitHub\RPiDG\DesktopApp\rpiDataGrabber\rpiDataGrabber\config.json";
            configData deserialized = JsonConvert.DeserializeObject<configData>(File.ReadAllText(filePath));
            string ip = deserialized.ip;
            string port = deserialized.port.ToString();

            HttpClient client = new HttpClient();
            var content = new StringContent(json.ToString(), Encoding.UTF8, "application/json");
            var result = client.PostAsync("http://" + ip +":"+port+"/setLeds.php", content).Result;
            
            //sciezka do pliku colors 
            const string filePath1 = @"C:\Users\Klient\Desktop\colors.json";
            System.IO.File.WriteAllText(filePath1, json);

        }

    }
}
