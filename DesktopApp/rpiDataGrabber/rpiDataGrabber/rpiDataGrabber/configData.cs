using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace rpiDataGrabber
{
    class configData
    {
        public string ip { get; set; }
        public string port { get; set; }
        public int sampleTime { get; set; }
        public int maxSamples { get; set; }
        public int decimalPlaces { get; set; }
    }
}
