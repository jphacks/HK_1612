long a=0,b=0,c=0;

void setup()
{
  Serial.begin(38400);
}

void loop()
{
  a=millis();
  b=analogRead(0);
  c=analogRead(1);
  Serial.print(a);
  Serial.print(",");
  Serial.print(b);
  Serial.print(",");
  Serial.print(c);
  Serial.println(",   ");
//  delay(3);
}
