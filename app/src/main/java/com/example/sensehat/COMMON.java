package com.example.sensehat;

public final class COMMON {
    // activities request codes
    public final static int REQUEST_CODE_CONFIG = 1;

    // configuration info: names and default values
    public final static String CONFIG_IP_ADDRESS = "ipAddress";
    public static String DEFAULT_IP_ADDRESS = "192.168.0.24";

    public final static String CONFIG_SAMPLE_TIME = "sampleTime";
    public static int DEFAULT_SAMPLE_TIME = 500;

    public final static String CONFIG_MAX_SAMPLES = "maxSamples";
    public static int DEFAULT_MAX_SAMPLES = 10;

    public final static String CONFIG_PORT_NUMBER = "portNumber";
    static int DEFAULT_PORT_NUMBER = 22;



    // error codes
    public final static int ERROR_TIME_STAMP = -1;
    public final static int ERROR_NAN_DATA = -2;
    public final static int ERROR_RESPONSE = -3;


    // IoT server data
    public final static String FILE_NAME = "chartdata.json";
    public final static String  FILE_MARIO= "mario.json";
    public final static String  FILE_LUIGI= "luigi.json";
}