# RHA
RaspberryPi hardware access from android app


Arduino Code :

      #include <SPI.h>
      #include <MFRC522.h>

      #define SS_PIN 10
      #define RST_PIN 9
      MFRC522 mfrc522(SS_PIN, RST_PIN);   // Create MFRC522 instance.

      String msg ;
      String uid ;
      bool is_card_detected = false ;
      bool is_card_readable = false ;

      void setup() 
      {
        Serial.begin(9600);   // Initiate a serial communication
        SPI.begin();      // Initiate  SPI bus
        mfrc522.PCD_Init();   // Initiate MFRC522

      }
      void loop() 
      {
        // Look for new cards
        if ( ! mfrc522.PICC_IsNewCardPresent()) is_card_detected = false ;
        else is_card_detected = true ;

        // Select one of the cards
        if ( ! mfrc522.PICC_ReadCardSerial()) is_card_readable = false ;
        else is_card_readable = true ;


        //Show UID on serial monitor
        if (is_card_detected && is_card_readable){

          String content= "";
          byte letter;
          for (byte i = 0; i < mfrc522.uid.size; i++) 
          {
            content.concat(String(mfrc522.uid.uidByte[i] < 0x10 ? " 0" : " "));
            content.concat(String(mfrc522.uid.uidByte[i], HEX));
          }
          content.toUpperCase();

          uid = content.substring(1) ;

          Serial.print(uid);
          Serial.println();
          delay(1000);

       }
      }

      /*
      void readSerialPort() {
        msg = "";
        if (Serial.available()) {
          delay(10);
          while (Serial.available() > 0) {
            msg += (char)Serial.read();
          }
          Serial.flush();
        }
      }*/

        /*if (content.substring(1) == "D9 50 09 E3") //change here the UID of the card/cards that you want to give access
        {
          Serial.println("Authorized access");
          Serial.println();
          delay(3000);
        }

       else   {
          Serial.println(" Access denied");
          delay(3000);
        }
      } */

