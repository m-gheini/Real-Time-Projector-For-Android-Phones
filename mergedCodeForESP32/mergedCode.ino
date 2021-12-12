#include "LedControl.h"
#include "binary.h"
#include <SPI.h>
#include <WiFi.h>
#include <WiFiUdp.h>

//#define WHATSAPP 01000100....

int displayCnt = 4;  // # of displays connected (between 1-8)
int intensity = 3;   // Brightness of displays (between 0-15)
int idx = 0;         // Index for loops
int charNum = 0;
int newData = 0;
char packetBuffer[257];
char WHATSAPP[] =  "00000000011111110000001000000100000000100111111100000000001111110100010001000100010001000011111100000000010000000100000001111111010000000100000000000000001100100100100101001001010010010010011000000000001111110100010001000100010001000011111100000000011111110100010001000100010001000011100000000000011111110100010001000100010001000011100000000000";
int WHATSAPP_SIZE = 12;

char TELEGRAM[] =  "00000000010000000100000001111111010000000100000000000000011111110100100101001001010010010100000100000000011111110000000100000001000000010000000100000000011111110100100101001001010010010100000100000000001111100100000101000001010001010010011000000000011111110100010001000100010001000011101100000000001111110100010001000100010001000011111100000000011111110010000000010000001000000111111100000000";
int TELEGRAM_SIZE = 22;

char MESSAGE[] =  "0000000000000000000000000000000000000000000000000000000000000000001001100100100101001001010010010011001000000000011111110010000000010000001000000111111100000000001001100100100101001001010010010011001000000000000000000000000000000000000000000000000000000000";
int MESSAGE_SIZE = 29;

const char * ssid = "HUAWEI-FC47";
const char * pwd = "06598602";

const char * udpAddress = "192.168.8.1";
const int udpPort = 23;

LedControl lc=LedControl(23,18,15,displayCnt);  // Pins: DIN,CLK,CS, # of Displays

WiFiUDP udp;

void processImage(char* packetBuffer){
//  Serial.println(packetBuffer);
  for (int display = 0; display <= displayCnt - 1; display++)  {
    for (int col = 7; col >= 0 ; col--) {
      for (int row = 0; row <= 7; row++)  {
//        Serial.println(charNum);
//        Serial.println(packetBuffer[charNum] == '1');
        if(packetBuffer[charNum] == '1'){
//          Serial.print("heart, display, col, row : ");
//          Serial.print(packetBuffer[charNum]);
//          Serial.print(display);
//          Serial.print(col);
//          Serial.println(row);
          lc.setLed(display,row,col,true);
        }
        else{
//          Serial.print("heart, display, col, row : ");
//          Serial.print(packetBuffer[charNum]);
//          Serial.print(display);
//          Serial.print(col);
//          Serial.println(row);
          lc.setLed(display,row,col,false);
        }
        charNum++;
      }
    }
  }
  charNum = 0;
}

void scrollText(char packetBuffer[], int size ){
  for(int i = 0; i < size; i++) { 
    for (int display = 3; display >= 0; display--)  {
      for (int col = 0; col <= 7 ; col++) {
        for (int row = 0; row <= 7; row++)  {
//          Serial.println(charNum);
//          Serial.println(packetBuffer[charNum] == '1');
          if(packetBuffer[charNum] == '1'){
//            Serial.print("heart, display, col, row : ");
//            Serial.print(packetBuffer[charNum]);
//            Serial.print(display);
//            Serial.print(col);
//            Serial.println(row);
            lc.setLed(display,row,col,true);
          }
          else{
//            Serial.print("heart, display, col, row : ");
//            Serial.print(packetBuffer[charNum]);
//            Serial.print(display);
//            Serial.print(col);
//            Serial.println(row);
            lc.setLed(display,row,col,false);
          }
          charNum++;
        }
      }
    }
    delay(500);
   charNum = (i+1) * 8;
  }
  charNum = 0;
}

bool compare(char packetBuffer[], char in[], int inSize) {
  Serial.println(packetBuffer);
  Serial.println(sizeof(in));
  
  for(int i = 0; i < inSize; i++) {
    Serial.println("***********");
    if (packetBuffer[i] != in[i]) {
      
      return false;
  }
  }
  return true;
}

void processText(char* packetBuffer){
  if (compare(packetBuffer, "com.whatsapp", WHATSAPP_SIZE)) {
    scrollText(WHATSAPP, 11);
  }
  else if (compare(packetBuffer, "org.telegram.messenger", TELEGRAM_SIZE)) {
    scrollText(TELEGRAM, 17);
  }
  else if (compare(packetBuffer, "com.samsung.android.messaging", MESSAGE_SIZE)) {
    processImage(MESSAGE);
  }
}

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, pwd);
  Serial.println("");

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  udp.begin(udpPort);
  
  // IMAGE
  for(idx=0; idx<displayCnt;idx++) {
    lc.shutdown(idx,false);  // Wake up displays
  }
  for(idx=0; idx<displayCnt;idx++) {
    lc.setIntensity(idx,intensity);  // Set intensity levels
  }
  for(idx=0; idx<displayCnt;idx++) {
    lc.clearDisplay(idx);  // Clear Displays
  }

}



void loop() {
  int packetSize = udp.parsePacket();
  if(udp.read(packetBuffer,257) > 0) {
    if (packetBuffer[0] == '1' || packetBuffer[0] == '0'){
      Serial.println("IN IMAGE!!!!");
      processImage(packetBuffer); 
    }
    else {
      Serial.println("IN TEXT!!!!");
//      Serial.println(packetBuffer);
      processText(packetBuffer);
    }
  }
}
