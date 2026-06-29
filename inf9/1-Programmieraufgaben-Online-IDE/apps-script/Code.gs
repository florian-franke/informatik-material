/**
 * Apps-Script-Auswertungsserver fuer
 * inf9/1-Programmieraufgaben-Online-IDE/aufgabe1.html
 * und aufgabe2.html.
 *
 * Einrichtung:
 * 1. Diese Datei in ein Google-Apps-Script-Projekt kopieren.
 * 2. In den Projekteinstellungen eine Script Property anlegen:
 *    GEMINI_API_KEY = dein Gemini API-Key
 * 3. Optional:
 *    GEMINI_MODEL = gemini-3.5-flash
 * 4. Als Web-App bereitstellen:
 *    - Ausfuehren als: Ich
 *    - Zugriff: Jeder
 */

const PROPERTY_API_KEY =
  'GEMINI_API_KEY';

const PROPERTY_MODEL =
  'GEMINI_MODEL';

const DEFAULT_MODEL =
  'gemini-3.5-flash';

const MAX_ANSWER_LENGTH =
  3000;

const RESULT_MESSAGE_TYPE =
  'GEMINI_EVALUATION_RESULT';

const TASKS = {
  b: {
    title:
      'Aufgabe b: Programmtext analysieren',
    maxPoints:
      6,
    instruction:
      'Bewerte, ob die Schuelerantwort die Funktion jeder Programmzeile beschreibt.',
    program:
      [
        'Circle ball = new Circle(200, 50, 50);',
        'ball.move(10, 10);',
        'ball.setFillColor(Color.red);',
        'ball.destroy();'
      ].join('\n'),
    expectedAspects: [
      'Circle ball = new Circle(200, 50, 50); erzeugt ein neues Circle-Objekt und speichert es in der Variablen ball.',
      'Die Zahlen im Konstruktor legen Position und Groesse des Kreises fest.',
      'ball.move(10, 10); verschiebt den Kreis um die angegebenen Werte.',
      'ball.setFillColor(Color.red); faerbt den Kreis rot.',
      'ball.destroy(); entfernt oder zerstoert den Kreis wieder.',
      'Die Antwort verwendet eigene Worte und erklaert die Reihenfolge des Programms nachvollziehbar.'
    ]
  },

  c: {
    title:
      'Aufgabe c: Zahlen variieren',
    maxPoints:
      6,
    instruction:
      'Bewerte, ob die Schuelerantwort Beobachtungen zur Wirkung der Zahlen im Programm beschreibt.',
    program:
      [
        'Circle ball = new Circle(200, 50, 50);',
        'ball.move(10, 10);',
        'ball.setFillColor(Color.red);',
        'ball.destroy();'
      ].join('\n'),
    expectedAspects: [
      'Die erste Zahl in new Circle(200, 50, 50) beeinflusst die horizontale Position des Kreises.',
      'Die zweite Zahl in new Circle(200, 50, 50) beeinflusst die vertikale Position des Kreises.',
      'Die dritte Zahl in new Circle(200, 50, 50) beeinflusst die Groesse des Kreises.',
      'Die erste Zahl in move(10, 10) beschreibt die horizontale Verschiebung.',
      'Die zweite Zahl in move(10, 10) beschreibt die vertikale Verschiebung.',
      'Die Antwort beruht erkennbar auf einzelnen Veraenderungen und Beobachtungen.'
    ]
  },

  '2b': {
    title:
      'Aufgabe 2b: Klassen analysieren und veraendern',
    maxPoints:
      6,
    instruction:
      'Bewerte, ob die Schuelerantwort die Funktion jeder Programmzeile in Programm.java beschreibt.',
    program:
      [
        'Programm.java:',
        'Hund petersHund = new Hund(5, "Wuffti");',
        '',
        'Hund inasHund = new Hund(8, "Schnuffi");',
        '',
        'petersHund.zeigeDaten();',
        '',
        'inasHund.zeigeDaten();',
        '',
        'inasHund.belle();',
        '',
        'Hund.java:',
        'class Hund {',
        '   int alter;',
        '   String name;',
        '',
        '   Hund(int par1, String par2)',
        '   {',
        '      alter = par1;',
        '      name = par2;',
        '   }',
        '',
        '   void zeigeDaten()',
        '   {',
        '      println("Der Hund heisst " + name + " und ist " + alter + " Jahre alt.");',
        '   }',
        '',
        '   void belle() {',
        '      println(name + ": Wuff wuff"); }',
        '}'
      ].join('\n'),
    expectedAspects: [
      'Hund petersHund = new Hund(5, "Wuffti"); erzeugt ein Hund-Objekt mit Alter 5 und Name Wuffti und speichert es in petersHund.',
      'Hund inasHund = new Hund(8, "Schnuffi"); erzeugt ein zweites Hund-Objekt mit Alter 8 und Name Schnuffi und speichert es in inasHund.',
      'petersHund.zeigeDaten(); ruft die Methode zeigeDaten fuer petersHund auf und gibt seine Daten aus.',
      'inasHund.zeigeDaten(); ruft die Methode zeigeDaten fuer inasHund auf und gibt ihre Daten aus.',
      'inasHund.belle(); ruft die Methode belle fuer inasHund auf und gibt den Belltext mit dem Namen aus.',
      'Die Antwort verwendet eigene Worte und erklaert Objekte, Konstruktoraufrufe und Methodenaufrufe nachvollziehbar.'
    ]
  }
};

