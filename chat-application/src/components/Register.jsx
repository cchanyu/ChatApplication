import React, { useState, useEffect } from 'react'
import { createUserWithEmailAndPassword, updateProfile } from "firebase/auth";
import { ref, uploadBytesResumable, getDownloadURL } from "firebase/storage";
import { db, auth, storage } from '../firebase/firebase.config';
import { doc, setDoc } from 'firebase/firestore';

import Add from '../img/addAvatar.png';
import '../css/Register.scss';
import { useNavigate, Link } from 'react-router-dom';
import DefaultAvatar from '../img/person.jpg';


const Register = () => {
  const [err, setErr] = useState(false);
  const [warning, setWarning] = useState(false);
  const [loading, setLoading] = useState(false);
  const [img, setImg] = useState(DefaultAvatar);
  const navigate = useNavigate();

  useEffect(() => {
    setImg(DefaultAvatar)
  }, [])

  const handleSubmit = async (e) => {
    e.preventDefault()
    const displayName = e.target[0].value;
    const email = e.target[1].value;
    const password = e.target[2].value;

    if (displayName === '' || email === '' || password === '') {
      setWarning(true)
      setTimeout(() => {
        setWarning(false);
      }, 5000);
      return;
    }
    
    setLoading(true);

    try {
      const res = await createUserWithEmailAndPassword(auth, email, password);
      const date = new Date().getTime();
      const storageRef = ref(storage, `${displayName + date}`);

      await uploadBytesResumable(storageRef, img).then(() => {
        getDownloadURL(storageRef).then(async (downloadURL) => {
            try{
              //Update profile
              await updateProfile(res.user, {
                displayName,
                photoURL: downloadURL,
              });
              // posting this to allow another user to reach to you
              await setDoc(doc(db, "users", res.user.uid), {
                uid: res.user.uid,
                displayName,
                email,
                photoURL: downloadURL,
              });

              // initialiating empty list of friends to chat with
              await setDoc(doc(db, "userChats", res.user.uid), {})
              navigate("/ChatApplication/")
            } catch (err) {
              console.log(err);
              setErr(true);
              setLoading(false);
            }
          });
        });
    } catch (err) {
      setErr(true)
      setLoading(false);
    }
  }

  return (
    <div className='register'>
      <div className='register-wrapper'>
        <span className="logo">Chat App</span>
        <span className="title">Register</span>
        <form onSubmit={handleSubmit}>
          <input type='text' placeholder='display name' />
          <input type='email' placeholder='email' />
          <input type='password' placeholder='password' />
          {img?.name === undefined ? <div className='attach'>
            <input style={{display:"none"}} type='file' id='file' onChange={e => setImg(e.target.files[0])} />
            <label htmlFor='file'>
              <img src={Add} alt='add' />
              <span>Add an Avatar</span>
            </label>
          </div> : <button className='cancel' onClick={() => setImg(null)}>X</button>}
          <button disabled={loading}>Sign up</button>
          {loading && "Registering... Please wait."}
          {err && <span>Something went wrong</span>}
          {warning && <span style={{color: "red"}}>Please fill all the form</span>}
        </form>
        <p>Do you have an account? <Link to="/ChatApplication/login">Login</Link></p>
      </div>
    </div>
  )
}

export default Register