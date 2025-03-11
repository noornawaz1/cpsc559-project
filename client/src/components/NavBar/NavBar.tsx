import { useNavigate } from "react-router-dom";
import styles from "./NavBar.module.scss";
import api from "../../services/api";
import { useEffect, useState } from "react";

function NavBar() {
  const navigate = useNavigate();
  const [username, setUsername] = useState<string | null>(null);

  useEffect(() => {
    const fetchCurrentUser = async () => {
      try {
        const res = await api.get("api/auth/me", {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        setUsername(res.data.username);
      } catch (err) {
        console.error("Failed to fetch user:", err);
      }
    };

    fetchCurrentUser();
  }, []);
  const handleLogout = async () => {
    localStorage.removeItem("token");
    alert("Logged out successfully");
    navigate("/");
  };

  return (
    <div className={styles.navbar}>
      <span className={styles.username}>{username}</span>
      <button onClick={handleLogout} className={styles.logoutBtn}>
        Logout
      </button>
    </div>
  );
}

export default NavBar;
