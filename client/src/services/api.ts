import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api", // should set to actual backend URL (and then proxy when ready)
});

// Don't need auth header
const authWhitelist = ["/login", "/register"];

// Include token in every request
api.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem("token"); // Retrieve token

      const isWhitelisted = authWhitelist.some(endpoint => config.url?.includes(endpoint));

      if (token && !isWhitelisted) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
);

export default api;
