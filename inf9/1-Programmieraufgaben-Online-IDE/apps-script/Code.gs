/**
 * Apps-Script-Auswertungsserver fuer
 * inf9/1-Programmieraufgaben-Online-IDE/index.html.
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
    expectedAspects: [
      'Die erste Zahl in new Circle(200, 50, 50) beeinflusst die horizontale Position des Kreises.',
      'Die zweite Zahl in new Circle(200, 50, 50) beeinflusst die vertikale Position des Kreises.',
      'Die dritte Zahl in new Circle(200, 50, 50) beeinflusst die Groesse des Kreises.',
      'Die erste Zahl in move(10, 10) beschreibt die horizontale Verschiebung.',
      'Die zweite Zahl in move(10, 10) beschreibt die vertikale Verschiebung.',
      'Die Antwort beruht erkennbar auf einzelnen Veraenderungen und Beobachtungen.'
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
function doGet() {
  return HtmlService
    .createHtmlOutput(
      '<!doctype html>' +
      '<meta charset="utf-8">' +
      '<title>Auswertungsserver</title>' +
      '<h1>Auswertungsserver ist erreichbar</h1>' +
      '<p>POST-Anfragen der Unterrichtsseite koennen verarbeitet werden.</p>'
    );
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

  return normalizeEvaluation_(
    evaluation,
    task.maxPoints
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
    'Circle ball = new Circle(200, 50, 50);',
    'ball.move(10, 10);',
    'ball.setFillColor(Color.red);',
    'ball.destroy();',
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
    'var targets=[window.parent,window.top];' +
    'try{if(window.parent&&window.parent.parent){targets.push(window.parent.parent);}}catch(error){}' +
    'for(var i=0;i<targets.length;i++){' +
    'try{targets[i].postMessage(payload,"*");}catch(error){}' +
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
