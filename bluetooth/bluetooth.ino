#include<SoftwareSerial.h>
#define PIN A5
SoftwareSerial BT(10,11);
String readdata;
char data = 0;            //Variable for storing received data
bool ledOn = false;
int startDelay, stopAfter;
char c;
int bulb_1 = 0, bulb_2 = 0;

void setup()
{
    BT.begin(38400);
    Serial.begin(9600);   //Sets the baud for serial data transmission                               
    pinMode(3, OUTPUT);  //Sets digital pin 13 as output pin
    pinMode(2, OUTPUT);
}

void loop()
{
   while(BT.available())      // Send data only when you receive data:
   {
    delay(10);
      data = BT.read();
      readdata+=data;
   }

   //Read the incoming data & store into data
   if(readdata.length()>0)
   {
      Serial.print(readdata);          //Print Value inside data in Serial monitor
      Serial.print("\n");   

      if(readdata == "1") {
        bulb_1 = 3;
        digitalWrite(bulb_1, LOW);
        readdata = "";
      }

      else if(readdata == "2") {
        bulb_2 = 2;
        digitalWrite(bulb_2, LOW);
        readdata = "";
      }
      
      else if(readdata == "01") {
        bulb_1 = 0;
        digitalWrite(bulb_1, LOW);
        readdata = "";
      }

      else if(readdata == "02") {
        bulb_2 = 0;
        digitalWrite(bulb_2, LOW);
        readdata = "";
      }
      
      if(readdata == "STOP") {
        bulb_1 = 0;
        bulb_2 = 0;
        readdata = "";
        digitalWrite(bulb_1, LOW);
        digitalWrite(bulb_2, LOW);
      }
           
      if(readdata == "ON") {        
         digitalWrite(bulb_2, HIGH);
         digitalWrite(bulb_1, HIGH);
         readdata = "";
         ledOn = true;
      }
      else if(readdata == "OFF") {
         digitalWrite(bulb_2, LOW);
         digitalWrite(bulb_1, LOW);
         readdata = "";
         ledOn = false;
      }
      else if(readdata[0] == 'b') {
        //Get index of 'e', number following 'e' indicates the number of seconds for which the bulb(s) should remain on
        c = readdata.indexOf('e');    
        
        startDelay = readdata.substring(1, c-1).toInt();
        stopAfter = readdata.substring(c+1, readdata.length()-1).toInt();

        if(startDelay != 0) {
          delay(startDelay*10);
          digitalWrite(bulb_1, HIGH);
          digitalWrite(bulb_2, HIGH);
        }

        if(stopAfter != 0) {
          delay(stopAfter*10);
          digitalWrite(bulb_1, LOW);
          digitalWrite(bulb_2, LOW);
        }
        readdata="";
      }
      ledOn = true;
   }
}
