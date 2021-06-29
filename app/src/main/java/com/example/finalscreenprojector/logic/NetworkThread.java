package com.example.finalscreenprojector.logic;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class NetworkThread extends Thread{
    private Bitmap bmp;
    private final int RED = -65536;
    private final int BLACK = -16777216;
    private String type;
    private int scale;
    public NetworkThread(Bitmap bitmap, String type){
        bmp = bitmap;
        this.type = type;
    }
    public int[][] rescalePicture(int[][]pixels){
        int[][] returnVal = new int[32][8];
        int[][] reduceColumns = new int[32*scale][8];
        for(int i = 0; i<=scale*32; i += 1){
            if(i%scale==0 && i!=0){
                for(int z = 0; z<8; z += 1){
                    int temp=0;
                    if(scale==40) {
                        temp = reduceColumns[i - 40][z] + reduceColumns[i - 39][z] + reduceColumns[i - 38][z] +
                                reduceColumns[i - 37][z] + reduceColumns[i - 36][z] + reduceColumns[i - 35][z] +
                                reduceColumns[i - 34][z] + reduceColumns[i - 33][z] + reduceColumns[i - 32][z] +
                                reduceColumns[i - 31][z] + reduceColumns[i - 30][z] + reduceColumns[i - 29][z] +
                                reduceColumns[i - 28][z] + reduceColumns[i - 27][z] + reduceColumns[i - 26][z] +
                                reduceColumns[i - 25][z] + reduceColumns[i - 24][z] + reduceColumns[i - 23][z] +
                                reduceColumns[i - 22][z] + reduceColumns[i - 21][z] +
                                reduceColumns[i - 20][z] + reduceColumns[i - 19][z] + reduceColumns[i - 18][z] +
                                reduceColumns[i - 17][z] + reduceColumns[i - 16][z] + reduceColumns[i - 15][z] +
                                reduceColumns[i - 14][z] + reduceColumns[i - 13][z] + reduceColumns[i - 12][z] +
                                reduceColumns[i - 11][z] + reduceColumns[i - 10][z] + reduceColumns[i - 9][z] +
                                reduceColumns[i - 8][z] + reduceColumns[i - 7][z] + reduceColumns[i - 6][z] +
                                reduceColumns[i - 5][z] + reduceColumns[i - 4][z] + reduceColumns[i - 3][z] +
                                reduceColumns[i - 2][z] + reduceColumns[i - 1][z];
                    }
                    else{
                        for(int t=scale;t>0;t--){
                            temp+=reduceColumns[i-t][z];
                        }
                    }
                    if (temp >= (scale*scale)/2)
                        returnVal[i / scale - 1][z] = 1;
                    else
                        returnVal[i / scale - 1][z] = 0;
                }
                if(i==scale*32)
                    return returnVal;
            }
            for (int j = 0; j<8; j += 1){
                int count=0;
                for(int k = 0; k<scale; k += 1){
                    if(pixels[i][j*scale+k] == 0){
                        count+=1;
                    }
                    else
                        count = count;
                }
                reduceColumns[i][j] = count;
            }
        }
        return returnVal;
    }
    public void run(){
        try {
            DatagramSocket datagramSocket = new DatagramSocket();

            System.out.println("sending started...");
            int size = bmp.getRowBytes()*bmp.getHeight();
            int[] pixels = new int[bmp.getWidth()*bmp.getHeight()];
            int[][] bitmapMatrix = new int[bmp.getHeight()][bmp.getWidth()];
            int rows=0;
            scale = bmp.getWidth()/8;
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
            for(int i = 0; i< bmp.getHeight(); i += 1){
                for(int j = 0; j<bmp.getWidth(); j += 1){
                    int index = i*bmp.getWidth()+j;
                    if(type=="drawing") {
                        if (pixels[index] == RED) {
                            bitmapMatrix[i][j] = 0;
                        } else {
                            bitmapMatrix[i][j] = 1;
                        }
                    }
                    else{
                        if(type=="image"){
                            float red = Color.red(pixels[index]);
                            float green = Color.green(pixels[index]);
                            float blue = Color.blue(pixels[index]);
                            float avg = (red+green+blue)/3;
                            if (avg<=127.5) {
                                bitmapMatrix[i][j] = 0;
                            } else {
                                bitmapMatrix[i][j] = 1;
                            }
                        }
                    }

                }
            }
            int[][] returnVal = rescalePicture(bitmapMatrix);
            String sendMessage = "";
            for (int i =0;i<32;i+=1){
                System.out.println(i+" "+returnVal[i][0]+
                        returnVal[i][1]+returnVal[i][2]+returnVal[i][3]+returnVal[i][4]+
                                returnVal[i][5]+returnVal[i][6]+
                                returnVal[i][7]);
                sendMessage+=String.valueOf(returnVal[i][0])+String.valueOf(returnVal[i][1])+String.valueOf(returnVal[i][2])+String.valueOf(returnVal[i][3])+String.valueOf(returnVal[i][4])+String.valueOf(returnVal[i][5])+String.valueOf(returnVal[i][6])+String.valueOf(returnVal[i][7]);

            }
            System.out.println(sendMessage);
            System.out.println("making message finished!"+bmp.getWidth()+" "+bmp.getHeight());
            String testText = sendMessage;
            byte[] bytes = testText.getBytes();
            byte[] ipAddr = new byte[]{(byte) 192, (byte) 168, 4, 1};
            //TODO change IP address to desired IP address
            byte[] address = "http://192.168.4.1".getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length,
                    InetAddress.getByAddress(ipAddr), 23);
            datagramSocket.send(datagramPacket);
            System.out.println("sending ended...");

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
