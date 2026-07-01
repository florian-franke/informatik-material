const TASKS = {
  '1a': {
    title:
      'Aufgabe 1a: Programmzeilen erklaeren',
    maxPoints:
      6,
    instruction:
      'Bewerte, ob die Schuelerantwort die Bedeutung der beiden Programmzeilen im Hauptprogramm erklaert.',
    program:
      [
        'Kiste block = new Kiste();',
        'Ball ball1 = new Ball(100);'
      ].join('\n'),
    expectedAspects: [
      'Kiste block = new Kiste(); erzeugt ein neues Objekt der Klasse Kiste.',
      'Das neu erzeugte Kiste-Objekt wird in der Objektvariablen block gespeichert.',
      'Ball ball1 = new Ball(100); erzeugt ein neues Objekt der Klasse Ball.',
      'Das neu erzeugte Ball-Objekt wird in der Objektvariablen ball1 gespeichert.',
      'Die 100 wird beim Erzeugen an den Konstruktor von Ball uebergeben.',
      'Die Antwort erklaert die Programmzeilen in eigenen Worten und unterscheidet Klasse, Objekt und Objektvariable nachvollziehbar.'
    ]
  },

  '1b': {
    title:
      'Aufgabe 1b: Parameterwert Ball(100)',
    maxPoints:
      6,
    instruction:
      'Bewerte, ob die Schuelerantwort die Bedeutung der 100 bei Ball(100) und den passenden Fachbegriff erklaert.',
    program:
      [
        'Ball ball1 = new Ball(100);',
        '',
        'class Ball extends Actor {',
        '   Ball(float startX)',
        '   {',
        '      ball = new Circle(startX, 50, 30);',
        '      setzeBallfarbe(new Color(239, 250, 180));',
        '      geschwindigkeit = 5;',
        '   }',
        '}'
      ].join('\n'),
    expectedAspects: [
      'Die 100 wird beim Konstruktoraufruf new Ball(100) an den Konstruktor Ball(float startX) uebergeben.',
      'Die 100 wird im Konstruktor als Wert fuer startX verwendet.',
      'startX legt die x-Position beziehungsweise den horizontalen Startpunkt des Kreises fest.',
      'Der passende Fachbegriff fuer startX ist Parameter.',
      'Der konkrete Wert 100 kann als Argument oder Parameterwert bezeichnet werden.',
      'Die Antwort stellt den Zusammenhang zwischen Aufruf, Konstruktor und Circle(startX, 50, 30) nachvollziehbar dar.'
    ]
  },

  '1c': {
    title:
      'Aufgabe 1c: Klasse und Objekt unterscheiden',
    maxPoints:
      6,
    instruction:
      'Bewerte, ob die Schuelerantwort den Unterschied zwischen Klasse und Objekt anhand des Programms erklaert.',
    program:
      [
        'Kiste block = new Kiste();',
        'Ball ball1 = new Ball(100);',
        '',
        'class Ball extends Actor { ... }',
        'class Kiste extends Actor { ... }'
      ].join('\n'),
    expectedAspects: [
      'Eine Klasse ist ein Bauplan oder eine Vorlage fuer Objekte.',
      'Ball und Kiste sind Klassen, weil sie Attribute, Konstruktoren und Methoden beschreiben.',
      'Ein Objekt ist eine konkrete Instanz, die nach einer Klasse erzeugt wurde.',
      'ball1 ist ein Objekt der Klasse Ball beziehungsweise eine Objektvariable, die darauf verweist.',
      'block ist ein Objekt der Klasse Kiste beziehungsweise eine Objektvariable, die darauf verweist.',
      'Die Antwort nutzt Beispiele aus dem Programm und trennt Bauplan, erzeugtes Objekt und Objektvariable nachvollziehbar.'
    ]
  },

  '1f': {
    title:
      'Aufgabe 1f: extends Actor erklaeren',
    maxPoints:
      6,
    instruction:
      'Bewerte, ob die Schuelerantwort die Bedeutung von extends Actor im Programmtext zu Ball erklaert.',
    program:
      [
        'class Ball extends Actor {',
        '   int geschwindigkeit;',
        '',
        '   void act()',
        '   {',
        '      bewegeBall();',
        '      if(ball.isOutsideView())',
        '      {',
        '         setzteBallnachoben();',
        '      }',
        '   }',
        '}'
      ].join('\n'),
    expectedAspects: [
      'extends Actor bedeutet, dass Ball von der Klasse Actor erbt.',
      'Ball ist dadurch eine Unterklasse von Actor.',
      'Ball uebernimmt Eigenschaften oder Verhalten, die Actor fuer handelnde Objekte in der LearnJ-Umgebung bereitstellt.',
      'Die Methode act() kann dadurch als wiederholt ausgefuehrte Aktion des Actors genutzt werden.',
      'Durch Vererbung muss gemeinsames Actor-Verhalten nicht neu programmiert werden.',
      'Die Antwort verwendet den Fachbegriff Vererbung und bezieht ihn erkennbar auf Ball und Actor.'
    ]
  }
};
