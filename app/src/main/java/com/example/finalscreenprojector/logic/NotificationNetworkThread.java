package com.example.finalscreenprojector.logic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class NotificationNetworkThread extends Thread{
    private String payload;
    private NotificationNetworkThread(String payload) {
        this.payload = payload;
    }

    public static NotificationNetworkThread createUDPSenderForString(String message) {
        return new NotificationNetworkThread(message);
    }

    @Override
    public void run() {
        try {
            DatagramSocket datagramSocket = new DatagramSocket();
            System.out.println(payload);
            String testText = payload;
            byte[] bytes = testText.getBytes();
            System.out.println("bytes: " + Arrays.deepToString(new byte[][]{bytes}));
            byte[] ipAddr = new byte[]{(byte) 172, (byte) 16, 121, 93};
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length,
                    InetAddress.getByAddress(ipAddr), 8000);
            try {
                datagramSocket.send(datagramPacket);

            } catch (Exception e) {
                System.out.println("execpt");
                System.out.println("e" + e);
            }
            System.out.println("sending ended...");

        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

}
