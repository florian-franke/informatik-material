import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class AB5 extends Roboter
{
    /*#
     * Aufgabe 1, 2, 3 und 10: Upgrade 1 - ein neuer Sensor wird erstellt
     */
    public boolean istFassVorne() {
        // Aufgabe 3a
        
        if (istVorne("Fass") || istVorne("Atommuell")) {
            // Aufgabe 3b

            return true;
        } else {
            // Aufgabe 3b

            return false;
        }
        // Aufgabe 3c

    }

    /*#
     * Aufgabe 4: Rueckspiegel
     */
    public boolean istFassHinten() {
        dreheUm();
        if (istVorne("Fass")) {
            dreheUm();
            return true;
        }else {
            dreheUm();
            return false;
        }
    }

    /*#
     * Aufgabe 5: Out of Power
     */
    public boolean istEnergieSchwach() {
        // Hier fehlt noch einiges!
        if(getEnergie()<20){
            return true;
        }
        else {
            return false;
        }
    }

    /*#
     * Aufgabe 6: Heavy Duty
     */
    // Hier ist Platz fuer die Methode istSchwerBeladen():
    public boolean istSchwerBeladen(){
        if(getAnzahl()<5){
            return false;
        }
        else {
            return true;
        }
    }

    /*#
     * Aufgabe 7 und 11: Look Ahead - Upgrade 2
     */
    // Hier ist Platz fuer die Methode istVorFassFrei():
    public boolean istVorFassFrei(){
        if(istFassVorne()){
                dreheRechts();
                einsVor();
                dreheLinks();
                einsVor();
                if (istWandVorne() || istVorne("Fass") || istVorne("Atommuell")){ //Bild 1
                    dreheUm();
                    einsVor();
                    dreheRechts();
                    einsVor();
                    dreheRechts();
                    return false;
                }
                else {
                    einsVor();
                    dreheLinks(); //Aufgabe 12
                    if(istWandVorne() || istVorne("Fass") || istVorne("Atommuell")){ //Bild 2
                        dreheRechts(); //Aufgabe 12
                        dreheUm();
                        einsVor();
                        einsVor();
                        dreheRechts();
                        einsVor();
                        dreheRechts();
                        return false;
                    }
                    else { //Bild 3
                        dreheUm();
                        einsVor();
                        einsVor();
                        dreheRechts();
                        einsVor();
                        dreheRechts();
                        return true;
                    }
                }
        }
        else {
            return false;
        }
    }
    


    /*#
     * Aufgabe 8: Aufraeumen
     */
    // Hier ist Platz fuer die Methode schiebeFassBisWand():

    /*#
     * Aufgabe 9: Fuehrerschein
     */
    // Hier ist Platz fuer die Methode istKreuzung():

    public boolean istKreuzung(){
        if(!istWandLinks() && !istWandRechts() && !istWandVorne()){
            return true;
        }
        else {
            return false;
        }
    }
    
    public void geheBisKreuzung() {
        while(!istKreuzung()){
            einsVor();
        }
    }

    /*#
     * Aufgabe 12: Wie koennte man diese Methode nennen?
     */
    // Hier ist Platz fuer die Methode ???():

    /*#
     * Einsatz 5: Bitte den Namen nicht aendern!
     */
    public void einsatz5() {

    }
}