const EVALUATION_SCHEMA = {
  type:
    'object',
  properties: {
    points: {
      type:
        'integer'
    },
    maxPoints: {
      type:
        'integer'
    },
    status: {
      type:
        'string'
    },
    strengths: {
      type:
        'array',
      items: {
        type:
          'string'
      }
    },
    missing: {
      type:
        'array',
      items: {
        type:
          'string'
      }
    },
    feedback: {
      type:
        'string'
    }
  },
  required: [
    'points',
    'maxPoints',
    'status',
    'strengths',
    'missing',
    'feedback'
  ]
};


/**
 * Kleiner Gesundheitscheck, damit ein direkter Aufruf der Web-App
 * nicht mit einer fehlenden Index-Datei endet.
 */
function doGet(event) {
  const callback =
    event &&
    event.parameter
      ? String(event.parameter.callback || '')
      : '';

  if (callback) {
    return handleJsonpRequest_(
      event,
      callback
    );
  }

  return HtmlService
    .createHtmlOutput(
      '<!doctype html>' +
      '<meta charset="utf-8">' +
      '<title>Auswertungsserver</title>' +
      '<h1>Auswertungsserver ist erreichbar</h1>' +
      '<p>POST-Anfragen der Unterrichtsseite koennen verarbeitet werden.</p>'
    );
}


function handleJsonpRequest_(
  event,
  callback
) {
  try {
    validateCallbackName_(
      callback
    );
  } catch (error) {
    return ContentService
      .createTextOutput(
        '/* Ungueltiger JSONP-Callback. */'
      )
      .setMimeType(
        ContentService.MimeType.JAVASCRIPT
      );
  }

  const request =
    readRequest_(event);

  let result;

  try {
    validateRequest_(
      request
    );

    const task =
      TASKS[request.taskId];

    result =
      evaluateWithGemini_(
        task,
        request.answer
      );

  } catch (error) {
    result =
      createErrorResult_(
        error && error.message
          ? error.message
          : 'Die Antwort konnte nicht ausgewertet werden.'
      );
  }

  const payload = {
    type:
      RESULT_MESSAGE_TYPE,
    requestId:
      request.requestId || '',
    result:
      result
  };

  return ContentService
    .createTextOutput(
      callback +
      '(' +
      toScriptJson_(payload) +
      ');'
    )
    .setMimeType(
      ContentService.MimeType.JAVASCRIPT
    );
}


function validateCallbackName_(callback) {
  if (!/^[A-Za-z_$][0-9A-Za-z_$]*(\.[A-Za-z_$][0-9A-Za-z_$]*)*$/.test(callback)) {
    throw new Error(
      'Ungueltiger JSONP-Callback.'
    );
  }
}


/**
 * Nimmt die Formularuebertragung der Unterrichtsseite entgegen,
 * laesst Gemini eine Rueckmeldung erzeugen und sendet das Ergebnis
 * per postMessage an die urspruengliche Seite zurueck.
 */
function doPost(event) {
  const request =
    readRequest_(event);

  let result;

  try {
    validateRequest_(request);

    const task =
      TASKS[request.taskId];

    result =
      evaluateWithGemini_(
        task,
        request.answer
      );

  } catch (error) {
    result =
      createErrorResult_(
        error && error.message
          ? error.message
          : 'Die Antwort konnte nicht ausgewertet werden.'
      );
  }

  return createPostMessageResponse_(
    request.requestId,
    result,
    request.parentOrigin
  );
}


