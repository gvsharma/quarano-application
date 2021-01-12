/// <reference types="cypress" />

describe('S1 - Externe PLZ führt zu Status externe PLZ', () => {
  Cypress.config('defaultCommandTimeout', 20000);
  before((done) => {
    cy.restartBackend(done);

    cy.server();
    cy.route('POST', '/hd/cases/?type=index').as('newIndex');
    cy.route('GET', '/hd/cases/*').as('case');
    cy.route('PUT', '/hd/cases/*/registration').as('registration');
  });

  function extractActivationCode(elem: JQuery) {
    const regex = /\/client\/enrollment\/landing\/index\/(.*)/g;
    let content;

    if (typeof elem !== 'string') {
      content = elem.text();
    } else {
      content = elem;
    }

    try {
      return regex.exec(content)[1];
    } catch (e) {
      cy.log(e);
      throw e;
    }
  }
  it('run scenario 1', () => {
    // 0 - Login als Gama "agent1"
    cy.logInAgent();

    // 1 - wähle Übersichtsseite "Indexfälle"
    cy.get('[data-cy="index-cases"]').should('exist').click();

    // 2 - wähle "neuen Indexfall anlegen"
    cy.get('[data-cy="new-case-button"]').should('exist').click();

    // 3 - Vorname -> "Julia"
    cy.get('[data-cy="input-firstname"]').should('exist').type('Julia');

    // 4 - Nachname ->  "Klein"
    cy.get('[data-cy="input-lastname"]').should('exist').type('Klein');

    // 5 - Geburtsdatum -> "06.10.1982"
    cy.get('[data-cy="input-dayofbirth"]').should('exist').type('06.10.1982');

    // 6 - Telefonnummer -> "0621842357"
    cy.get('[data-cy="phone-number-input"]').should('exist').type('0621842357');

    // 7 - Email -> "jklein@gmx.de"
    cy.get('[data-cy="input-email"]').should('exist').type('jklein@gmx.de');

    // 8 - Straße -> "Hauptstraße"
    cy.get('[data-cy="street-input"]').should('exist').type('Hauptstraße');

    // 9 - Hausnummer -> "152"
    cy.get('[data-cy="house-number-input"]').should('exist').type('152');

    // 10 - PLZ von Mannheim -> "68199"
    cy.get('[data-cy="zip-code-input"]').should('exist').type('68199');

    // 11 - Stadt -> "Mannheim"
    cy.get('[data-cy="city-input"]').should('exist').type('Mannheim');

    // 12 - wähle "Speichern"
    cy.get('[data-cy="client-submit-button"]').should('exist').click();

    // 13 - wähle "Nachverfolgung Starten"
    cy.get('[data-cy="start-tracking-span"]').should('exist').click();

    // 14 - Extrahiere Anmeldelink aus dem Template
    cy.get('a.mat-tab-link.mat-focus-indicator.ng-star-inserted[ng-reflect-router-link="email"]')
      .should('exist')
      .click();
    cy.get('qro-client-mail > div > pre')
      .should('exist')
      .then((elem) => {
        // const regex = /https:\/\/.*\/client\/enrollment\/landing\/index\/.*/gm;
        // TODO sollte hier nicht wirklich die https verwendet werden?
        const regex = /\/client\/enrollment\/landing\/index\/.*/gm;
        let content;
        let url = '';

        if (typeof elem !== 'string') {
          content = elem.text();
        } else {
          content = elem;
        }

        const urls = regex.exec(content);
        if (urls && urls.length !== 0) {
          url = urls[0];
        }

        // 15 - Logout als GAMA
        cy.get('[data-cy="profile-user-button"]').should('exist').click();
        cy.get('[data-cy="logout-button"]').should('exist').click();

        // 16 - Anmeldelink aufrufen
        cy.visit(url);
      });

    // 17 - Klick auf Weiter
    cy.get('[data-cy="cta-button-index"]').should('exist').click();

    // 18 - Benutzername: "Julia"
    cy.get('[data-cy="input-username"]').should('exist').type('Julia');

    // 19 - Passwort: "Password01!"
    cy.get('[data-cy="input-password"]').should('exist').type('Password01!');

    // 20 - Passwort bestätgen  "Password01!"
    cy.get('[data-cy="input-password-confirm"]').should('exist').type('Password01!');

    // 21 - Geburtsdatum: 06.10.1982
    cy.get('[data-cy="input-dateofbirth"]').should('exist').type('06.10.1982');

    // 22 - AGB aktivieren
    cy.get('[data-cy="input-privacy-policy"]').should('exist').click();

    // 23 - Klick auf "Registrieren" Button
    cy.get('[data-cy="registration-submit-button"]').should('exist').click();

    // 24 - Klick auf "weiter"
    cy.get('[data-cy="first-step-button"]').should('exist').click();

    // 25 - Haben Sie bereits Covid-19 charakteristische Symptome festgestellt? -> "Nein"

    // 26 - Bitte geben Sie Ihren behandelnden Hausarzt an. -> Dr. Schmidt
    // 27 - Nennen Sie uns bitte den (vermuteten) Ort der Ansteckung: -> "Familie"
    // 28 - Haben Sie eine oder mehrere relevante Vorerkrankungen? -> "nein"
    // 29 - Arbeiten Sie im medizinischen Umfeld oder in der Pflege? -> "nein"
    // 30 - Haben Sie Kontakt zu Risikopersonen? -> "nein"
    // 31 - Klick "weiter"
    // 32 - Kontakte mit anderen Menschen -> "Manfred Klein"
    // 33 - Klick enter
    // 34 - wähle "Kontakt anlegen" in Popup
    // 35 - Telefonnummer (mobil) -> "01758631534"
    // 36 - Klick auf "speichern"
    // 37 - Klick auf "Erfassung abschließen"
    // 38 - Logout als Bürger
    // 39 - Login als GAMA "agent1"
    // 40 - suche Indexfall "Julia Klein"
    // 41 - wähle Reiter "Kontakte"
    // 42 - klick auf "Manfred Klein"
    // 43 - Geburtsdatum -> "25.07.1980"
    // 44 - Email -> "mklein@gmx.de"
    // 45 - wähle "Speichern"
    // 46 - wähle "Nachverfolgung Starten"
    // 47 - Extrahiere Anmeldelink aus dem Template
    // 48 - Logout als GAMA
    // 49 - Anmeldelink aufrufen
    // 50 - Klick auf "Weiter"
    // 51 - Benutzername: "Manfred"
    // 52 - Passwort: "Password02!"
    // 53 - Passwort bestätgen  "Password02!"
    // 54 - Geburtsdatum: "25.07.1980"
    // 55 - AGB aktivieren
    // 56 - Klick auf "Registrieren" Button
    // 57 - Straße -> "Hauptstraße"
    // 58 - Hausnummer -> "152"
    // 59 - PLZ von Ilvesheim -> "68549"
    // 60 - Stadt -> "Ilvesheim"
    // 61 - Klick auf "weiter"
    // CHECK: Popup erscheint mit Text "Bitte prüfen Sie die eingegebene PLZ
    // Das für die PLZ 68549 zuständige Gesundheitsamt arbeitet nicht mit dieser Software. Bitte überprüfen Sie zur Sicherheit Ihre Eingabe. Ist diese korrekt, dann verwenden Sie diese Software nicht weiter und wenden Sie sich bitte an Ihr zuständiges Gesundheitsamt."
    // 62 - Klick "PLZ bestätigen"
    // CHECK: Es erscheint folgender Text: "Das für Sie zuständige Gesundheitsamt arbeitet nicht mit Quarano. Bitte wenden Sie sich direkt an Ihr Gesundheitsamt.
    // Landratsamt Rhein-Neckar-Kreis; Gesundheitsamt; Kurfürstenanlage 38-40; 69115 Heidelberg
    // E-Mail:	infektionsschutz@rhein-neckar-kreis.de; Telefon:	062215221817; Fax:	062215221899"
    // CHECK: neue Anmeldung als "Manfred" (Passwort: "Password02!") ist nicht möglich
    // 63 - Login als GAMA "agent1"
    // 64 - wähle Übersichtsseite "Kontaktfälle"
    // CHECK: Status bei "Manfred Klein" ist "Externe PLZ"
    // 65 - Logout als GAMA
  });
});
