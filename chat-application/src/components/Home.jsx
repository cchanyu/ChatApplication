import React, { useRef } from 'react';
import Sidebar from '../chat-components/Sidebar'
import Chat from '../chat-components/Chat'
import DraggableCore from 'react-draggable';
import { Resizable } from 'react-resizable';
import '../css/Home.scss';

const Home = () => {
  const draggableRef = useRef(null);

  return (
    <div className='home'>
      <DraggableCore axis="both" bounds="parent" nodeRef={draggableRef}>
        <Resizable 
          width={1000}
          height={700}
          maxConstraints={[window.innerWidth, window.innerHeight]}>
          <div ref={draggableRef} className="home-container">
            <Sidebar />
            <Chat />
          </div>
        </Resizable>
      </DraggableCore>
    </div>
  )
}

export default Home