import greenfoot.*; // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.util.Random;
import java.util.List;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import java.io.File;
import java.nio.file.*;

/**
 * Die einzigen aktiven Akteure in der Roboterwelt sind die Roboter.
 * Die Welt besteht aus 14 * 10 Feldern.
 */
public class RoboterWelt extends World
{
    private static int zellenGroesse = 60;
    private boolean schwerkraft;
    private ReadWrite secret = new ReadWrite();
    private GreenfootImage[] backgroundimages;
    private int[][] map;

    /**
     * Erschaffe eine Welt mit 14 * 12 Zellen.
     */
    public RoboterWelt()
    {
        super(14, 12, zellenGroesse);
        backgroundimages = new GreenfootImage[8];
        backgroundimages[0] = new GreenfootImage("images/Bodenplatte.png");
        backgroundimages[1] = new GreenfootImage("images/warnung1.png");
        backgroundimages[2] = new GreenfootImage("images/bluerock1.png");
        backgroundimages[3] = new GreenfootImage("images/Kontaktplatte.png");
        backgroundimages[4] = new GreenfootImage("images/Bodenplatte1.png");
        backgroundimages[5] = new GreenfootImage("images/bluerock2.png");
        backgroundimages[6] = new GreenfootImage("images/bluerock3.png");
        backgroundimages[7] = new GreenfootImage("images/Aufzugschacht.png");

        setBackground("images/Bodenplatte.png");

        setActOrder(Roboter.class);
        setPaintOrder(Message.class, Roboter.class, Gegenstand.class, Wand.class);

        Greenfoot.setSpeed(25);
        if (secret.getLevel()==-1) secret.init(); // Falls fehlerhafte Secret-Datei

        if (secret.getLevel()==1) ab1_roboterSteuern();
        if (secret.getLevel()==2) ab2_roboterProgrammieren();
        if (secret.getLevel()==3) ab3_wiederholer();
        if (secret.getLevel()==4) ab4_verzweiger();
        if (secret.getLevel()==5) ab5_funktionen();
        if (secret.getLevel()==6) ab6_attribute();
        if (secret.getLevel()==7) ab7_vererbung();
        if (secret.getLevel()==8) ab8_lokale_variablen();
        if (secret.getLevel()==9) ab9_parameter();
        if (secret.getLevel()==10) ab10_zaehlschleifen();
        if (secret.getLevel()==11) ab11_finalBild();
    }

    /**
     * Das Roboterszenario auf die Anfangswerte (Level 1) zurücksetzen
     */
    public void neuStart() {
        secret.init();
        ab1_roboterSteuern();
    }

    /**
     * In ein niedrigeres Level wechseln
     * @param int   Nummer des Levels, in das gewechselt werden soll
     */
    public void geheZuLevel (int i) {

        if (i>0 && i<=secret.getMaxLevel()) {
            secret.writeLevel(i,true);
            Greenfoot.setWorld(new RoboterWelt());
        } else {
            melde("Du hast erst Level "+secret.getMaxLevel()+ " erreicht. ");
        }
    }

