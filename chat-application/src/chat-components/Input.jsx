import React, { useCallback, useContext, useEffect, useState } from 'react'
import Img from '../img/img.png'
import Attach from '../img/attach.png'
import { AuthContext } from '../context/AuthContext'
import { ChatContext } from '../context/ChatContext'
import { Timestamp, arrayUnion, doc, serverTimestamp, updateDoc } from 'firebase/firestore'
import { db, storage } from '../firebase/firebase.config'
import { v4 as uuid } from 'uuid';
import { getDownloadURL, ref, uploadBytesResumable } from 'firebase/storage'

const Input = () => {
  const [err, setErr] = useState(false)
  const [text, setText] = useState("")
  const [img, setImg] = useState(null)

  const {currentUser} = useContext(AuthContext)
  const {data} = useContext(ChatContext)

  const handleSend = useCallback(async () => {
    if (text === '' && img === null) {
      return;
    }

    if(img) {
      const storageRef = ref(storage, uuid());
      const uploadTask = uploadBytesResumable(storageRef, img);

      uploadTask.on (
        (err) => {
          setErr(false)
        },
        () => {
          getDownloadURL(uploadTask.snapshot.ref).then(async(downloadURL) => {
            await updateDoc(doc(db, "chats", data.chatId), {
              messages: arrayUnion({
                id: uuid(),
                text,
                senderId: currentUser.uid,
                date: Timestamp.now(),
                img: downloadURL,
              }),
            })
          })
        }
      )
    } else {
      await updateDoc(doc(db, "chats", data.chatId), {
        messages: arrayUnion({
          id: uuid(),
          text,
          senderId: currentUser.uid,
          date: Timestamp.now(),
        }),
      })
    }

    await updateDoc(doc(db, "userChats", currentUser.uid), {
      [data.chatId + ".lastMessage"]:{
        text
      },
      [data.chatId+".date"]: serverTimestamp()
    })
    await updateDoc(doc(db, "userChats", data.user.uid), {
      [data.chatId + ".lastMessage"]:{
        text
      },
      [data.chatId+".date"]: serverTimestamp()
    })

    setText("")
    setImg(null)
  }, [text, img, currentUser.uid, data.chatId, data.user.uid]);

  useEffect(() => {
    const handleKeyDown = (event) => {
      if (event.keyCode === 13) { // keycode for enter
        event.preventDefault();
        handleSend();
      }
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
    };
  }, [text, img, handleSend]);
  const isChatIdNull = data.chatId === "null";

  return (
    <div className='input'>
      <input type='text' placeholder='Type something...' onChange={e => setText(e.target.value)} value={text} disabled={isChatIdNull}/>
      <div className='send'>
        {img === null ? <div className='attach'>
          <img src={Attach} alt="" disabled={isChatIdNull} />
          <input type="file" style={{display:"none"}} id='file' onChange={e => setImg(e.target.files[0])} disabled={isChatIdNull}/>
          <label htmlFor='file'>
            <img src={Img} alt="" />
          </label>
        </div> : <button onClick={() => setImg(null)}>X</button>}
        <button onClick={() => handleSend()} disabled={isChatIdNull}>Send</button>
        {err && <span>Message couldn't be sent!</span>}
      </div>
    </div>
  )
}

export default Input