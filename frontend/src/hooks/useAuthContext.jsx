import { createContext, useContext, useState, useEffect } from "react";
import axios from "axios";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [authLoading, setAuthLoading] = useState(false);
  const [error, setError] = useState("");
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [initializing, setInitializing] = useState(true);
  const [user, setUser] = useState(null);

  useEffect(() => {
    // Check initial localStorage for persisted session
    const token = localStorage.getItem("authToken");
    const username = localStorage.getItem("currentUser");
    const userId = localStorage.getItem("currentUserId");
    if (token && username && userId) {
      setUser({ token, username, userId });
      setIsAuthenticated(true);
    }
    setInitializing(false);
  }, []);

  const login = async ({ username, password }) => {
    setAuthLoading(true);
    setError("");
    try {
      const res = await axios.post("http://localhost:8080/api/auth/login", { username, password });
      const { token, userId, username: name } = res.data;
      setUser({ token, userId, username: name });
      setIsAuthenticated(true);
      // Persist credentials
      localStorage.setItem("authToken", token);
      localStorage.setItem("currentUser", name);
      localStorage.setItem("currentUserId", userId);
    } catch (err) {
      setError(err?.response?.data?.error || "Login failed");
      throw err;
    } finally {
      setAuthLoading(false);
    }
  };

  const register = async (payload) => {
    setAuthLoading(true);
    setError("");
    try {
      const res = await axios.post("http://localhost:8080/api/auth/register", payload);
      const { token, userId, username: name } = res.data;
      setUser({ token, userId, username: name });
      setIsAuthenticated(true);
      localStorage.setItem("authToken", token);
      localStorage.setItem("currentUser", name);
      localStorage.setItem("currentUserId", userId);
    } catch (err) {
      setError(err?.response?.data?.error || "Registration failed");
      throw err;
    } finally {
      setAuthLoading(false);
    }
  };

  const logout = () => {
    localStorage.clear();
    setIsAuthenticated(false);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        login,
        register,
        logout,
        authLoading,
        error,
        isAuthenticated,
        user,
        initializing
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuthContext = () => useContext(AuthContext);
