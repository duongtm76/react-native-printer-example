package com.awsomeapp202308;

import static android.content.Context.WIFI_SERVICE;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.lvrenyang.io.NETPrinting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.HashMap;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.PrintWriter;

public class PrintModule extends ReactContextBaseJavaModule {
    int SERVER_PORT = 9100;
    String SERVER_IP = "";
    ReactApplicationContext context;

    PrintModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
        SERVER_IP = this.getLocalIpAddress();
    }

    @Override
    public String getName() {
        return "PrintModule";
    }

    @ReactMethod
    public void createPrintEvent(String name, String location) {
        // NETPrinting printer = new NETPrinting();
        // printer.Open("192.168.1.6", 3000);
        // printer.Write("abc".getBytes(), 0, 0);

        // this.PrintByDefault();
        try {
            Log.d("PrintModule", "Start Printing");
            Socket sock = new Socket("192.168.1.87", 9100);
            PrintWriter oStream = new PrintWriter(sock.getOutputStream());
            oStream.println("HI,test from Android Device");
            oStream.println("\n\n\n");
            oStream.close();
            sock.close();
        } catch (UnknownHostException e) {
            Log.d("PrintModule", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("PrintModule", e.toString());
            e.printStackTrace();
        }
    }

    private String getLocalIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) this.context.getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipInt = wifiInfo.getIpAddress();
            return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array())
                    .getHostAddress();
        } catch (UnknownHostException exception) {
            return "";
        }
    }

    public void PrintByDefault() {
        try {
            Log.d("PrintModule", "PrinByDefault - IP: " + SERVER_IP + ", PORT: " + SERVER_PORT);
            Socket serverSocket = new Socket(SERVER_IP, SERVER_PORT);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("PrintModule", "Not connected");
                }
            });
            Log.d("PrintModule", "printWriter...");
            PrintWriter printWriter = new PrintWriter(serverSocket.getOutputStream());
            BufferedReader input = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("PrintModule", "Connected");
                    printWriter.write("Hello world");
                    printWriter.print("123123123");
                }
            });
        } catch (IOException e) {
            Log.d("PrintModule", "printWriter err: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getConstants() {
        Log.d("PrintModule", "getConstants");
        final Map<String, Object> constants = new HashMap<>();
        constants.put("DEFAULT_EVENT_NAME", "New Event");
        return constants;
    }
}