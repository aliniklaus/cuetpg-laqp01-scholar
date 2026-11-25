const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: denied all reads and writes", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("noteProgress").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("testAttempts").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("notebookLinks").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("savedAiNotes").get());
});

test("NoteProgress: owner create, partial update, cross-user denial, ghost field & id rejection", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 10000);
  const noteRef = aliceDb.collection("users").doc(ALICE_UID).collection("noteProgress").doc("note_u1_1");

  await assertSucceeds(
    noteRef.set({
      userId: ALICE_UID,
      noteId: "note_u1_1",
      unitId: "unit_1",
      bookmarked: true,
      mastered: false,
      personalNote: "Revise Metaphysical poets",
      createdAt: created,
      updatedAt: created,
    })
  );

  // Partial update allowed fields only
  await assertSucceeds(
    noteRef.update({
      mastered: true,
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject immutable createdAt modification
  await assertFails(
    noteRef.update({
      createdAt: new Date(Date.now() - 500),
      updatedAt: new Date(Date.now() - 500),
    })
  );

  // Reject ghost field / @DocumentId 'id' leakage
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("noteProgress").doc("note_u1_2").set({
      id: "note_u1_2",
      userId: ALICE_UID,
      noteId: "note_u1_2",
      unitId: "unit_1",
      bookmarked: true,
      mastered: false,
      createdAt: created,
      updatedAt: created,
    })
  );

  // Cross-user isolation
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("noteProgress").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("noteProgress").doc("note_u1_1").get());
});

test("TestAttempts: owner create & list succeed, updates denied, invalid score bounds fail", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);
  const attemptRef = aliceDb.collection("users").doc(ALICE_UID).collection("testAttempts").doc("attempt_1");

  await assertSucceeds(
    attemptRef.set({
      userId: ALICE_UID,
      mode: "MOCK",
      title: "CUET PG LAQP01 Full Mock #1",
      unitFilter: "All Units",
      score: 215,
      maxScore: 300,
      correctCount: 56,
      wrongCount: 9,
      unattemptedCount: 10,
      durationSeconds: 4120,
      unitAccuracySummary: "Unit 1: 82% • Unit 6: 75%",
      createdAt: created,
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("testAttempts").get()
  );

  // Immutable once submitted
  await assertFails(attemptRef.update({ score: 300 }));

  // Reject future timestamp
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("testAttempts").doc("attempt_2").set({
      userId: ALICE_UID,
      mode: "PRACTICE",
      title: "Unit 3 Drill",
      unitFilter: "Unit 3",
      score: 36,
      maxScore: 40,
      correctCount: 9,
      wrongCount: 0,
      unattemptedCount: 1,
      durationSeconds: 300,
      createdAt: new Date(Date.now() + 60000),
    })
  );

  // Cross-user read denied
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("testAttempts").doc("attempt_1").get());
});

test("NotebookLinks: owner CRUD, https URL validation, and cross-user denial", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const created = new Date(Date.now() - 5000);
  const linkRef = aliceDb.collection("users").doc(ALICE_UID).collection("notebookLinks").doc("nb_1");

  await assertSucceeds(
    linkRef.set({
      userId: ALICE_UID,
      title: "Literary Theory & Postcolonialism",
      url: "https://notebooklm.google.com/notebook/abc-123",
      unitTag: "Unit 6",
      description: "Compiled Derrida, Said, Spivak PDFs",
      pinned: true,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertSucceeds(
    linkRef.update({
      pinned: false,
      title: "Unit 6 Theory Notebook",
      updatedAt: new Date(Date.now() - 1000),
    })
  );

  // Reject non-https URL
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("notebookLinks").doc("nb_2").set({
      userId: ALICE_UID,
      title: "Insecure Link",
      url: "http://notebooklm.google.com/notebook/xyz",
      unitTag: "Unit 1",
      pinned: false,
      createdAt: created,
      updatedAt: created,
    })
  );

  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("notebookLinks").get());
});

test("SavedAiNotes: owner create/list/delete and mode validation", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const created = new Date(Date.now() - 5000);
  const cardRef = aliceDb.collection("users").doc(ALICE_UID).collection("savedAiNotes").doc("card_1");

  await assertSucceeds(
    cardRef.set({
      userId: ALICE_UID,
      mode: "MNEMONIC",
      topic: "Romantic Poets Chronology",
      unitTag: "Unit 1",
      content: "Blake, Wordsworth, Coleridge (First Gen) -> Byron, Shelley, Keats (Second Gen).",
      createdAt: created,
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("savedAiNotes").get());
  await assertSucceeds(cardRef.delete());

  // Invalid mode rejected
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("savedAiNotes").doc("card_2").set({
      userId: ALICE_UID,
      mode: "UNKNOWN_MODE",
      topic: "Test",
      unitTag: "Unit 1",
      content: "Body",
      createdAt: created,
    })
  );
});
