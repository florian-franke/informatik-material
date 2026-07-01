import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)

/**
 * Ergänzen Sie hier eine Beschreibung für die Klasse Message.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Message extends Actor
{
    /**
     * Act - tut, was auch immer Message tun will. Diese Methode wird aufgerufen, 
     * sobald der 'Act' oder 'Run' Button in der Umgebung angeklickt werden. 
     */
    public Message (String text) {
        GreenfootImage t = new GreenfootImage(text,30, new Color(255,255,255),new Color(200,100,100,0));
        GreenfootImage mitRand = new GreenfootImage(t.getWidth()+60, t.getHeight()+30);
        mitRand.setColor(new Color(150,50,50,220));
        mitRand.fillRect(0,0, mitRand.getWidth(), mitRand.getHeight());
        mitRand.drawImage(t, 30, 15);
        setImage(mitRand);
    }
}