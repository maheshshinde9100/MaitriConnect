import React from "react";

const Navbar = ({ setChatOpen }) => {
  return (
    <>
      <div className="navbar-container" style={{display:"flex",width:"100px"}}>
        <nav>
          <ul>
            <li
              onClick={() => {
                setChatOpen(false);
              }}
            >
              Home
            </li>
            <li
              onClick={() => {
                setChatOpen(true);
              }}
            >
              Chat Room
            </li>
          </ul>
        </nav>
      </div>
    </>
  );
};

export default Navbar;
