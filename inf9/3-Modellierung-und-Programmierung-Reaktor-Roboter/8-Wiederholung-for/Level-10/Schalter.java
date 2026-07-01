import greenfoot.*;
import java.util.*;

/**
 * Die Klasse Schalter ist eine Hilfsklasse für die Schlösser und Schalthebel 
 * Sie hat zwei zweidimensionale Arrays, die für den Schalterzustand on bzw. off
 * festlegen, wie ein Bereich der Welt aussehen soll. startx und starty speichern
 * die linke obere Ecke dieses Bereichs.
 */

public class Schalter
{
    RoboterWelt   world;
    private boolean an;
    private int startx;
    private int starty;
    private int[][] on;
    private int[][] off;

    /** Der Konstruktor erstellt einen neuen Schalter
     * @param w Die Welt, die manipuliert werden soll
     */
    public Schalter(World w) {
        super();
        an = true;
        startx = 0;
        starty = 0;
        on = null;
        off = null;
        world = (RoboterWelt) w;
    }

    /** Die Methode setze verändert die Welt entsprechend den Vorgaben
     * von backg. Dabei werden alle Objekte in diesem Bereich entfernt 
     * (außer den Robotern) und gegen neue Objekte ausgetauscht.
     * @param backg Zweidimensionales Array, das die neuen Actors für den Bereich festlegt.
     */

    private void setze(int[][] backg) {

        for(int x=0; x<backg[0].length; x++) {
            for(int y=0; y<backg.length; y++) {
                if (backg[y][x]>=0 && (startx+x>0) && (startx+x<world.getWidth()-1) &&
                (starty+y>1) && (starty+y<world.getHeight()-1)) {
                    List al = world.getObjectsAt(x+startx,y+starty, null);
                    for (int i = 0; i< al.size(); i++) {
                        Actor da = (Actor) (al.get(i));
                        if (! (da instanceof Roboter))  world.removeObject(da);
                    }

                    if(backg[y][x]>=0 && backg[y][x] <10) {
                        world.changeBackground(backg[y][x], x, y);
                    }
                    Actor a = ((RoboterWelt) world).generateActor(backg[y][x]);
                    if (backg[y][x] == 11 || backg[y][x]==12 || backg[y][x]==17 || backg[y][x]==22 || backg[y][x]==23) {
                        ((Wand) a).setSchalter(this);
                    }  
                    if (backg[y][x] == 31) {
                        ((Gegenstand) a).setSchalter(this);
                    }  
                    if (a!= null) world.addObject(a,x+startx,y+starty);
                }
            }
        }
    }

    /** Setzt den Schalterzustand auf on
     */  
    public void anschalten() {
        an = true;
        if (on != null) setze(on);
    }

    /** Setzt den Schalterzustand auf off
     */  

    public void ausschalten() {
        an = false;
        if (off != null) setze(off);
    }

    /** Schaltet den Schalter um, d.h. von on auf off oder umgekehrt
     */
    public void umschalten() {
        if (an) ausschalten(); else anschalten();
    }

    /** Legt fest, wie der Schalter die Welt verändern soll.
     * @param x Koordinate der linke oberen Ecke des zu verändernden Bereichs
     * @param y Koordinate der linke oberen Ecke des zu verändernden Bereichs
     * @param on Zweidimensionales Array zu Festlegung, wie der Bereich bei angeschaltetem Schalter ausssehen soll
     * @param off Zweidimensionales Array zu Festlegung, wie der Bereich bei ausgeschaltetem Schalter ausssehen soll
     */
    public void setPosition(int x, int y, int[][] on, int[][] off) {
        startx = x;
        starty = y;
        this.on = on;
        this.off = off;
    }

    /** Liefert den Schalterzustand
     * @return true, wenn der Schalter an ist, sonst false
     */
    public boolean getAn() {
        return an;
    }
}