    /** 
     * Secret-Datei laden, um ein höheres Level freizuschalten
     * 
     */
    public void ladeSecretDatei() {
        int oldLevel = secret.getLevel();
        try{ 
            JFileChooser fileChooser = new JFileChooser(".");
            fileChooser.setDialogTitle("Secret-Datei laden");
            fileChooser.setApproveButtonText("Level-Datei laden");
            JFrame fcframe = new JFrame();
            fcframe.toFront();
            fcframe.setAlwaysOnTop(true);

            
            if(fileChooser.showOpenDialog(fcframe)==JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                    Path FROM = file.toPath();
                    Path TO   = Paths.get(".\\secret");
                    //overwrite existing file, if exists
                    CopyOption[] options = new CopyOption[]{
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES
                        };
                    Files.copy(FROM, TO, options);
                    int max = secret.getMaxLevel();
                    int level = secret.getLevel();
                    if(max >= 1 && max <= 11 && level>=1 && level <=11) {
                      melde("Level 1-"+max+" sind nun freigeschaltet. Gehe zu Level "+level+".");
                    } else {
                      throw new IllegalArgumentException( "Keine richtige Secret-Datei." ); 
                    }
                    Greenfoot.setWorld(new RoboterWelt());
                }
            }
        }
        catch(Exception e) {
            melde("Fehlerhafte Secret-Datei. Bleibe in Level "+oldLevel+".");
            secret.init();
            secret.writeLevel(oldLevel,true);
            Greenfoot.setWorld(new RoboterWelt());
        }

    }

    /**
     * Die Welt wird geleert
     */
    private void weltLeeren()
    {
        removeObjects(getObjects(null));
    }

    /**
     * Liefert eine Referenz auf die Map
     */
    public int[][] getMap() {
        return map;
    }

    /** 
     * Liefert die Information, ob es sich um eine Welt mit Schwerkraft handelt
     */
    public boolean getSchwerkraft() {
        return schwerkraft;
    }

    /** 
     * Überschrift oben links ausgeben.
     */
    private void zeichneUeberschrift(String text) {
        this.getBackground().setColor(Color.LIGHT_GRAY);
        this.getBackground().fillRect(0,0,300,20);
        this.getBackground().setColor(Color.BLACK);
        this.getBackground().setFont(new Font("Verdana",true,false,12));
        this.getBackground().drawString(text, 2, 14);
    }

    public Actor generateActor(int nr) {
        Actor a = null;
        switch(nr) {
            case 10: a = new Wand(); break;
            case 11: a = new Wand("SchalterEin"); break;
            case 12: a = new Wand("SchalterAus"); break;

            case 13: a = new Wand("Stromquelle1"); break;
            case 14: a = new Wand("Stromquelle2"); break;
            case 15: a = new Wand("Stromquelle3"); break;
            case 16: a = new Wand("Stromquelle4"); break;
            case 17: a = new Wand("Schloss"); break;

            case 18: a = new Wand("Tuer1"); break;
            case 19: a = new Wand("Tuer2"); break;
            case 20: a = new Wand("Stein"); break;
            case 21: a = new Wand("Steine"); break;

            case 22: a = new Wand("Rufknopf"); break;
            case 23: a = new Wand("RufknopfAus"); break;

            case 30: a = new Gegenstand("Aufzug");  break;
            case 31: a = new Gegenstand("Kontaktplatte"); break;
            case 32: a = new Gegenstand("Strom"); break;
            case 33: a = new Gegenstand("Portal"); break;
            case 34: a = new Gegenstand("Schraube"); break;
            case 35: a = new Gegenstand("Brennstab"); break;
            case 36: a = new Gegenstand("Schluessel"); break;
            case 37: a = new Gegenstand("Oelfleck"); break;
            case 38: a = new Gegenstand("Akku"); break;
            case 40: a = new Gegenstand("Feuer"); break;
            case 41: a = new Gegenstand("Feuerloescher"); break;
            case 42: a = new Gegenstand("Atommuell"); break;
            case 43: a = new Gegenstand("Fass"); break;
            case 44: a = new Gegenstand("Bombe"); break;
            case 45: a = new Gegenstand("Diamant"); break;

        }
        return a;
    }

    public GreenfootImage getBackgroundImage(int nr) {
        GreenfootImage back = backgroundimages[0];
        switch(nr) {
            case 1: back = backgroundimages[1]; break;
            case 2: back = backgroundimages[2]; break;
            case 3: back = backgroundimages[3]; break;
            case 4: back = backgroundimages[4]; break;
            case 5: back = backgroundimages[5]; break;
            case 6: back = backgroundimages[6]; break;
            case 7: back = backgroundimages[7]; break;
            case 30: back = backgroundimages[7]; break;
        }
        return back;
    }

    /**
     * Generiert eine Welt aus dem angegeben 2-dim. Array
     * Dabei werden sowohl die Hintergrundbilder gesetzt, als auch Objekte
     * in die Welt eingefügt.
     * @param backg 2-dim. Array, das die Welt beschreibt.
     */
    private void generateWorld(int[][] backg)
    {
        weltLeeren();
        map = backg;
        GreenfootImage back = new GreenfootImage(840,720);
        for(int x=0; x<14; x++) {
            for(int y=0; y<12; y++) {
                back.drawImage(getBackgroundImage(backg[y][x]), x*zellenGroesse, y*zellenGroesse);
                Actor a = generateActor(backg[y][x]);
                if (a != null) addObject(a,x,y);
            }
        }
        this.setBackground(back);
        schwerkraft = false;
    }

    /**
     * Ändert den Hintergrund einer Zelle
     */
    public void changeBackground(int typ, int x, int y) {
        GreenfootImage back = this.getBackground();
        back.drawImage(getBackgroundImage(typ), x*zellenGroesse, y*zellenGroesse);
    }

    /**
     * Zeigt die Koordinaten aller Felder an
     */
    public void zeigeKoordinaten(){
        int delta;
        for (int x=0; x<getWidth(); x++){
            for (int y=0; y<getHeight(); y++){
                this.getBackground().setColor(Color.BLACK);
                this.getBackground().setFont(new Font("Verdana",false,false,12));
                if (x>9 || y>9) {
                    delta = 4;
                    if (x>9 && y>9) delta = 8;
                }
                else delta = 0;
                this.getBackground().drawString("("+x+"|"+y+")", x*60+14-delta, y*60+35);
            }
        }
    }

    /**
     * Erzeugt die Welt neu, damit die Koordinaten wieder verschwinden
     */
    public void versteckeKoordinaten(){
        Greenfoot.setWorld(new RoboterWelt());
    }

    /**
     * Für Welten mit Schwerkraft berechnet diese Methode, wie die Gegenstände fallen müssen.
     * 
     */
    private void nurSchwerkraftObjekte(List<Actor> l) {
        Iterator<Actor> it = l.iterator();
        while(it.hasNext()){
            Actor a = it.next(); 
            if(a instanceof Gegenstand) {
                if((((Gegenstand) a).getName()).equals("Aufzug")) it.remove();
            }
            if(a instanceof Wand) {
                if(!(((Wand) a).getName()).equals("Steine")) it.remove();
            }
        }
    }

    public void schwerkraftAnwenden() {
        if(schwerkraft) {
            List<Actor> ag = new ArrayList<Actor>();
            ag.addAll(this.getObjects(Gegenstand.class));
            ag.addAll(this.getObjects(Wand.class));
            ag.addAll(this.getObjects(Roboter.class));

            nurSchwerkraftObjekte(ag);
            List<Actor> gefallen = new ArrayList<Actor>();
            gefallen.add(new Gegenstand());
            while(gefallen.size()>0) {
                gefallen.clear();

                for(Actor g : ag) {
                    // Wenn auf Aufzug nicht mehr fallen      
                    List<Gegenstand> gg = getObjectsAt(g.getX(), g.getY(), Gegenstand.class);
                    boolean aufAufzug=false;
                    for(Gegenstand ggg : gg) {
                        if (ggg.getName().equals("Aufzug")) aufAufzug=true;
                    }

                    if(!aufAufzug) {
                        boolean wandunten=false;
                        List<Actor> u = this.getObjectsAt(g.getX(),g.getY()+1, Actor.class);
                        Iterator<Actor> it = u.iterator();
                        while(it.hasNext()) {
                            Actor uu = it.next();
                            if ((uu instanceof Gegenstand) && ((Gegenstand) uu).getName().equals("Aufzug")) it.remove();
                            if ((uu instanceof Wand) && !((Wand) uu).getName().equals("Steine")) wandunten=true;
                        }
                        if(u.size()==0 ) {
                            int rot = g.getRotation();
                            g.setRotation(90);
                            g.setLocation(g.getX(),g.getY()+1);
                            gefallen.add(g);
                            g.setRotation(rot);
                        } 

                        if((g instanceof Roboter) && gefallen.contains(g)) {
                            ((Roboter) g).verbraucheEnergie(20);
                        } 
                        if(!(g instanceof Roboter) && !wandunten) {
                            List<Actor> r = this.getObjectsAt(g.getX()+1,g.getY(), Actor.class);
                            List<Actor> ru = this.getObjectsAt(g.getX()+1,g.getY()+1, Actor.class);
                            if(!gefallen.contains(g) && r.size()==0 && ru.size()==0) {
                                g.setLocation(g.getX()+1,g.getY()+1);
                                gefallen.add(g);
                            } 
                            List<Actor> l = this.getObjectsAt(g.getX()-1,g.getY(), Actor.class);
                            List<Actor> lu = this.getObjectsAt(g.getX()-1,g.getY()+1, Actor.class);
                            if(!gefallen.contains(g) && l.size()==0 && lu.size()==0) {
                                g.setLocation(g.getX()-1,g.getY()+1);
                                gefallen.add(g);
                            } 
                            if ((g instanceof Wand) && gefallen.contains(g)) {
                                List<Roboter> ro = this.getObjectsAt(g.getX(), g.getY()+1, Roboter.class);
                                for(Roboter rob : ro) {
                                    rob.verbraucheEnergie(80);
                                }
                            }
                        }
                    }
                }
                Greenfoot.delay(4);
            }
        }
    }

    /* Es folgen nun die Definitionen der Level und Einsätze                      */
    /* ---------------------------------------------------------------------------*/

    private void ab1_roboterSteuern()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10, 0, 0, 0, 0, 0,10, 0,34, 0, 0, 0, 0,10},
                {10, 0,34,34,34, 0,10,38, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0,10,34, 0,10,10,10, 0,10},
                {10,10,10, 0,10,10,10, 0, 0,10, 0, 0, 0,10},
                {31, 3, 0, 0,10,10,10,31, 0,10,33, 0, 0,10},
                {10,10,10, 0,10,10,10, 0, 0,10, 0, 0, 0,10},
                {10, 0, 0,34, 0, 0,10, 0, 0,10,10,10, 0,10},
                {10,34,34,34, 0, 0,10, 0, 0,34, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0,10, 0,38, 0,34, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);
        zeichneUeberschrift("AB1 - Roboter von Hand steuern");

        AB1 starter = new AB1();
        addObject(starter, 0, 6);
        AB1 starter1 = new AB1();
        addObject(starter1, 7, 6);
        starter1.verbraucheEnergie(50);
        Schalter s = new Schalter(this);
        int[][] on={{19}}; 
        int[][] off={{31}};
        s.setPosition(1, 6, on, off);
        s.ausschalten();
        Gegenstand g = (Gegenstand) (this.getObjectsAt(0, 6, Gegenstand.class).get(0));
        g.setSchalter(s);
    }

    private void ab2_roboterProgrammieren()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10, 0, 0, 0, 0, 0,10, 0,34,38,38,34, 0,10},
                {10, 0,34,34,34, 0,10, 0, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0,10, 0, 0,10,10,10, 0,10},
                {10,10,10, 0,10,10,10, 0, 0,10, 0, 0, 0,10},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0,10, 0,10},
                {10,10,10, 0,10,10,10, 0, 0,10, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0,10, 0, 0,10,10,10, 0,10},
                {10, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);
        zeichneUeberschrift("AB2 - Methoden erstellen");
        AB2 ab21 = new AB2();
        addObject(ab21, 1, 3);
        AB2 ab22 = new AB2();
        addObject(ab22, 7, 2);
        AB2 ab23 = new AB2();
        addObject(ab23, 10, 6);
        AB2 ab24 = new AB2();
        addObject(ab24,  1, 10);
    }

    public void einsatz_02()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10, 0, 0, 0, 0, 0, 0, 0},
                {10, 0, 0, 0, 0,33,10, 0, 0, 0, 0, 0, 0, 0},
                {10, 0, 0,35, 0, 0,10, 0, 0, 0, 0, 0, 0, 0},
                {10, 0, 0,38, 0, 0,10, 0, 0, 0, 0, 0, 0, 0},
                {10, 0, 0,35, 0, 0,10, 0, 0, 0, 0, 0, 0, 0},
                {10, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0},
                {10, 0, 0,10, 0, 0,10, 0, 0, 0, 0, 0, 0, 0},
                {10,10, 0,38, 0,10,10, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10, 1,10,10,10, 0, 0, 0, 0, 0, 0, 0},
                { 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10, 0, 0, 0, 0, 0, 0, 0}};

        weltLeeren();
        generateWorld(backg);
        zeichneUeberschrift("Einsatz 2: Brennstäbe sichern");
        AB2 ab2 = new AB2();
        addObject(ab2,  0, 10);
        ab2.einsatz2();
        Greenfoot.delay(2);
        if (ab2.istAufGegenstand("Portal") && ab2.getAnzahl("Brennstab")==2) {
            secret.writeLevel(3);
            melde("Super! Du hast die Brennstäbe gesichert");
        } else {
            warne("Du hast die Aufgabe noch nicht erfüllt");
        }
        Greenfoot.setWorld(new RoboterWelt());
    }

    private void ab3_wiederholer()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10, 0,41, 0, 0,40, 0,10, 0, 0, 0, 0, 0,10},
                {10, 0,41, 0, 0, 0,40,10, 0, 0, 0, 0, 0,10},
                {10, 0,41, 0, 0,40,40,10, 0,10,10,10, 0,10},
                {10, 0,41, 0,40,40,40,10, 0,10, 0,10, 0,10},
                {10, 0, 0, 0, 0, 0, 0,10, 0, 0, 0,10, 0,10},
                {10, 0,10,10,10,10,10,10,10,10,10,10, 0,10},
                {10, 0,10,41,10,41,10,41,10,41,10,10, 0,10},
                {10, 0,31, 0,31, 0,31, 0,31, 0,31,10, 0,10},
                {10, 0,10, 0,40,40,40,40,40,40,10, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);
        zeichneUeberschrift("AB3 - Wiederholungen mit while-Schleifen");
        AB3 ab;
        ab= new AB3();
        addObject(ab, 1, 2);
        ab= new AB3();
        addObject(ab, 1, 3);
        ab= new AB3();
        addObject(ab, 1, 4);
        ab= new AB3();
        addObject(ab, 1, 5);
        ab= new AB3();
        addObject(ab, 2, 9);
        ab= new AB3();
        addObject(ab, 11, 10);
    }

    public void einsatz_03()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,41,10,41,10,41,10,41,10,41,10,10,10,10},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,31,10,10,10},
                {10,10,10,10,10,10,10,10,10,10, 3,10,10,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0,31,10,10,10},
                {10, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0,10,10,10},
                {10, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0,10,10,10},
                {10, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0,10,10,10},
                {10, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0,10,10,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0,31, 3,33,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);
        zeichneUeberschrift("Einsatz 3:Feuer löschen");
        AB3 ab3 = new AB3();
        addObject(ab3,  0, 3);
        ab3.setAnzahlVonGegenstand("Akku",10);
        Schalter s = new Schalter(this);
        int[][] on={{19}}; 
        int[][] off={{31}};
        s.setPosition(11, 10, on, off);
        s.anschalten();
        Gegenstand g = (Gegenstand) (this.getObjectsAt(10, 10, Gegenstand.class).get(0));
        g.setSchalter(s);
        s = new Schalter(this);
        int[][] on2={{18}}; 
        s.setPosition(10, 4, on2, off);
        s.anschalten();
        g = (Gegenstand) (this.getObjectsAt(10, 3, Gegenstand.class).get(0));
        g.setSchalter(s);
        g = (Gegenstand) (this.getObjectsAt(10, 5, Gegenstand.class).get(0));
        g.setSchalter(s);
        int y = 10;
        int maxx = Greenfoot.getRandomNumber(4)+3;

        while (y>4) {
            for (int x=1; x<=maxx; x++) {
                addObject(new Gegenstand("Feuer"), x,y);
            }
            int newx = 20;
            while(newx > maxx || newx < 1) newx=Greenfoot.getRandomNumber(4)-7+y;

            maxx = newx;
            y = y-1;
        }

        ab3.einsatz3();
        Greenfoot.delay(2);
        boolean feueraus = true;
        List l = this.getObjects(Gegenstand.class);
        for (Object o : l) {
            if (((Gegenstand) o).getName().equals("Feuer")) {
                feueraus = false;
            }
        }
        if (ab3.istAufGegenstand("Portal") && feueraus) {
            secret.writeLevel(4);
            melde("Hurra! Das Feuer ist gelöscht.\n Die Katastrophe wurde verhindert.");
        } else {
            warne("Du hast die Aufgabe noch nicht erfüllt");
        }
        Greenfoot.setWorld(new RoboterWelt());

    }

    private void ab4_verzweiger()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,36,10,10, 0,10, 0, 0, 0, 0, 0,10,10,10},
                {10, 0,10,10, 0,10, 0,10,10,10, 0, 0, 0,10},
                {10, 0,10,10, 0,10, 0, 0, 0,10,10,10, 0,10},
                {10, 0,10,10, 0,10,10,10, 0,10, 0, 0, 0,10},
                {10, 0,10,10, 0,10, 0, 0, 0,10, 0,10,10,10},
                {10, 0,10,10, 0,10, 0,10,10,10,10,10,10,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10, 0,37, 0, 0,37, 0,37, 0, 0, 0,37, 0,10},
                {10, 0, 0,34,34, 0,34, 0,34, 0, 0,34, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);
        zeichneUeberschrift("AB4 - Verzweigungen nutzen");

        // Schluesselloch und Tuere werden gekoppelt:
        Schalter s = new Schalter(this);
        int[][] on ={{-1,17},{18,-1}};
        int[][] off={{-1,17},{0,-1}};
        s.setPosition(1,5,on,off);
        s.anschalten();

        // Schalter und Stromquelle werden gekoppelt:
        s = new Schalter(this);
        int[][] on1 = {{-1,-1,12},{13,32,14}};
        int[][] off1 = {{-1,-1,11},{15,0,16}};
        s.setPosition(3,5,on1,off1);
        s.anschalten();

        AB4 ab41 = new AB4();
        addObject(ab41, 1, 2);
        ab41.dreheRechts();
        AB4 ab42 = new AB4();
        addObject(ab42, 4, 2);
        ab42.dreheRechts();
        AB4 ab43 = new AB4();
        addObject(ab43, 1, 9);
        AB4 ab44 = new AB4();
        addObject(ab44, 1,10);
        ab44.setAnzahlVonGegenstand("Schraube", 10);
        AB4 ab45 = new AB4();
        addObject(ab45, 6, 7);
        ab45.dreheLinks();

        // Lege Schraubenreihe zufaellig
        for (int i=0; i<12; i++) {
            // muss sein, weil aus irgendwelchen Gründen "alte" Schrauben liegengeblieben sind
            List<Gegenstand> g = getObjectsAt(i+1, 10, Gegenstand.class);
            removeObjects(g);																							  
            if (Greenfoot.getRandomNumber(3)==0)
                addObject(new Gegenstand("Schraube"), i+1, 10);
        }
    }

    public void einsatz_04()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                { 0, 0, 0, 0, 0, 0,10, 4, 4, 4, 4, 4, 4,10},
                {10,10,10,10,10, 0,10, 4, 4, 4, 4, 4, 4,10},
                {10,10,10,10,10, 0,10,10,10,10,10,10,18,10},
                {10,10,10,10,10,10,10,10,10,10,10,10, 0,10},
                {10,10,10,10,10,10, 0, 0, 0,10,10,10, 0,10},
                {10,10,10,10,10,10, 0,10, 0,10,10,10, 0,10},
                {10,10,10,10,10,10, 0,10, 0,10,10,10, 0,10},
                {10,10,10,10,10,10, 0,10, 0,10,10,10, 0,10},
                {10,10,10,10,10, 0, 0,10, 0, 0, 0, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);
        zeichneUeberschrift("Einsatz 4: Reaktor abschalten");
        AB4 ab4 = new AB4();
        addObject(ab4,  0, 2);

        int x = Greenfoot.getRandomNumber(3)+1;
        int y = Greenfoot.getRandomNumber(5)+6;
        Wand w;
        for (int xx=4; xx>= x; xx--) {
            w = (Wand) (this.getObjectsAt(xx, 4, Wand.class).get(0));
            this.removeObject(w);
        }
        for (int yy=5; yy<= y; yy++) {
            w = (Wand) (this.getObjectsAt(x, yy, Wand.class).get(0));
            this.removeObject(w);
        }

        for (int xx=x+1; xx<=4; xx++) {
            w = (Wand) (this.getObjectsAt(xx, y, Wand.class).get(0));
            this.removeObject(w);
        }
        for (int yy=y+1; yy<= 10; yy++) {
            w = (Wand) (this.getObjectsAt(4, yy, Wand.class).get(0));
            this.removeObject(w);
        }
        boolean mindEinSchalter = false;
        for(int xx=7;xx<=11;xx++) {
            if ((Greenfoot.getRandomNumber(2)==0) || ((xx==11) && !mindEinSchalter)) {
                Schalter s = new Schalter(this);
                int[][] on ={{11}};
                int[][] off={{12}};
                s.setPosition(xx,4,on,off);
                s.anschalten();
                mindEinSchalter = true;
            }
        }
        Schalter s = new Schalter(this);
        if (Greenfoot.getRandomNumber(2)==0) {
            int[][] on ={{-1,18},{11,-1}};
            int[][] off={{-1,0},{12,-1}};
            s.setPosition(5,7,on,off);
        } else {
            int[][] on ={{-1,18},{17,-1}};
            int[][] off={{-1,0},{17,-1}};
            s.setPosition(5,7,on,off);
            addObject(new Gegenstand("Schluessel"),0,2);
            ab4.aufnehmen();
        } 
        s.anschalten();
        s = new Schalter(this);
        if (Greenfoot.getRandomNumber(2)==0) {
            int[][] on ={{-1,11},{18,-1}};
            int[][] off={{-1,12},{0,-1}};
            s.setPosition(8,8,on,off);
        } else {
            int[][] on ={{-1,17},{18,-1}};
            int[][] off={{-1,17},{0,-1}};
            s.setPosition(8,8,on,off);
            addObject(new Gegenstand("Schluessel"),0,2);
            ab4.aufnehmen();
        } 
        s.anschalten();
        s = new Schalter(this);
        if (Greenfoot.getRandomNumber(2)==0) {
            int[][] on ={{-1,18},{11,-1}};
            int[][] off={{-1,0},{12,-1}};
            s.setPosition(11,4,on,off);
        } else {
            int[][] on ={{-1,18},{17,-1}};
            int[][] off={{-1,0},{17,-1}};
            s.setPosition(11,4,on,off);
            addObject(new Gegenstand("Schluessel"),0,2);
            ab4.aufnehmen();
        } 
        s.anschalten();

        ab4.einsatz4();
        Greenfoot.delay(2);
        boolean geschafft = true;

        for(int xx=7;xx<=11;xx++) {
            s = ((Wand) (this.getObjectsAt(xx, 4, Wand.class).get(0))).getSchalter();

            if (s != null && s.getAn()) {
                geschafft = false;
            }
        }

        if (geschafft) {
            secret.writeLevel(5);
            melde("In letzter Minute wurde das Kraftwerk abgeschaltet.\nDer Super-GAU wurde dadurch verhindert.");
        } else {
            warne("Du hast die Aufgabe noch nicht erfüllt");
        }
        Greenfoot.setWorld(new RoboterWelt());
    }

    private void ab5_funktionen()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10, 0,10,10,10, 0,10,10,10,10,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10,10,10,10,10,10, 0,10, 0,10,10,10,10,10},
                {10, 0,43,10, 0, 0, 0,10, 0,43, 0, 0,43,10},
                {10, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0,10},
                {10,43, 0, 0,43, 0,10,10, 0,42, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0,43,10, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 4,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);

        zeichneUeberschrift("AB5 - Methoden mit Rückgabewert");
        AB5 ab5;
        ab5 = new AB5();
        addObject(ab5,  1, 3);
        for(int i=0; i<10; i++) {
            addObject(new Gegenstand("Schraube"),1,3);
            ab5.aufnehmen();
        }

        ab5 = new AB5();
        addObject(ab5,  1, 5);
        for(int i=0; i<5; i++) {
            addObject(new Gegenstand("Schraube"),1,5);
            ab5.aufnehmen();
        }
        ab5.verbraucheEnergie(40);
        ab5 = new AB5();
        addObject(ab5,  2, 7);
        for(int i=0; i<2; i++) {
            addObject(new Gegenstand("Schraube"),2,7);
            ab5.aufnehmen();
        }
        ab5.verbraucheEnergie(80);

        ab5 = new AB5();
        addObject(ab5,  8, 5);
        ab5 = new AB5();
        addObject(ab5,  8, 7);
        ab5 = new AB5();
        addObject(ab5,  5, 9);
        ab5.verbraucheEnergie(85);

    }

    public void einsatz_05()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10, 0,10,10,10,10,10,10},
                {10,10,10,10,10,10,10, 0,10,10,10,10,10,10},
                {10,10,10,10,10,10,10, 0,10,10,10,10,10,10},
                {10,10,10,10,10,10,10, 0,10,10,10,10,10,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0,10, 0,10, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0,10, 0,10, 0, 0, 0, 0,10},
                {10, 1, 1, 0, 0, 0,10, 0,10, 0, 0, 0, 0,10},
                {10, 4, 1, 0, 0, 0,10, 0,10, 1, 1, 1, 1,10},
                {10, 4, 1, 0,10, 0,10, 0,10, 4, 4, 4, 4,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        // Hoehe variabel
        int z = Greenfoot.getRandomNumber(4);
        for (int y = 0; y <z; y++) {
            backg[4-y] = backg[5-y];
            backg[5-y] = backg[6-y];
        }
        // Breite variabel
        int w = Greenfoot.getRandomNumber(3);
        for (int x = 0; x < w; x++) {
            for (int i = 0; i < 12; i++) {
                backg[i][13-(x+1)] = backg[i][13];
            }
        }

        weltLeeren();
        generateWorld(backg);
        zeichneUeberschrift("Einsatz 5 - Ordnung schaffen");
        AB5 ab5 = new AB5();
        addObject(ab5,  7, 1);
        ab5.dreheRechts();
        for (int y = 6-z; y < 11; y++) {
            addObject(new Gegenstand("Akku"), 7, y);
        }

        // Faesser im rechten Raum auf einer Zeile verteilen
        if (w==2) addObject(new Gegenstand("Atommuell"), 10, 6-z);
        if (w==1) {
            if (Greenfoot.getRandomNumber(2)==0) addObject(new Gegenstand("Atommuell"), 10, 6-z);
            else addObject(new Gegenstand("Atommuell"), 11, 6-z);
        }
        if (w==0) {
            addObject(new Gegenstand("Atommuell"), 10, 6-z);
            addObject(new Gegenstand("Atommuell"), 12, 6-z);
        }

        int x = 2;
        int y = Greenfoot.getRandomNumber(4+z)+6-z;
        addObject(new Gegenstand("Atommuell"), x,y);
        x = 4;
        y = Greenfoot.getRandomNumber(4+z)+6-z;
        addObject(new Gegenstand("Atommuell"), x,y);

        ab5.einsatz5();
        Greenfoot.delay(2);
        // Liste der Atommuellfaesser wird abgefragt
        List<Gegenstand> liste = getObjects(Gegenstand.class);
        boolean geschafft = true;
        for (Gegenstand g : liste) {
            if(g.istGegenstand("Atommuell")){
                if(backg[g.getY()][g.getX()] != 4) geschafft = false;
            }
        }

        if (geschafft) {
            secret.writeLevel(6);
            melde("Es herrscht wieder Ordnung im Zwischenlager.\nSo ein Aufräumroboter wäre auch für zu Hause praktisch...");
        } else {
            warne("Du hast die Aufgabe noch nicht erfüllt");
        }

        Greenfoot.setWorld(new RoboterWelt());
    }

    private void ab6_attribute()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,33,10,10,10,10,10,10,10,10},
                {10, 0, 0, 0,10, 0,10, 0, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10, 0, 0,10,10,10, 0,10,10,10, 0,10,10,10},
                {10, 0, 0,10, 0, 0, 0,10, 0,10, 0,10, 0,10},
                {10, 0, 0,10, 0,10, 0,10, 0,10, 0,10, 0,10},
                {10, 0, 0,10, 0,10, 0, 0, 0,10, 0,10, 0,10},
                {10, 0, 0,10,10,10, 0,10,10,10, 0,10, 0,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10, 0,10},
                {10, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);

        zeichneUeberschrift("AB6 - Eigene Attribute");
        AB6_AB7 ab6;
        ab6 = new AB6_AB7();
        addObject(ab6,  4, 5);
        ab6 = new AB6_AB7();
        addObject(ab6,  8, 5);
        ab6.setRotation(90);
        ab6 = new AB6_AB7();
        addObject(ab6,  3, 9);
        ab6.setRotation(270);
    }

    public void einsatz_06()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,33,10,10,10,10,10,10,10,10},
                {10, 0, 0, 0,10, 0,10, 0, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0,10},
                {10, 0, 0,10,10,10,10,10, 0, 0, 0,10, 0,10},
                {10, 0, 0,10, 0, 0, 0,10,10,10, 0,10, 0,10},
                {10, 0, 0,10, 0,10,10,10, 0,10, 0,10, 0,10},
                {10,10, 0,10, 0, 0, 0, 0, 0,10, 0,10, 0,10},
                {10, 0, 0,10,10,10, 0,10,10,10, 0,10, 0,10},
                {10, 0,10, 0, 0, 0, 0, 0, 0, 0, 0,10, 0,10},
                {10, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);

        zeichneUeberschrift("Einsatz 6: Notfallprogramm");
        AB6_AB7 ab6;
        ab6 = new AB6_AB7();
        addObject(ab6,  6, 5);
        ab6.setRotation(90);
        ab6.einsatz6();
        Greenfoot.delay(2);
        if (ab6.istAufGegenstand("Portal")) {
            if(ab6.getSchritte()==65) {
                secret.writeLevel(7);
                melde("Notfallprogramm aktiviert!\nSuche Ausgang...\nSuche Ausgang...\nAusgang nach 65 Schritten erreicht!");
            } else {
                warne("Du hast die Aufgabe noch nicht korrekt erfüllt.\nDer Pledge-Algorithmus sollte für dieses\nLabyrinth 65 Schritte benötigen.");
            }
        } else {
            warne("Du hast die Aufgabe noch nicht erfüllt");
        }
        Greenfoot.setWorld(new RoboterWelt());
    }

    private void ab7_vererbung()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,33,10,10,10,10,10,10,10},
                {10,10,10, 0,10,10, 0,10, 0,10, 0,10, 0,10},
                {10, 0,43, 0,35, 0, 0,35, 0, 0, 0, 0,35,10},
                {10,10,10, 0,10,10, 0,10,10,10, 0,10, 0,10},
                {10,10,10, 0,10,43,43,43, 0,10, 0,10, 0,10},
                {10,10,10, 0,10,43,43,43,35,10,43,10, 0,10},
                {10, 0, 0, 0,10, 0, 0, 0,35,10,43,10, 0,10},
                {10, 0,43, 0,10,10, 0,10,10,10, 0,10, 0,10},
                {10, 0,43, 0, 0,43, 0, 0, 0, 0, 0,10, 0,10},
                {10, 0, 0, 0,43,43, 0,43, 0, 0, 0, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);

        zeichneUeberschrift("AB7 - Vererbung");
        AB6_AB7 ab6;
        ab6 = new AB6_AB7();
        addObject(ab6,  8, 5);
        ab6.setRotation(90);
        ab6 = new AB6_AB7();
        addObject(ab6,  4, 9);
        ab6.setRotation(90);

        ab6 = new AB6_AB7();
        addObject(ab6,  1, 3);
        ab6.setRotation(0);

    }

    public void einsatz_07()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10, 0,10,10,10,10,10,10,10,10,10,10, 0,10},
                {10, 0,10,10,10,10,10,10,10,10,10,10, 0,10},
                {10, 0,10,10,10,10,10,10,10,10,10,10, 0,10},
                {33, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,31,38,10},
                {10, 0,10,10,10,10,10,10,10,10,10,10, 0,10},
                {10, 0,10,10,10,10,10,10,10,10,10,10, 0,10},
                {10, 0,10,10,10,10,10,10,10,10,10,10, 0,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        int[][] on = {
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,},
                {21,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,},
                {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,}};

        weltLeeren();
        int x = 3;
        int pos = 1;
        int brennstaebe=0;
        while (x< 11) {
            for (int y = 3; y <=9 ; y++) {
                backg[y][x] = 0;
            }
            if (Greenfoot.getRandomNumber(2)==0) {
                backg[3][x] = 42;
                if (Greenfoot.getRandomNumber(2)==0) { backg[4][x] = 35; brennstaebe++;}
                if (Greenfoot.getRandomNumber(2)==0) { backg[5][x] = 35; brennstaebe++;}
            } else {
                if (pos ==1) {
                    pos = 2;
                    on[0][x-1] = 21;
                    on[8][x-1] = 21;
                } else {
                    if (pos == 2) {
                        pos = 1;
                        on[4][x-1] = 21;
                        on[8][x-1] = 21;
                    }
                }
            }

            if (Greenfoot.getRandomNumber(2)==0) {
                backg[9][x] = 42;
                if (Greenfoot.getRandomNumber(2)==0) {backg[7][x] = 35;brennstaebe++;}
                if (Greenfoot.getRandomNumber(2)==0) {backg[8][x] = 35;brennstaebe++;}
            }else {
                if (pos ==3) {
                    pos = 2;
                    on[8][x-1] = 21;
                    on[0][x-1] = 21;
                    on[4][x-1] = 0;
                } else {
                    if (pos == 2) {
                        on[4][x-1] = 21;
                        on[0][x-1] = 21;
                        on[8][x-1] = 0;
                        pos = 3;
                    }
                }
            }

            x += Greenfoot.getRandomNumber(2)+2;
        }

        generateWorld(backg);

        zeichneUeberschrift("Einsatz 7: Brennstäbe inventarisieren");
        AB6_AB7 ab7;
        ab7 = new AB6_AB7();
        addObject(ab7,  1, 6);
        Schalter s = new Schalter(this);
        s.setPosition(2,2,on,on);
        Gegenstand g = (Gegenstand) (this.getObjectsAt(11, 6, Gegenstand.class).get(0));
        g.setSchalter(s);

        ab7.einsatz7();
        Greenfoot.delay(2);
        if (ab7.istAufGegenstand("Portal")) {
            if(ab7.getBrennstaebe()==brennstaebe) {
                secret.writeLevel(8);
                melde("Jetzt sind auch noch die Stollen eingestürtzt.\nZum Glück hat das Notfallprogramm funktioniert.\nGut gemacht, Robi!");
            } else {
                warne("Du hast die Aufgabe noch nicht korrekt erfüllt.\nEs hätten "+brennstaebe+" Brennstaebe sein müssen.");
            }
        } else {
            warne("Du hast die Aufgabe noch nicht erfüllt");
        }
        Greenfoot.setWorld(new RoboterWelt());
    }

    private void ab8_lokale_variablen()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10, 0, 0,34,34, 0,38,41,36, 0,36,41,34,10},
                {10, 0, 0, 0,38, 0, 0,38,41, 0,41, 0, 0,10},
                {10,10,10,10,10,18,10,10,10,10,18,10,10,10},
                {10, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0,10},
                {10, 0,43,43,43,43,43, 0,10, 0, 0, 0, 0,10},
                {10, 0,43,43,43,43,43, 0,10,10,18,10,10,10},
                {10, 0,43,43,43,43,43, 0,10, 0, 0, 0, 0,10},
                {10, 0,43,43,43,43,43, 0,10, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0, 0, 0,10, 0, 0, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        generateWorld(backg);

        zeichneUeberschrift("AB8 - Lokale Variablen");
        AB8 ab8;
        ab8 = new AB8();
        addObject(ab8,  1, 2);
        ab8 = new AB8();
        addObject(ab8,  1, 3);
        ab8 = new AB8();
        addObject(ab8,  1, 5);
        ab8 = new AB8();
        addObject(ab8,  9, 5);
        ab8 = new AB8();
        addObject(ab8,  9, 8);

    }

    public void einsatz_08()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,33,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();
        int x = 1;
        int pos = 1;
        int qm=0;
        while(x<11) {
            int breite = Greenfoot.getRandomNumber(4)+2;
            if ((breite+x)>13) {
                breite = 13-x;
            }
            int hoehe = Greenfoot.getRandomNumber(5)+3;
            for(int xx = 0; xx<breite; xx++) {
                backg[4][xx+x] = 0;
                backg[5][xx+x] = 1;
                for(int yy=2; yy<hoehe; yy++) {
                    backg[yy+4][xx+x] = 4;
                }
            }
            qm += breite*(hoehe-2);
            backg[3][x]=0;
            x = x + breite+1;
        }

        generateWorld(backg);

        zeichneUeberschrift("Einsatz 8: Platz berechnen");
        AB8 ab8;
        ab8 = new AB8();
        addObject(ab8,  0, 2);
        int anz = ab8.einsatz8();
        Greenfoot.delay(2);
        if (ab8.istAufGegenstand("Portal") && anz == qm) {
            secret.writeLevel(9);
            melde("Nur noch "+qm+" Plätze?\nNa viel Platz ist das ja nicht mehr.\nAber dafür kannst du ja nichts.\nAuftrag erfüllt!");
        } else {
            warne("Du hast die Aufgabe noch nicht erfüllt.\nDu hast "+anz+" Plätze ermittelt. Es waren aber "+qm+" Plätze.");
        }
        Greenfoot.setWorld(new RoboterWelt());
    }

    private void ab9_parameter()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {20,20,20,20,20,20,20,20,20,20,20,20,20,20},
                {20,20,20,20,23, 0,30, 0, 0, 0, 0,20, 0,20},
                {20,20,20,20,20,20, 7,20,20,20,20,20,20,20},
                {20,20,20,20,20,20, 7,20,20,20,20,20,20,20},
                {20,44,44,44, 0, 0, 7, 0, 0, 0, 0,20,20,20},
                {20,20,20,20,20,20, 7,20,20,20,20,20,20,20},
                {20,20,20,20,20,20, 7,20,20,20,20,20,20,20},
                {20,20,20,20,20,20, 7,20, 0, 0, 0, 0,20,20},
                {20,20,20,20, 0, 0, 7, 0, 0, 0, 0, 0,20,20},
                {20,20,20,20,20,20, 7,20,21, 0, 0, 0,20,20},
                {20,20,20,20,20,20,20,20,20,20,20,20,20,20}};

        weltLeeren();

        generateWorld(backg);
        schwerkraft = true;
        Aufzugknopf s = new Aufzugknopf(this);
        int[][] on = {{22}}; 
        int[][] off = {{23}};
        s.setPosition(4,2,on,off);
        s.setAufzug((Gegenstand) this.getObjectsAt(6,2,Gegenstand.class).get(0),2);
        Wand w = (Wand) (this.getObjectsAt(4, 2, Wand.class).get(0));
        w.setSchalter(s);

        zeichneUeberschrift("AB9 - Methoden mit Parametern");
        // zeigeKoordinaten();
        AB9 legeRoboter, sprengRoboter1, sprengRoboter2, aufzugRoboter;

        aufzugRoboter = new AB9();
        addObject(aufzugRoboter,  5, 2);

        sprengRoboter1 = new AB9();
        addObject(sprengRoboter1,  6, 2);

        sprengRoboter2 = new AB9();
        addObject(sprengRoboter2,  7, 2);

        legeRoboter = new AB9();
        addObject(legeRoboter,  8, 9);

        legeRoboter.setAnzahlVonGegenstand("Brennstab", Greenfoot.getRandomNumber(5)+9);

        EinsatzLeiter el = new EinsatzLeiter();
        addObject(el,  12, 2);
        el.nehmeKontaktAuf();
        schwerkraftAnwenden();
    }

    public void einsatz_09()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {20,20,20,20,20,20,20,20,20,20,20,20,20,20},
                {20,20,20,20,23, 0, 7, 0, 0, 0, 0,20, 0,20},
                {20,20,20,20,20,20, 7,20,20,20,20,20,20,20},
                {20,23,44,44,44,44, 7,20,20,20,21,20,21,20},
                {20,20,21,20,21,20,30, 0, 0, 0,21,21,21,20},
                {20,21,20,20,20,20, 7,21,20,21,21,20,21,20},
                {20,20,21,20,20,20, 7,20,20,20,20,20,20,20},
                {20,20,21, 0, 0, 0, 7,20,20, 0,20,20,21,20},
                {20,21,21,20,21,21, 7, 0, 0, 0, 0, 0,21,20},
                {20,21,20,21,20,20, 7,21,20,21,21,21,20,20},
                {20,20,20,20,20,20,20,20,20,20,20,20,20,20}};

        weltLeeren();
        Greenfoot.setSpeed(40);

        generateWorld(backg);
        schwerkraft = true;
        Aufzugknopf s = new Aufzugknopf(this);
        int[][] on = {{22}}; 
        int[][] off = {{23}};
        s.setPosition(4,2,on,off);
        s.setAufzug((Gegenstand) this.getObjectsAt(6,5,Gegenstand.class).get(0),2);
        Wand w = (Wand) (this.getObjectsAt(4, 2, Wand.class).get(0));
        w.setSchalter(s);

        s = new Aufzugknopf(this);
        s.setPosition(1,4,on,off);
        s.setAufzug((Gegenstand) this.getObjectsAt(6,5,Gegenstand.class).get(0),4);
        w = (Wand) (this.getObjectsAt(1, 4, Wand.class).get(0));
        w.setSchalter(s);        
        zeichneUeberschrift("Einsatz 9: Das Endlager wird bestückt");

        AB9 aufzugRoboter, sprengRoboter1, sprengRoboter2, legeRoboter;
        aufzugRoboter = new AB9();
        addObject(aufzugRoboter,  5, 2);

        sprengRoboter1 = new AB9();
        addObject(sprengRoboter1,  7, 2);
        sprengRoboter1.dreheUm();

        sprengRoboter2 = new AB9();
        addObject(sprengRoboter2,  8, 2);
        sprengRoboter2.dreheUm();

        legeRoboter = new AB9();
        addObject(legeRoboter,  9, 2);

        legeRoboter.setAnzahlVonGegenstand("Brennstab", 22);
        legeRoboter.setAnzahlVonGegenstand("Akku",5);
        legeRoboter.dreheUm();

        EinsatzLeiter el = new EinsatzLeiter();
        addObject(el,  12, 2);
        schwerkraftAnwenden();

        el.nehmeKontaktAuf();
        Greenfoot.delay(40);
        el.einsatz9();
        Greenfoot.delay(2);
        boolean ok = true;
        if (legeRoboter.getY() != 2 || aufzugRoboter.getY()!=2 || sprengRoboter1.getY()!=2 || sprengRoboter2.getY()!=2) {
            ok = false;
            warne("Du hast die Aufgabe noch nicht erfüllt!\nDie Roboter stehen nicht im oberen Stollen.");
        } else {
            for(int x=8; x <11; x++) {
                for(int y=5; y<7; y++) {
                    List<Gegenstand> g =  getObjectsAt(x,y,Gegenstand.class);
                    if(g.size() != 1 || !g.get(0).getName().equals("Brennstab")) ok = false;
                }
            }
            for(int x=8; x <13; x++) {
                for(int y=9; y<11; y++) {
                    List<Gegenstand> g =  getObjectsAt(x,y,Gegenstand.class);
                    if(g.size() != 1 || !g.get(0).getName().equals("Brennstab")) ok = false;
                }
            }
            for(int x=2; x <5; x++) {
                for(int y=8; y<10; y++) {
                    List<Gegenstand> g =  getObjectsAt(x,y,Gegenstand.class);
                    if(g.size() != 1 || !g.get(0).getName().equals("Brennstab")) ok = false;
                }
            }

            if (!ok) {
                warne("Du hast die Aufgabe noch nicht erfüllt.\nDie Brennstäbe sind nicht\nin den alten Stollen positioniert.");
            }
        }

        if (ok) {
            secret.writeLevel(10);
            melde("Prima gemacht. Auftrag erfüllt!");
        } 
        Greenfoot.setWorld(new RoboterWelt());
    }

    private void ab10_zaehlschleifen()
    {
        int[][] backg = {{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                {20,20,20,20,20,20,20,20,20,20,20,20,20,20},
                {20,23, 5, 5, 5, 5, 7,20,43,43,43,20, 5,20},
                {20,20,20,20,20,20, 7,20,43,43,43,20, 5,20},
                {20, 5, 5, 5, 5,20, 7,20,20,43,20, 5, 5,20},
                {20, 5, 5, 5, 5,20,30,20,20,43,20, 5, 5,20},
                {20, 5, 5, 5, 5,20, 7,20,20,43,20,20,20,20},
                {20, 5,34,34, 5,20, 7, 5, 5,43, 5, 5, 5,20},
                {20,34,34,34,34,20,20, 5,20,20,20, 5,20,20},
                {20,34,34,34,34,20, 5, 5, 5, 5, 5, 5, 5,20},
                {20,34,34,34,34,20, 5, 5, 5, 5, 5, 5, 5,20},
                {20,20,20,20,20,20,20,20,20,20,20,20,20,20}};

        weltLeeren();
        generateWorld(backg);
        schwerkraft = true;

        Aufzugknopf s = new Aufzugknopf(this);
        int[][] on = {{22}}; 
        int[][] off = {{23}};
        s.setPosition(1,2,on,off);
        s.setAufzug((Gegenstand) this.getObjectsAt(6,5,Gegenstand.class).get(0),2);
        Wand w = (Wand) (this.getObjectsAt(1, 2, Wand.class).get(0));
        w.setSchalter(s);

        zeichneUeberschrift("AB10 - Zählschleifen");
        Greenfoot.setSpeed(40);

        AB10 r1 = new AB10();
        addObject(r1,1,10);
        r1.setAnzahlVonGegenstand("Akku", 3);

        AB10 r2 = new AB10();
        addObject(r2,8,7);

        AB10 r3 = new AB10();
        addObject(r3,2,2);

        schwerkraftAnwenden();
    }

    public void einsatz_10()
    {
        int[][] backg = {{ 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6},
                {20,20,20,20,20,20,20,20,20,20,20,20,20,20},
                {20,23, 6, 6, 6, 6, 7,20, 6, 6, 6, 6, 6,20},
                {20,20,20,20,20,20, 7,20, 6, 6, 6, 6, 6,20},
                {20, 6, 6, 6, 6,20, 7,20, 6, 6, 6, 6, 6,20},
                {20, 6, 6, 6, 6,20, 7,20,20,20,20,20,20,20},
                {20, 6, 6, 6, 6,20, 7, 6, 6, 6, 6, 6,20,20},
                {20, 6, 6, 6, 6,20,30,20,20,20,20, 6, 6,20},
                {20, 6, 6, 6, 6,20, 7,20, 6, 6, 6, 6, 6,20},
                {20,20,20,20,20,20, 7,20,20,20,20, 6,20,20},
                {20, 6, 6, 6, 6, 6, 7, 6, 6, 6, 6, 6, 6,20},
                {20,20,20,20,20,20,20,20,20,20,20,20,20,20}};

        weltLeeren();
        generateWorld(backg);
        schwerkraft = true;

        Aufzugknopf s = new Aufzugknopf(this);
        int[][] on = {{22}}; 
        int[][] off = {{23}};
        s.setPosition(1,2,on,off);
        s.setAufzug((Gegenstand) this.getObjectsAt(6,7,Gegenstand.class).get(0),2);
        Wand w = (Wand) (this.getObjectsAt(1, 2, Wand.class).get(0));
        w.setSchalter(s);

        zeichneUeberschrift("Einsatz 10: Sammle die Diamanten");
        Greenfoot.setSpeed(40);

        AB10 aufzugRoboter, sprengRoboter1, sprengRoboter2, legeRoboter;
        aufzugRoboter = new AB10();
        addObject(aufzugRoboter,  2, 2);

        sprengRoboter1 = new AB10();
        addObject(sprengRoboter1,  3, 2);
        sprengRoboter1.setAnzahlVonGegenstand("Bombe", 2);

        sprengRoboter2 = new AB10();
        addObject(sprengRoboter2,  4, 2);
        sprengRoboter2.setAnzahlVonGegenstand("Bombe", 2);

        EinsatzLeiter el = new EinsatzLeiter();
        addObject(el,  13, 0);
        el.nehmeKontaktAuf();

        Random r = new Random();
        for(int x=1;x<5; x++) {
            for(int y=4;y<9;y++) {
                int z = r.nextInt(10);
                Actor g=null;
                if(z<7) {
                    g = new Gegenstand("Diamant");
                } else if(z<8) {
                    g = new Wand("Steine");
                }
                if(g!=null) this.addObject(g,x,y);
            }
        }
        for(int x=8;x<13; x++) {
            for(int y=2;y<5;y++) {
                int z = r.nextInt(10);
                Actor g=null;
                if(z<7) {
                    g = new Gegenstand("Diamant");
                } else if(z<8) {
                    g = new Wand("Steine");
                }
                if(g!=null) this.addObject(g,x,y);
            }
        }
        for(int x=8;x<13; x++) {
            for(int y=8;y<9;y++) {
                int z = r.nextInt(10);
                Actor g=null;
                if(z<7) {
                    g = new Gegenstand("Diamant");
                } else if(z<8) {
                    g = new Wand("Steine");
                }
                if(g!=null) this.addObject(g,x,y);
            }
        }
        this.schwerkraftAnwenden();

        el.einsatz10();
        Greenfoot.delay(2);

        boolean ok = false;
        int anz = (aufzugRoboter.getAnzahl("Diamant")+sprengRoboter1.getAnzahl("Diamant")+sprengRoboter2.getAnzahl("Diamant"));
        if(anz >= 10){
            secret.writeLevel(11);
            if (anz<15) {
                melde("Mindestanforderung erfüllt.\n"+anz+" Diamanten gesammelt.");
            } else {
                if (anz <20) {
                    melde("Gut gemacht. "+anz+" Diamanten gesammelt.\nDie Roboter werden für die Minenarbeit trainiert!");
                } else {
                    melde("Grandios. "+anz+" Diamanten gesammelt.\nDiese Roboter kauft die Firma sofort!");
                }
            }
        } else {
            warne("Du hast die Aufgabe noch nicht erfüllt!");
        }
        Greenfoot.setWorld(new RoboterWelt());
    }

    private void ab11_finalBild()
    {
        int[][] backg = {{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10,45,45,45, 0,45, 0, 0,45, 0, 0,45, 0,10},
                {10, 0,45, 0,45, 0,45, 0,45, 0, 0,45, 0,10},
                {10, 0,45, 0,45, 0,45, 0,45, 0, 0,45, 0,10},
                {10, 0,45, 0,45, 0,45, 0,45, 0, 0,45, 0,10},
                {10, 0,45, 0, 0,45, 0, 0,45,45, 0,45,45,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,10},
                {10,10,10,10,10,10,10,10,10,10,10,10,10,10}};

        weltLeeren();

        generateWorld(backg);
        zeichneUeberschrift("Bravo, jetzt kannst du programmieren!");

    }

    private void melde(String text) {

        Message m = new Message(text);
        addObject(m, 7,6);
        long z = System.currentTimeMillis();
        while(System.currentTimeMillis()-z < 2000){
            Greenfoot.delay(1);
        }
        removeObject(m);

    }

    private void warne(String text) {

        melde(text);

    }
}