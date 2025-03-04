import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./Home.module.scss";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faPencil, faTrash } from "@fortawesome/free-solid-svg-icons";

// TODO: Remove this mock data and replace with api calls
// mock Data for lists and users
const mockLists = [
  { id: "1", name: "Shopping List", author_id: "101" },
  { id: "2", name: "Work Tasks", author_id: "102" },
  { id: "3", name: "Personal Goals", author_id: "103" },
];

const mockUsers = [
  { id: "101", username: "alice" },
  { id: "102", username: "bob" },
  { id: "103", username: "charlie" },
];

const getUsername = (author_id: string) => {
  const user = mockUsers.find((user) => user.id === author_id);
  return user ? user.username : "Unknown User";
};

function Home() {
  const [lists, setLists] = useState(mockLists);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  return (
    <div className={styles.homePage}>
      <div className={styles.navbar}>
        <span className={styles.username}>username</span>
        <button onClick={handleLogout} className={styles.logoutBtn}>
          Logout
        </button>
      </div>

      <div className={styles.mainContent}>
        <h2 className={styles.title}>All Lists</h2>

        <div className={styles.listContainer}>
          {lists.map((list) => (
            <div key={list.id} className={styles.listItem}>
              <span>
                {list.name} <em>(@{getUsername(list.author_id)})</em>
              </span>
              <div>
                <button className={styles.iconButton}>
                  <FontAwesomeIcon icon={faPencil} />
                </button>
                <button className={styles.deleteButton}>
                  <FontAwesomeIcon icon={faTrash} />
                </button>
              </div>
            </div>
          ))}
        </div>

        <button className={styles.addBtn}>
          <FontAwesomeIcon icon={faPlus} className={styles.iconSpacing} />
          Create a List
        </button>
      </div>
    </div>
  );
}

export default Home;
