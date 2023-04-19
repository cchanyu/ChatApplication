import React, { useContext, useEffect, useRef } from 'react'
import { AuthContext } from '../context/AuthContext'
import { ChatContext } from '../context/ChatContext'

const Message = ({message}) => {

  const {currentUser} = useContext(AuthContext);
  const {data} = useContext(ChatContext);
  const ref = useRef();

  const messageDate = message.date.toDate();
  const currentDate = new Date();
  const isWithinLast24Hours = (currentDate - messageDate) < (24 * 60 * 60 * 1000); // milliseconds in 24 hours

  useEffect (() => {
    ref.current?.scrollIntoView({behavior:"smooth"})
  }, [message])

  return (
    <div ref={ref} className={`message ${message.senderId === currentUser.uid && "owner"}`}>
      <div className="messageInfo">
        <img src={message.senderId === currentUser.uid ? currentUser.photoURL : data.user.photoURL} alt="user" />
        <span>{isWithinLast24Hours 
          ? messageDate.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }) 
          : messageDate.toLocaleDateString('en-US')}
        </span>
      </div>
      <div className='messageWrapper'>
        <span>{message.senderId === currentUser.uid ? currentUser.displayName : data.user.displayName}</span>
        <div className="messageContent">
          <p>{message.text}</p>
          {message.img && <img src={message.img} alt="img" />}
        </div>
      </div>
    </div>
  )
}

export default Message