function readRequest_(event) {
  const parameters =
    event && event.parameter
      ? event.parameter
      : {};

  return {
    requestId:
      String(parameters.requestId || ''),
    taskId:
      String(parameters.taskId || ''),
    answer:
      String(parameters.answer || ''),
    parentOrigin:
      String(parameters.parentOrigin || '')
  };
}


function validateRequest_(request) {
  if (!request.requestId) {
    throw new Error(
      'Die Anfrage enthaelt keine requestId.'
    );
  }

  if (!Object.prototype.hasOwnProperty.call(
    TASKS,
    request.taskId
  )) {
    throw new Error(
      'Diese Aufgabe ist auf dem Auswertungsserver nicht bekannt.'
    );
  }

  const answer =
    request.answer.trim();

  if (answer.length < 10) {
    throw new Error(
      'Bitte formuliere eine etwas ausfuehrlichere Antwort.'
    );
  }

  if (answer.length > MAX_ANSWER_LENGTH) {
    throw new Error(
      'Die Antwort ist zu lang.'
    );
  }
}


function evaluateWithGemini_(
  task,
  answer
) {
  const scriptProperties =
    PropertiesService.getScriptProperties();

  const apiKey =
    scriptProperties.getProperty(
      PROPERTY_API_KEY
    );

  if (!apiKey) {
    throw new Error(
      'Auf dem Apps-Script-Server fehlt die Script Property GEMINI_API_KEY.'
    );
  }

  const model =
    scriptProperties.getProperty(
      PROPERTY_MODEL
    ) ||
    DEFAULT_MODEL;

  const endpoint =
    'https://generativelanguage.googleapis.com/v1beta/interactions';

  const payload = {
    model:
      model,
    input:
      buildPrompt_(
        task,
        answer
      ),
    response_format: {
      type:
        'text',
      mime_type:
        'application/json',
      schema:
        EVALUATION_SCHEMA
    }
  };

  const response =
    UrlFetchApp.fetch(
      endpoint,
      {
        method:
          'post',
        contentType:
          'application/json',
        headers: {
          'x-goog-api-key':
            apiKey
        },
        payload:
          JSON.stringify(payload),
        muteHttpExceptions:
          true
      }
    );

  const statusCode =
    response.getResponseCode();

  const responseText =
    response.getContentText();

  if (
    statusCode < 200 ||
    statusCode >= 300
  ) {
    if (statusCode === 429) {
      throw new Error(
        'Das Gemini-Kontingent ist gerade ausgelastet oder aufgebraucht. Bitte warte kurz und versuche es dann erneut.'
      );
    }

    throw new Error(
      'Gemini hat die Anfrage abgelehnt. HTTP-Status: ' +
      statusCode
    );
  }

  const geminiResponse =
    JSON.parse(responseText);

  const text =
    extractGeminiText_(geminiResponse);

  const evaluation =
    text
      ? JSON.parse(text)
      : geminiResponse;

  return applyRuleBasedMinimum_(
    normalizeEvaluation_(
      evaluation,
      task.maxPoints
    ),
    task,
    answer
  );
}


function applyRuleBasedMinimum_(
  evaluation,
  task,
  answer
) {
  let ruleBasedEvaluation = null;

  if (task === TASKS.b) {
    ruleBasedEvaluation =
      evaluateTaskBByRules_(
        answer,
        task.maxPoints
      );
  }

  if (task === TASKS.c) {
    ruleBasedEvaluation =
      evaluateTaskCByRules_(
        answer,
        task.maxPoints
      );
  }

  if (task === TASKS['2b']) {
    ruleBasedEvaluation =
      evaluateTask2BByRules_(
        answer,
        task.maxPoints
      );
  }

  if (!ruleBasedEvaluation) {
    return evaluation;
  }

  if (
    ruleBasedEvaluation.points >
    evaluation.points
  ) {
    return ruleBasedEvaluation;
  }

  return evaluation;
}


