package org.example.mb1414_fx;

import jssc.SerialPortEvent;
import jssc.SerialPortException;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;

public class Mb1414 extends LiaisonSerie {
    double distance = 0.0;
    double distanceInit = 0.0, vitesseInit = 0.0;
    double distanceFinal, distanceDelta, vitesseFinal, vitesseDelta, acceleration;
    DecimalFormat dfDistance = new DecimalFormat("0.## sm");
    DecimalFormat dfDeltaT=new DecimalFormat("0.## s");
    DecimalFormat dfVitesseAcceleration = new DecimalFormat("0.##");
    Instant tInit, tFinal;
    public Mb1414() {
        tInit=Instant.now();
    }
    @Override
    public void serialEvent(SerialPortEvent event) {
        tFinal=Instant.now();
        calcul(event);
        if (distanceDelta != 0) {
            System.out.printf("""
                %s
                __________________________
                Delta d = %s
                Delta t = %s
                Vitesse = %s sm/s
                Acceleration = %s sm square
                %n""", dfDistance.format(distance), dfDistance.format(distanceDelta), dfDeltaT.format((Duration.between(tInit,tFinal).toMillis())*0.001), dfVitesseAcceleration.format(vitesseFinal), dfVitesseAcceleration.format(acceleration));
            tInit=tFinal;
        }
    }

    private void calcul(SerialPortEvent event) {
        byte[] laTrame = lireTrame(event.getEventValue());
        if(laTrame.length==8){
            Instant.now();
            distance=((laTrame[1]-0x30)*100 + (laTrame[2]-0x30)*10 + (laTrame[3]-0x30))*2.54;
        }
        distanceFinal = distance;
        distanceDelta = distanceFinal - distanceInit;
        distanceInit = distanceFinal;

        if (distanceDelta != 0) {
            vitesseFinal = (((distanceDelta))/((Duration.between(tInit, tFinal).toMillis())*0.001));
            vitesseDelta = vitesseFinal - vitesseInit;
            vitesseInit = vitesseFinal;
            acceleration = (((vitesseDelta))/((Duration.between(tInit, tFinal).toMillis()) * 0.001));
        }
    }

    public void initialisationCapteur(String lePortCom) throws SerialPortException {
        super.initCom(lePortCom);
        super.configurerParametres(57600, 8, 0, 1);
    }
    public void fermerLiaison() {
        super.fermerPort();
    }

    public double getDistanceDelta() {
        return distanceDelta;
    }

    public double getVitesseFinal() {
        return vitesseFinal;
    }
}