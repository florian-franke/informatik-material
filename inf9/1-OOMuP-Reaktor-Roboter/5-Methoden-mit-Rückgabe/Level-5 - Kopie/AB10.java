import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class AB10 extends AB3
{
    /*#
     * Aufgaben 1, 2 und 3: Patrouille
     */
    public void rundeDrehen() {
        int anzGemacht;
        anzGemacht = 0;
        while (anzGemacht < 3) {
            laufeBisWand();
            dreheUm();
            anzGemacht++;
        }
    }

    /*#
     * Aufgabe 4: Zu Befehl!
     */
    public void dreheAnzahlRunden(int anz) {
        // Hier kommt dein Quelltext hin
    }

    /*#
     * Aufgabe 5: Lauf x Schritte
     */ 

    /*#
     * Aufgabe 6: Was tut es?
     * Analysiere diese Methode
     */
    public void wastutes(int anz) {
        int i=0;
        while(i<anz) {
            while(!this.istWandVorne()) {
                aufnehmen();
                einsVor();
            }
            dreheUm();
            i++;
        }
    }

    /*#
     * Aufgabe 7: Aufräumen
     */
    public void aufraeumen() {

    }

    /*
     * Hilfsmethode, die den Aufzug holt.
     */ 
    public void holeAufzug() {
        if(!istVorne("Schalter")) {
            dreheUm();
        }
        benutze("Schalter");
    }

    /*#
     * Aufgabe 8: Aufzug
     */

    /*
     * Hilfesmethode für den Einsatz 10
     */
    public boolean einsVorSammle() {
        if (istVorneFrei() && getEnergie()>0) {
            einsVor();
            if(istAufGegenstand("Diamant")) {aufnehmen();}
            return true;
        } else {
            return false;
        }
    }

    /*#
     * Einsatz 10 bitte beim Einsatzleiter implementieren
     */
}