function evaluateTaskBByRules_(
  answer,
  maxPoints
) {
  const normalizedAnswer =
    normalizeGermanText_(
      answer
    );

  const strengths = [];
  const missing = [];
  let points = 0;

  if (containsAny_(normalizedAnswer, [
    'erstellt',
    'erzeugt',
    'angelegt',
    'new circle',
    'neuer kreis',
    'kreis wird erstellt',
    'ball wird erstellt'
  ])) {
    points += 1;
    strengths.push(
      'Du erkennst, dass zuerst ein Kreis beziehungsweise Ball erstellt wird.'
    );
  } else {
    missing.push(
      'Erklaere, dass in der ersten Zeile ein Kreis-Objekt erzeugt wird.'
    );
  }

  if (containsAny_(normalizedAnswer, [
    'bewegt',
    'verschoben',
    'move',
    'wandert'
  ])) {
    points += 1;
    strengths.push(
      'Du beschreibst, dass der Kreis bewegt oder verschoben wird.'
    );
  } else {
    missing.push(
      'Ergaenze, dass move(10, 10) den Kreis verschiebt.'
    );
  }

  if (containsAny_(normalizedAnswer, [
    'rot',
    'red',
    'color.red'
  ])) {
    points += 1;
    strengths.push(
      'Du nennst die rote Faerbung durch setFillColor(Color.red).'
    );
  } else if (containsAny_(normalizedAnswer, [
    'blau',
    'blue',
    'gefaerbt',
    'farbig',
    'farbe'
  ])) {
    missing.push(
      'Die Farbe ist im Programm rot, nicht blau.'
    );
  } else {
    missing.push(
      'Ergaenze, dass setFillColor(Color.red) den Kreis rot faerbt.'
    );
  }

  if (containsAny_(normalizedAnswer, [
    'zerstoert',
    'zerstort',
    'entfernt',
    'geloescht',
    'geloscht',
    'verschwindet',
    'destroy'
  ])) {
    points += 1;
    strengths.push(
      'Du erkennst, dass der Kreis am Ende entfernt oder zerstoert wird.'
    );
  } else {
    missing.push(
      'Erklaere, dass destroy() den Kreis wieder entfernt.'
    );
  }

  if (containsAny_(normalizedAnswer, [
    'dann',
    'anschliessend',
    'anschließend',
    'danach',
    'zuerst'
  ])) {
    points += 1;
    strengths.push(
      'Du beschreibst die Reihenfolge des Programms nachvollziehbar.'
    );
  }

  if (containsAny_(normalizedAnswer, [
    'position',
    'groesse',
    'grosse',
    'größe',
    'koordinate',
    'x',
    'y',
    '200',
    '50'
  ])) {
    points += 1;
    strengths.push(
      'Du gehst auf die Zahlen fuer Position oder Groesse ein.'
    );
  } else {
    missing.push(
      'Fuer die volle Punktzahl solltest du noch die Bedeutung der Zahlen erwaehnen.'
    );
  }

  points =
    clampNumber_(
      points,
      0,
      maxPoints
    );

  return {
    ok:
      true,
    points:
      points,
    maxPoints:
      maxPoints,
    status:
      points >= maxPoints * 0.75
        ? 'gut'
        : points >= maxPoints * 0.4
          ? 'teilweise richtig'
          : 'noch unvollstaendig',
    strengths:
      strengths.slice(0, 4),
    missing:
      missing.slice(0, 4),
    feedback:
      points >= maxPoints * 0.75
        ? 'Deine Antwort trifft die wichtigsten Programmschritte. Achte noch auf genaue Fachbegriffe und die richtige Farbe.'
        : 'Du hast einige wichtige Programmschritte erkannt. Ergaenze noch die fehlenden Details, besonders Farbe und Zahlen.'
  };
}


