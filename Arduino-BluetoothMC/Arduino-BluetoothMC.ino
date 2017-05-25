//call the library and define the rx and tx pins for send and receive
#include <SoftwareSerial.h>
SoftwareSerial bluetooth(10,11);  //RX,TX

//char used to read the input
char data=0;

//put a led in pin 5 in arduino
int led = 5;

//pot setup and value
int potPin=A0;
int potValue=0;


//temp setup and value
int tempPin=A1;
float readValue , realValue;
int finalValue;

//ldr setup and value
int ldrPin=A2;
int ldrValue;

//button setup
int button =6;
int button_state=0;

void setup() {

//intainalize the Serial and the bluetooth with 9600 boot rat 
bluetooth.begin(9600);

//define the input and the output for the arduio
pinMode(led,OUTPUT);
pinMode(potPin,INPUT);
pinMode(tempPin,INPUT);
pinMode(ldrPin,INPUT);
pinMode(button,INPUT);

}

void loop() {

//read the value from the pot
potValue =analogRead(potPin);

//read the value from the temp
readValue=analogRead(tempPin);
realValue = readValue*5 /1023;
finalValue = realValue*100;

//read value from LDR
ldrValue=analogRead(ldrPin);

button_state=digitalRead(button);

sendAndroidValues();

    if(bluetooth.available()){
      data = bluetooth.read();
      if(data == 'o'){
        digitalWrite(led,HIGH);
        }
        else if(data == 'f'){
          digitalWrite(led,LOW);
          }
      } 
}
   



//send the value from the sensor over serial to BT module
void sendAndroidValues()
{
  //put # before the value so that the app know that new data send
  bluetooth.print('#');

  bluetooth.print(finalValue);
  bluetooth.print('+');

  bluetooth.print(ldrValue);
  bluetooth.print('+');

 //put the sensors value
  bluetooth.print(potValue);
  bluetooth.print('+');

  //put the button state
  bluetooth.print(button_state);
  bluetooth.print('+');


  //used to end the transimssion
  bluetooth.print('~');
  bluetooth.println();
  //add delay to eliminate missed data
  delay(150);
  }
