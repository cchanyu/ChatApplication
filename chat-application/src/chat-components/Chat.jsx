import React, { useContext } from 'react'
import Cam from '../img/cam.png'
import Add from '../img/add.png'
import More from '../img/more.png'
import Messages from './Messages'
import Input from './Input'
import { ChatContext } from '../context/ChatContext'

const Chat = () => {
  const { data } = useContext(ChatContext);

  return (
    <div className='chat'>
      <div className="chatInfo">
        <div className='chatUser'>
          <img src={data.user?.photoURL} alt="profile" />
          <span>{data.user?.displayName}</span>
        </div>
        <div className="chatIcons">
          <img src={Cam} alt="c" />
          <img src={Add} alt="a" />
          <img src={More} alt="m" />
        </div>
      </div>
      <Messages />
      <Input />
    </div>
  )
}

export default Chat