function evaluateTaskCByRules_(
  answer,
  maxPoints
) {
  const normalizedAnswer =
    normalizeGermanText_(
      answer
    );

  const strengths = [];
  const missing = [];
  let points = 0;

  const mentionsCircleLine =
    containsAny_(normalizedAnswer, [
      'zeile 1',
      'erste zeile',
      'circle',
      'new circle',
      'zahlen in zeile 1'
    ]);

  const mentionsCoordinates =
    containsAny_(normalizedAnswer, [
      'x und y',
      'x- und y',
      'x koordinate',
      'x-koordinate',
      'y koordinate',
      'y-koordinate',
      'koordinate',
      'koordinaten'
    ]);

  const mentionsSize =
    containsAny_(normalizedAnswer, [
      'radius',
      'groesse',
      'grosse',
      'durchmesser',
      'breite',
      'hoehe'
    ]);

  const mentionsMove =
    containsAny_(normalizedAnswer, [
      'move',
      'bei move',
      'verschiebung',
      'verschoben',
      'bewegt'
    ]);

  if (
    mentionsCircleLine &&
    mentionsCoordinates
  ) {
    points += 2;
    strengths.push(
      'Du erkennst, dass zwei Zahlen in der ersten Zeile die Position beziehungsweise Koordinaten betreffen.'
    );
  } else {
    missing.push(
      'Erklaere, dass zwei Zahlen in new Circle(...) die x- und y-Position festlegen.'
    );
  }

  if (
    mentionsCircleLine &&
    mentionsSize
  ) {
    points += 1;
    strengths.push(
      'Du erkennst, dass eine Zahl die Groesse beziehungsweise den Radius des Kreises beschreibt.'
    );
  } else {
    missing.push(
      'Ergaenze, dass eine Zahl in new Circle(...) die Groesse des Kreises beeinflusst.'
    );
  }

  if (
    mentionsMove &&
    mentionsCoordinates
  ) {
    points += 2;
    strengths.push(
      'Du beschreibst, dass move(...) die Verschiebung in x- und y-Richtung angibt.'
    );
  } else {
    missing.push(
      'Erklaere, dass die beiden Zahlen in move(10, 10) die Verschiebung in x- und y-Richtung angeben.'
    );
  }

  if (
    containsAny_(normalizedAnswer, [
      'erste zahl',
      'zweite zahl',
      'dritte zahl',
      '200',
      '50'
    ])
  ) {
    points += 1;
    strengths.push(
      'Du ordnest einzelne Zahlen oder Zahlenpositionen genauer zu.'
    );
  } else {
    missing.push(
      'Fuer die volle Punktzahl solltest du noch genauer sagen, welche Zahl wofuer steht.'
    );
  }

  points =
    clampNumber_(
      points,
      0,
      maxPoints
    );

  return {
    ok:
      true,
    points:
      points,
    maxPoints:
      maxPoints,
    status:
      points >= maxPoints * 0.75
        ? 'gut'
        : points >= maxPoints * 0.4
          ? 'teilweise richtig'
          : 'noch unvollstaendig',
    strengths:
      strengths.slice(0, 4),
    missing:
      missing.slice(0, 4),
    feedback:
      points >= maxPoints * 0.75
        ? 'Deine Antwort beschreibt die Bedeutung der Zahlen schon gut. Fuer die volle Punktzahl ordne die einzelnen Zahlen noch genauer zu.'
        : 'Du hast wichtige Beobachtungen genannt. Ergaenze noch genauer, welche Zahl welche Wirkung hat.'
  };
}


