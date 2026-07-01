import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.List;

public class EinsatzLeiter extends Actor
{
    // Hier werden alle Roboter aufgelistet, denen der Einsatzleiter Befehle erteilen kann:
    AB9 legeRoboter; //Legeroboter
    AB9 sprengRoboter1; //Sprengroboter 1
    AB9 sprengRoboter2; //Sprengroboter 2
    AB9 aufzugRoboter; //Aufzugroboter
    
    // Roboter für Einsatz 10
    AB10 roboter1;
    AB10 roboter2;
    AB10 roboter3;

    /*#
     * Aufgabe 10: Anweisung 1
     * Hier werden Methoden am sprengRoboter1 aufgerufen.
     * Tipp: Schreibe sprengRoboter1. und druecke STRG+Leertaste --> Schau mal, welche Methoden du alle zur Verfuegung hast ...
     */
    public void holeBombeUndSprenge (int xBombe, int yBombe, int xPos, int yPos){
        // Hinweis:
        // Der sprengRoboter1 bekommt den Befehl holeBombeUndKehreZurueck(bx, by), wenn du vorher beim AB9-Roboter programmiert hast.
        // (bx|by) sind die Koordinaten der Bombe --> das muss dann der Methodenaufrufer eingeben (also du spaeter, 
        // wenn du am EinsatzLeiter diese Methode aufrufst): sprengRoboter1.holeBombeUndKehreZurueck(bx, by);
        // Gib dem sprengRoboter1 den Befehl zum Sprengen an der Position (sx,sy):
        // Kombiniere nun die richtigen Methodenaufrufe beim Aufzug- und Sprengroboter.
    }

 

    /*#
     * Ab hier kannst du Hilfs-Methoden fuer den Einsatz 9+10 implementieren:
     */

    /*#
     * Einsatz 9:
     */
    public void einsatz9() {
       // Hier kommt dein Quelltext hin
    }
   
    /*#
     * Einsatz 10:
     */
    public void einsatz10() {
       // Hier kommt dein Quelltext hin
    }
    

    /*#
     * -------------------------------------------------------------------
     * Bitte so lassen - diese Methode wird nur von euren Lehrer verwendet
     */
    public void nehmeKontaktAuf() {
        List<AB9> l9 = ((RoboterWelt)getWorld()).getObjects(AB9.class);
        if (l9.size()>0) {
            aufzugRoboter = l9.get(0);
            sprengRoboter1 = l9.get(1);
            sprengRoboter2 = l9.get(2);
            legeRoboter = l9.get(3);
        }
        List<AB10> l10 = ((RoboterWelt)getWorld()).getObjects(AB10.class);
        if (l10.size()>0) {
            roboter1 = l10.get(0);
            roboter2 = l10.get(1);
            roboter3 = l10.get(2);
        }
    }
}
