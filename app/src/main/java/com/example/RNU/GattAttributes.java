package com.example.RNU;

import java.util.HashMap;

public class GattAttributes {

    private static HashMap<String, String> attributes = new HashMap();
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String HM_RX_TX = "49535343-1E4D-4BD9-BA61-23C647249616";
    static {
        attributes.put(HM_RX_TX,"RX/TX data");
    }
}
