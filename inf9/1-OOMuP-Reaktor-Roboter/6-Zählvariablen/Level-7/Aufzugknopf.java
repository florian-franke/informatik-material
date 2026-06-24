import greenfoot.*;
import java.util.List;

/**
 * Spezieller Schalter zum Rufen eins Aufzugs.
 * 
 * @author Thomas Schaller
 * @version 28.06.2017 V1.0
 */
public class Aufzugknopf extends Schalter 
{
    Gegenstand aufzug;
    int stockwerk;

    /**
     * Konstruktor Aufzugknopf
     */
    public Aufzugknopf(World w)
    {
        super(w);
        aufzug = null;
    }

    /**
     * Setzt den dazugehörigen Aufzug
     * 
     * @param  aufzug   Ein Gegenstand vom Typ Aufzug
     */
    public void setAufzug(Gegenstand aufzug, int stockwerk)
    {
        this.aufzug = aufzug;
        this.stockwerk = stockwerk;
    }

    public void rufeAufzug() {
        List<Actor> beiAufzug = aufzug.getWorld().getObjectsAt(aufzug.getX(), aufzug.getY(), Actor.class);
        while(aufzug.getY() != stockwerk) {
            int neuy =aufzug.getY()-(aufzug.getY()-stockwerk)/Math.abs(aufzug.getY()-stockwerk);
            for(Actor a : beiAufzug) {    
                if(a != aufzug)           
                    a.setLocation(aufzug.getX(), neuy);
            }
            aufzug.setLocation(aufzug.getX(), neuy);
            Greenfoot.delay(1);
        }
    }

    /** Schaltet den Schalter um, d.h. von on auf off oder umgekehrt
     */
    public void umschalten() {
        anschalten();
        Greenfoot.delay(1);
        rufeAufzug();
        ausschalten();
        Greenfoot.delay(1);
    }

}