function evaluateTask2BByRules_(
  answer,
  maxPoints
) {
  const normalizedAnswer =
    normalizeGermanText_(
      answer
    );

  const strengths = [];
  const missing = [];
  let points = 0;

  if (containsAny_(normalizedAnswer, [
    'petershund',
    'wuffti',
    '5',
    'erzeugt',
    'erstellt',
    'new hund'
  ])) {
    points += 1;
    strengths.push(
      'Du erkennst, dass fuer Peter ein Hund-Objekt mit passenden Daten erzeugt wird.'
    );
  } else {
    missing.push(
      'Erklaere, dass petersHund als Hund mit Alter 5 und Name Wuffti erzeugt wird.'
    );
  }

  if (containsAny_(normalizedAnswer, [
    'inashund',
    'schnuffi',
    '8',
    'zweiter hund',
    'new hund'
  ])) {
    points += 1;
    strengths.push(
      'Du erkennst, dass ein zweites Hund-Objekt fuer Ina erzeugt wird.'
    );
  } else {
    missing.push(
      'Ergaenze, dass inasHund als Hund mit Alter 8 und Name Schnuffi erzeugt wird.'
    );
  }

  if (
    containsAny_(normalizedAnswer, [
      'konstruktor',
      'parameter',
      'alter',
      'name'
    ]) &&
    containsAny_(normalizedAnswer, [
      '5',
      '8',
      'wuffti',
      'schnuffi'
    ])
  ) {
    points += 1;
    strengths.push(
      'Du beschreibst, dass die Werte an den Konstruktor uebergeben werden.'
    );
  } else {
    missing.push(
      'Ergaenze, dass die Klammerwerte an den Konstruktor fuer Alter und Name uebergeben werden.'
    );
  }

  if (
    containsAny_(normalizedAnswer, [
      'petershund.zeigedaten',
      'peters hund',
      'zeige daten',
      'daten aus'
    ]) &&
    containsAny_(normalizedAnswer, [
      'peter',
      'petershund',
      'wuffti'
    ])
  ) {
    points += 1;
    strengths.push(
      'Du erklaerst den Methodenaufruf zeigeDaten fuer petersHund.'
    );
  } else {
    missing.push(
      'Beschreibe, dass petersHund.zeigeDaten() die Daten von petersHund ausgibt.'
    );
  }

  if (
    containsAny_(normalizedAnswer, [
      'inashund.zeigedaten',
      'inas hund',
      'zeige daten',
      'daten aus'
    ]) &&
    containsAny_(normalizedAnswer, [
      'ina',
      'inashund',
      'schnuffi'
    ])
  ) {
    points += 1;
    strengths.push(
      'Du erklaerst den Methodenaufruf zeigeDaten fuer inasHund.'
    );
  } else {
    missing.push(
      'Beschreibe, dass inasHund.zeigeDaten() die Daten von inasHund ausgibt.'
    );
  }

  if (containsAny_(normalizedAnswer, [
    'bellt',
    'bellen',
    'belle',
    'wuff wuff',
    'belltext'
  ])) {
    points += 1;
    strengths.push(
      'Du erkennst, dass inasHund.belle() den Hund bellen laesst.'
    );
  } else {
    missing.push(
      'Ergaenze, dass inasHund.belle() die Methode belle aufruft und einen Belltext ausgibt.'
    );
  }

  points =
    clampNumber_(
      points,
      0,
      maxPoints
    );

  return {
    ok:
      true,
    points:
      points,
    maxPoints:
      maxPoints,
    status:
      points >= maxPoints * 0.75
        ? 'gut'
        : points >= maxPoints * 0.4
          ? 'teilweise richtig'
          : 'noch unvollstaendig',
    strengths:
      strengths.slice(0, 4),
    missing:
      missing.slice(0, 4),
    feedback:
      points >= maxPoints * 0.75
        ? 'Deine Antwort erklaert die Objekte und Methodenaufrufe schon gut. Achte noch darauf, Alter, Name und Methoden genau zuzuordnen.'
        : 'Du hast erste Programmschritte erkannt. Ergaenze noch genauer, welche Objekte erzeugt werden und welche Methoden aufgerufen werden.'
  };
}


function normalizeGermanText_(text) {
  return String(text || '')
    .toLowerCase()
    .replace(/ä/g, 'ae')
    .replace(/ö/g, 'oe')
    .replace(/ü/g, 'ue')
    .replace(/ß/g, 'ss')
    .replace(/\s+/g, ' ')
    .trim();
}


function containsAny_(
  text,
  needles
) {
  return needles.some(
    function(needle) {
      return text.includes(
        normalizeGermanText_(
          needle
        )
      );
    }
  );
}


function buildPrompt_(
  task,
  answer
) {
  return [
    'Du bist eine hilfreiche, faire Informatik-Lehrkraft in Klasse 9.',
    'Bewerte eine kurze Schuelerantwort zu einem Java-/LearnJ-Programm.',
    '',
    'Programm:',
    task.program,
    '',
    'Aufgabe:',
    task.title,
    task.instruction,
    '',
    'Erwartete Aspekte:',
    task.expectedAspects
      .map(function(aspect, index) {
        return (index + 1) + '. ' + aspect;
      })
      .join('\n'),
    '',
    'Bewerte fachlich wohlwollend, aber nicht beliebig.',
    'Gib keine personenbezogenen Daten aus.',
    'Erfinde keine zusaetzlichen Informationen.',
    'Wenn die Antwort unklar ist, gib konkrete Hinweise zum Verbessern.',
    '',
    'Antworte ausschliesslich als JSON-Objekt mit diesen Feldern:',
    '{',
    '  "points": Zahl von 0 bis ' + task.maxPoints + ',',
    '  "maxPoints": ' + task.maxPoints + ',',
    '  "status": kurze Bewertung wie "gut", "teilweise richtig" oder "noch unvollstaendig",',
    '  "strengths": Array mit 0 bis 4 kurzen Strings,',
    '  "missing": Array mit 0 bis 4 kurzen Strings,',
    '  "feedback": ein kurzer, motivierender Feedbacktext',
    '}',
    '',
    'Schuelerantwort:',
    answer
  ].join('\n');
}


