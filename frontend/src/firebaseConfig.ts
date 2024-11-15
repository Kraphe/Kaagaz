// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import {getStorage} from 'firebase/storage'
const firebaseConfig = {
  apiKey: "AIzaSyDh2e7GENUkX4-dOw082RneO3-dPjkF3ms",
  authDomain: "project-upload-4bbfd.firebaseapp.com",
  projectId: "project-upload-4bbfd",
  storageBucket: "project-upload-4bbfd.appspot.com",
  messagingSenderId: "534446741418",
  appId: "1:534446741418:web:a44b837bf3c8112963bdd3",
  measurementId: "G-B1T2TGKX5N"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const storage = getStorage(app);


// import { initializeApp } from "firebase/app";
// import { getStorage } from "firebase/storage";

// const firebaseConfig = {
  
//   apiKey: import.meta.env.VITE_apiKey,
//   authDomain: import.meta.env.VITE_authDomain,
//   projectId: import.meta.env.VITE_projectId,
//   storageBucket: import.meta.env.VITE_storageBucket,
//   messagingSenderId: import.meta.env.VITE_messagingSenderId,
//   appId: import.meta.env.VITE_appId,
// };

// const app = initializeApp(firebaseConfig);
// const storage = getStorage(app);
// export { storage };
