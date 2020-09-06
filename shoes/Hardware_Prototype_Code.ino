 #define trigPin 13
#define echoPin 12
#define motor 7
#include <Servo.h>
Servo servo;
const int  trig = 2, echo = 3 ;
int pos = 0, distcm = 0;
void setup()
{ 
  Serial.begin(9600);
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);
  servo.attach(9);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(motor, OUTPUT);
}

void loop()
{ 
  for (pos = 0; pos <= 180; pos += 20)
  {
    servo.write(pos);
    delay(50);
    Serial.println(radar());
  }
  for (pos = 180; pos >= 0; pos -= 20)
  {
    servo.write(pos);
     delay(50);
    Serial.println(radar());   
  }
}
long radar(void)
{
  digitalWrite(trig, HIGH);
  delayMicroseconds(15);
  digitalWrite(trig, LOW);
  long  dur = pulseIn(echo, HIGH);
  distcm = dur / 58;
  
  long duration, distance;
digitalWrite(trigPin, LOW);
delayMicroseconds(2);
digitalWrite(trigPin, HIGH);
delayMicroseconds(10);
digitalWrite(trigPin, LOW);

duration = pulseIn(echoPin, HIGH);
distance = (duration/2) / 29.1;

if (distance<=50) 
{
digitalWrite(motor,HIGH);
} else
{
digitalWrite(motor,LOW);
} delay(500);
return distcm;
}
