import { useState, useEffect } from "react";
import { useAuthContext } from "../hooks/useAuthContext";
import { useNavigate } from "react-router-dom";
import { MessageCircle, User, Lock, Mail, Eye, EyeOff, ArrowRight } from "lucide-react";

const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    firstName: "",
    lastName: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login, register, isAuthenticated, initializing } = useAuthContext();
  const navigate = useNavigate();

  useEffect(() => {
    if (!initializing && isAuthenticated) {
      navigate("/");
    }
  }, [isAuthenticated, initializing, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      if (isLogin) {
        // Pass as object with username and password
        await login({
          username: formData.username,
          password: formData.password
        });
      } else {
        // Pass entire formData for registration
        await register({
          username: formData.username,
          email: formData.email,
          password: formData.password,
          firstName: formData.firstName,
          lastName: formData.lastName,
        });
      }
    } catch (err) {
      setError(err.message || "Authentication failed");
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  if (initializing) {
    return (
      <div style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '100vh',
        background: 'var(--bg-app)'
      }}>
        <div className="animate-spin" style={{
          width: '48px',
          height: '48px',
          border: '4px solid var(--bg-tertiary)',
          borderTopColor: 'var(--primary-500)',
          borderRadius: 'var(--radius-full)',
        }}></div>
      </div>
    );
  }

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'var(--bg-app)',
      padding: 'var(--space-4)',
    }}>
      <div style={{ width: '100%', maxWidth: '480px' }} className="animate-scale-in">
        {/* Logo & Title */}
        <div style={{ textAlign: 'center', marginBottom: 'var(--space-8)' }}>
          <div style={{
            width: '80px',
            height: '80px',
            margin: '0 auto var(--space-4)',
            background: 'linear-gradient(135deg, var(--primary-600), var(--primary-700))',
            borderRadius: 'var(--radius-lg)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 10px 30px rgba(107, 110, 245, 0.4)',
          }}>
            <MessageCircle size={40} style={{ color: 'white' }} />
          </div>

          <h1 className="font-bold text-3xl" style={{
            color: 'var(--text-primary)',
            marginBottom: 'var(--space-2)',
          }}>
            MaitriConnect
          </h1>

          <p style={{ fontSize: '14px', color: 'var(--text-tertiary)' }}>
            Connect, chat, and collaborate in real-time
          </p>
        </div>

        {/* Auth Card */}
        <div className="card" style={{
          background: 'var(--bg-primary)',
          borderRadius: 'var(--radius-xl)',
          padding: 'var(--space-8)',
          boxShadow: 'var(--shadow-xl)',
        }}>
          {/* Tabs */}
          <div style={{
            display: 'flex',
            gap: 'var(--space-2)',
            marginBottom: 'var(--space-6)',
            padding: 'var(--space-1)',
            background: 'var(--bg-secondary)',
            borderRadius: 'var(--radius-md)',
          }}>
            <button
              onClick={() => {
                setIsLogin(true);
                setError("");
              }}
              style={{
                flex: 1,
                padding: 'var(--space-2)',
                border: 'none',
                borderRadius: 'var(--radius-sm)',
                background: isLogin ? 'var(--bg-active)' : 'transparent',
                color: isLogin ? 'white' : 'var(--text-secondary)',
                fontWeight: 600,
                fontSize: '14px',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
            >
              Sign In
            </button>
            <button
              onClick={() => {
                setIsLogin(false);
                setError("");
              }}
              style={{
                flex: 1,
                padding: 'var(--space-2)',
                border: 'none',
                borderRadius: 'var(--radius-sm)',
                background: !isLogin ? 'var(--bg-active)' : 'transparent',
                color: !isLogin ? 'white' : 'var(--text-secondary)',
                fontWeight: 600,
                fontSize: '14px',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
            >
              Sign Up
            </button>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit}>
            {/* Username */}
            <div style={{ marginBottom: 'var(--space-4)' }}>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: 500,
                color: 'var(--text-secondary)',
                marginBottom: 'var(--space-2)',
              }}>
                Username
              </label>
              <div style={{ position: 'relative' }}>
                <User size={18} style={{
                  position: 'absolute',
                  left: 'var(--space-3)',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: 'var(--text-muted)',
                }} />
                <input
                  type="text"
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  placeholder="Enter username"
                  required
                  className="input"
                  style={{ paddingLeft: 'var(--space-10)' }}
                />
              </div>
            </div>

            {/* Email (for signup) */}
            {!isLogin && (
              <div style={{ marginBottom: 'var(--space-4)' }} className="animate-slide-up">
                <label style={{
                  display: 'block',
                  fontSize: '14px',
                  fontWeight: 500,
                  color: 'var(--text-secondary)',
                  marginBottom: 'var(--space-2)',
                }}>
                  Email
                </label>
                <div style={{ position: 'relative' }}>
                  <Mail size={18} style={{
                    position: 'absolute',
                    left: 'var(--space-3)',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    color: 'var(--text-muted)',
                  }} />
                  <input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="Enter email"
                    required={!isLogin}
                    className="input"
                    style={{ paddingLeft: 'var(--space-10)' }}
                  />
                </div>
              </div>
            )}

            {/* First & Last Name (for signup) */}
            {!isLogin && (
              <div style={{ display: 'flex', gap: 'var(--space-3)', marginBottom: 'var(--space-4)' }} className="animate-slide-up">
                <div style={{ flex: 1 }}>
                  <label style={{
                    display: 'block',
                    fontSize: '14px',
                    fontWeight: 500,
                    color: 'var(--text-secondary)',
                    marginBottom: 'var(--space-2)',
                  }}>
                    First Name
                  </label>
                  <input
                    type="text"
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleChange}
                    placeholder="First name"
                    className="input"
                  />
                </div>
                <div style={{ flex: 1 }}>
                  <label style={{
                    display: 'block',
                    fontSize: '14px',
                    fontWeight: 500,
                    color: 'var(--text-secondary)',
                    marginBottom: 'var(--space-2)',
                  }}>
                    Last Name
                  </label>
                  <input
                    type="text"
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleChange}
                    placeholder="Last name"
                    className="input"
                  />
                </div>
              </div>
            )}

            {/* Password */}
            <div style={{ marginBottom: 'var(--space-6)' }}>
              <label style={{
                display: 'block',
                fontSize: '14px',
                fontWeight: 500,
                color: 'var(--text-secondary)',
                marginBottom: 'var(--space-2)',
              }}>
                Password
              </label>
              <div style={{ position: 'relative' }}>
                <Lock size={18} style={{
                  position: 'absolute',
                  left: 'var(--space-3)',
                  top: '50%',
                  transform: 'translateY(-50%)',
                  color: 'var(--text-muted)',
                }} />
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Enter password"
                  required
                  className="input"
                  style={{ paddingLeft: 'var(--space-10)', paddingRight: 'var(--space-10)' }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{
                    position: 'absolute',
                    right: 'var(--space-3)',
                    top: '50%',
                    transform: 'translateY(-50%)',
                    background: 'transparent',
                    border: 'none',
                    cursor: 'pointer',
                    padding: 'var(--space-1)',
                    color: 'var(--text-muted)',
                  }}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            {/* Error Message */}
            {error && (
              <div className="animate-slide-up" style={{
                padding: 'var(--space-3)',
                background: 'rgba(239, 68, 68, 0.1)',
                border: '1px solid rgba(239, 68, 68, 0.3)',
                borderRadius: 'var(--radius-md)',
                marginBottom: 'var(--space-4)',
              }}>
                <p style={{ fontSize: '14px', color: 'var(--accent-red)', margin: 0 }}>
                  {error}
                </p>
              </div>
            )}

            {/* Submit Button */}
            <button
              type="submit"
              disabled={loading}
              className="btn-primary"
              style={{
                width: '100%',
                padding: 'var(--space-3)',
                fontSize: '15px',
                fontWeight: 600,
                opacity: loading ? 0.7 : 1,
                cursor: loading ? 'not-allowed' : 'pointer',
              }}
            >
              {loading ? (
                <span className="flex items-center justify-center" style={{ gap: 'var(--space-2)' }}>
                  <div className="animate-spin" style={{
                    width: '16px',
                    height: '16px',
                    border: '2px solid rgba(255,255,255,0.3)',
                    borderTopColor: 'white',
                    borderRadius: 'var(--radius-full)',
                  }}></div>
                  Processing...
                </span>
              ) : (
                <span className="flex items-center justify-center" style={{ gap: 'var(--space-2)' }}>
                  {isLogin ? "Sign In" : "Create Account"}
                  <ArrowRight size={18} />
                </span>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;
