import axios from "axios";

const api = axios.create({
  baseURL: "/api", 
});

// Endpoints that don't need auth
const authWhitelist = ["/login", "/register"];

// Include token in every request except whitelisted endpoints
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token"); // Retrieve token

    const isWhitelisted = authWhitelist.some((endpoint) =>
      config.url?.includes(endpoint)
    );

    if (token && !isWhitelisted) {
      config.headers.Authorization = `Bearer ${token}`; // Add to auth header
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default api;
