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

beforeEach(async () => {
  await testEnv.clearFirestore();
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

test("unauthenticated user cannot create profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(
    unauthDb.collection("users").doc(ALICE_UID).set({
      id: ALICE_UID,
      username: "alice",
      displayName: "Alice In Wonderland"
    })
  );
});

test("authenticated user can create their own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      id: ALICE_UID,
      username: "alice",
      displayName: "Alice In Wonderland",
      followerCount: 0,
      followingCount: 0,
      likesCount: 0,
      verified: false
    })
  );
});

test("Bob cannot update Alice's profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await aliceDb.collection("users").doc(ALICE_UID).set({
    id: ALICE_UID,
    username: "alice",
    displayName: "Alice In Wonderland"
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      displayName: "Hacked by Bob"
    })
  );
});

test("Alice can create video post and Bob can read it", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("videos").doc("vid_1").set({
      userId: ALICE_UID,
      videoUrl: "https://example.com/dance.mp4",
      caption: "My viral dance! #vibez",
      creatorName: "Alice",
      creatorUsername: "alice",
      likesCount: 0,
      commentsCount: 0,
      sharesCount: 0
    })
  );

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(bobDb.collection("videos").doc("vid_1").get());
});

test("Bob can add a comment to Alice's video", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await aliceDb.collection("videos").doc("vid_1").set({
    userId: ALICE_UID,
    videoUrl: "https://example.com/dance.mp4",
    caption: "My viral dance! #vibez"
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertSucceeds(
    bobDb.collection("videos").doc("vid_1").collection("comments").doc("c_1").set({
      videoId: "vid_1",
      userId: BOB_UID,
      username: "bob",
      text: "Awesome vibes! 🔥",
      likesCount: 0
    })
  );
});
