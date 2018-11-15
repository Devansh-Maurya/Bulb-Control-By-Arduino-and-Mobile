#include<SoftwareSerial.h>

//We're using pin 10 and 11 in RX and TX mode
SoftwareSerial BT(10,11);
//String that will store the command from mobile device
String readdata;
char data = 0;
bool ledOn = false;
//Variables used for implementing delay mechanism
int startDelay, stopAfter;	
//Used for reading data as it is received from the Bluetooth input stream
char c;
//The two vaiables represent the two output pins to which bulbs are connected
int bulb_1 = 0, bulb_2 = 0;	

void setup()
{
    BT.begin(38400);
    Serial.begin(9600);   	//Sets the baud for serial data transmission
    pinMode(3, OUTPUT);  	//Sets digital pin 3 as output pin
    pinMode(2, OUTPUT);		//Sets digital pin 2 as output pin
}

void loop()
{
   while(BT.available())      	//Receive data as long as it is available
   {
    delay(10);
      data = BT.read();
      readdata+=data;
   }

   //Read the incoming data & store into readdata
   if(readdata.length()>0)
   {
      Serial.print(readdata);          //Print value inside data in Serial monitor
      Serial.print("\n");

      if(readdata == "1") {
      	//If bulb 1 is selected, set bulb_1 to pin 3
        bulb_1 = 3;
        //Keep the bulb off initially
        digitalWrite(bulb_1, LOW);
        //Make readdata string empty
        readdata = "";
      }

      else if(readdata == "2") {
      	//If bulb 2 is selected, set bulb_2 to pin 2
        bulb_2 = 2;
        //Keep the bulb off initially
        digitalWrite(bulb_2, LOW);
        readdata = "";
      }
      
      else if(readdata == "01") {
      	//If bulb 1 has been unchecked, make bulb_1 as 0 to disselect it
        bulb_1 = 0;
        readdata = "";
      }

      else if(readdata == "02") {
      	//If bulb 1 has been unchecked, make bulb_1 as 0 to disselect it
        bulb_2 = 0;
        readdata = "";
      }
      
      if(readdata == "STOP") {
      	//This signal is sent by pressing disconnect button on the app
      	//It makes both the bulbs non-funtional
        bulb_1 = 0;
        bulb_2 = 0;
        readdata = "";
      }
           
      if(readdata == "ON") {        
      	//This signal is sent by tapping the bulb when it is off
      	//Used to switch on the selected bulb(s)
         digitalWrite(bulb_2, HIGH);
         digitalWrite(bulb_1, HIGH);
         readdata = "";
         ledOn = true;
      }
      else if(readdata == "OFF") {
      	//This signal is sent by tapping the bulb when it is on
      	//Used to switch off the selected bulb(s)
         digitalWrite(bulb_2, LOW);
         digitalWrite(bulb_1, LOW);
         readdata = "";
         ledOn = false;
      }
      else if(readdata[0] == 'b') {
        //Get index of 'e', number following 'e' indicates the number of seconds for which the bulb(s) should remain on
        c = readdata.indexOf('e');    
        
        //Get the start delay and convert it to int
        startDelay = readdata.substring(1, c-1).toInt();
        //Get the stop delay and convert it to int
        stopAfter = readdata.substring(c+1, readdata.length()-1).toInt();

	delay(startDelay*10);
	digitalWrite(bulb_1, HIGH);
	digitalWrite(bulb_2, HIGH);

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