function extractGeminiText_(geminiResponse) {
  if (
    geminiResponse &&
    geminiResponse.output_text
  ) {
    return String(geminiResponse.output_text).trim();
  }

  if (
    geminiResponse &&
    Array.isArray(geminiResponse.output)
  ) {
    const outputText =
      geminiResponse.output
        .map(function(item) {
          if (item.text) {
            return item.text;
          }

          if (Array.isArray(item.content)) {
            return item.content
              .map(function(contentItem) {
                return (
                  contentItem.text ||
                  contentItem.output_text ||
                  ''
                );
              })
              .join('');
          }

          return '';
        })
        .join('')
        .trim();

    if (outputText) {
      return outputText;
    }
  }

  if (
    geminiResponse &&
    geminiResponse.candidates &&
    geminiResponse.candidates.length > 0 &&
    geminiResponse.candidates[0].content &&
    geminiResponse.candidates[0].content.parts
  ) {
    return geminiResponse.candidates[0].content.parts
      .map(function(part) {
        return part.text || '';
      })
      .join('')
      .trim();
  }

  if (
    geminiResponse &&
    geminiResponse.text
  ) {
    return String(geminiResponse.text).trim();
  }

  return '';
}


function normalizeEvaluation_(
  evaluation,
  maxPoints
) {
  const points =
    clampNumber_(
      evaluation.points,
      0,
      maxPoints
    );

  return {
    ok:
      true,
    points:
      points,
    maxPoints:
      maxPoints,
    status:
      cleanText_(
        evaluation.status,
        points >= maxPoints * 0.75
          ? 'gut'
          : points >= maxPoints * 0.4
            ? 'teilweise richtig'
            : 'noch unvollstaendig'
      ),
    strengths:
      cleanStringArray_(
        evaluation.strengths
      ),
    missing:
      cleanStringArray_(
        evaluation.missing
      ),
    feedback:
      cleanText_(
        evaluation.feedback,
        'Ueberarbeite deine Antwort mithilfe der Hinweise.'
      )
  };
}


function clampNumber_(
  value,
  min,
  max
) {
  const number =
    Number(value);

  if (!isFinite(number)) {
    return min;
  }

  return Math.max(
    min,
    Math.min(
      max,
      Math.round(number)
    )
  );
}


function cleanText_(
  value,
  fallback
) {
  const text =
    String(value || '')
      .replace(/\s+/g, ' ')
      .trim();

  return text || fallback;
}


function cleanStringArray_(value) {
  if (!Array.isArray(value)) {
    return [];
  }

  return value
    .map(function(item) {
      return cleanText_(item, '');
    })
    .filter(function(item) {
      return item.length > 0;
    })
    .slice(0, 4);
}


function createErrorResult_(message) {
  return {
    ok:
      false,
    message:
      message
  };
}


function createPostMessageResponse_(
  requestId,
  result,
  parentOrigin
) {
  const payload = {
    type:
      RESULT_MESSAGE_TYPE,
    requestId:
      requestId || '',
    result:
      result
  };

  const html =
    '<!doctype html>' +
    '<meta charset="utf-8">' +
    '<title>Auswertung</title>' +
    '<script>' +
    '(function(){' +
    'var payload=' + toScriptJson_(payload) + ';' +
    'var payloadText=' + toScriptJson_(JSON.stringify(payload)) + ';' +
    'var targets=[window.parent,window.top];' +
    'try{if(window.parent&&window.parent.parent){targets.push(window.parent.parent);}}catch(error){}' +
    'for(var i=0;i<targets.length;i++){' +
    'try{targets[i].postMessage(payload,"*");}catch(error){}' +
    'try{targets[i].postMessage(payloadText,"*");}catch(error){}' +
    '}' +
    '}());' +
    '</script>' +
    '<p>Die Auswertung wurde an die Unterrichtsseite uebermittelt.</p>';

  return HtmlService
    .createHtmlOutput(html)
    .setTitle('Auswertung');
}


function toScriptJson_(value) {
  return JSON.stringify(value)
    .replace(/</g, '\\u003c')
    .replace(/>/g, '\\u003e')
    .replace(/&/g, '\\u0026')
    .replace(/\u2028/g, '\\u2028')
    .replace(/\u2029/g, '\\u2029');
}
