import express from "express";
import bodyParser from "body-parser";
import fetch from "node-fetch";

let app = express();
app.use(bodyParser.urlencoded({ extended: false }));

// ================== firebase ================== //
import { initializeApp } from "firebase/app";
import {
  getAuth,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
} from "firebase/auth";
import { getDatabase, ref, onValue, set, update } from "firebase/database";

// Your web app's Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyBZ9AdFgch5AAuhtMz5qOhkek98RP0DvZI",
  authDomain: "milabstudyzones.firebaseapp.com",
  databaseURL: "https://milabstudyzones-default-rtdb.firebaseio.com",
  projectId: "milabstudyzones",
  storageBucket: "milabstudyzones.appspot.com",
  messagingSenderId: "2376899614",
  appId: "1:2376899614:web:7f70334a7645cd1df7620f",
};
// Initialize Firebase
const appFirebase = initializeApp(firebaseConfig);
const db = getDatabase();

// ================== firebase ================== //



// ================== Authentication ================== //
app.get("/userCheck", function (req, res) {

  const auth = getAuth();
  signInWithEmailAndPassword(auth, req.query.email, req.query.password)
    .then((userCredential) => {
      // Signed in
      const user = userCredential.user;
      console.log(`The user ${user} sign in successfully`);
      res.send(JSON.stringify({ isUser: true }));
    })
    .catch((error) => {
      const errorCode = error.code;
      const errorMessage = error.message;
      console.log(`errorCode:  ${errorCode}`);
      console.log(`errorMessage:  ${errorMessage}`);
      res.send(JSON.stringify({ isUser: false }));
    });
});

app.get("/userSignUp", function (req, res) {

  const auth = getAuth();
  createUserWithEmailAndPassword(auth, req.query.email, req.query.password)
    .then((userCredential) => {
      // Signed Up
      const user = userCredential.user;
      console.log(`The user ${user} sign Up successfully`);
      res.send(JSON.stringify({ isCreatedUser: true }));
    })
    .catch((error) => {
      const errorCode = error.code;
      const errorMessage = error.message;
      console.log(`errorCode:  ${errorCode}`);
      console.log(`errorMessage:  ${errorMessage}`);
      res.send(JSON.stringify({ isCreatedUser: false }));
    });
});
// ================== Authentication ================== //



// ================== FireBase To Map ================== //
app.get("/getstudyzones", function (req, res) {
  onValue(ref(db), (snapshot) => res.send(JSON.stringify(snapshot.val())));
});

app.get("/createstudyzone", function (req, res) {
  let crowded = parseInt(req.query.crowded);
  let food = parseInt(req.query.food);
  let price = parseInt(req.query.price);

  let totalRating = parseFloat(
    ((1 / 3) * crowded + (1 / 3) * food + (1 / 3) * price).toFixed(2)
  );

  let ApiKey = "e199c9caa1682e987d96e42feafe3106";
  let path = `http://api.positionstack.com/v1/forward?access_key=${ApiKey}&query=${req.query.city}`;
  fetch(path)
    .then((response) => response.json())
    .then((data) => {
      let lat = data.data[0].latitude;
      let lon = data.data[0].longitude;
      const refrence = ref(db, "studyZones/" + req.query.name);
      set(refrence, {
        location: { latitude: lat, longitude: lon },
        totalRating: totalRating,
        crowded: { rating: crowded, raters: [crowded] },
        food: { rating: food, raters: [food] },
        price: { rating: price, raters: [price] },
      });
      onValue(refrence, (snapshot) => res.send(JSON.stringify(snapshot.val())));
    });
});

app.get("/infostudyzone", function (req, res) {
  const refrence = ref(db, "studyZones/" + req.query.name);
  onValue(refrence, (snapshot) => {
    let data = snapshot.val();
    console.log(data);
    let answer = {
      name: req.query.name,
      crowded: data.crowded.rating,
      food: data.food.rating,
      price: data.price.rating,
      totalRating: data.totalRating,
      location: { latitude: data.location.latitude, longitude: data.location.longitude },
      //latitude: data.location.latitude,
      //longitude: data.location.longitude,
    };
    res.send(JSON.stringify(answer));
  });
});

app.get("/updatestudyzones", function (req, res) {
  const refrence = ref(db, "studyZones/" + req.query.name);
  onValue(refrence, (snapshot) => {
    let data = snapshot.val();

    function updateRatings(key) {
      data[key].raters.push(parseInt(req.query[key]));
      let sum = data[key].raters.reduce((a, b) => a + b, 0);
      let len = data[key].raters.length;
      data[key].rating = parseFloat((sum / len).toFixed(2));
    }
    updateRatings("crowded");
    updateRatings("food");
    updateRatings("price");

    data.totalRating = parseFloat(
      (
        (1 / 3) * data.crowded.rating +
        (1 / 3) * data.food.rating +
        (1 / 3) * data.price.rating
      ).toFixed(2)
    );
    set(refrence, data);
    res.send(JSON.stringify(data));
  });
});

// ================== FireBase To Map ================== //

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`app is running on port ${PORT}`);
});
