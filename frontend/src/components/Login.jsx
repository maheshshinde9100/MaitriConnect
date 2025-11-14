import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Eye, EyeOff, LogIn, UserPlus } from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { validateEmail, validateUsername, validatePassword } from '../utils/helpers';
import { SUCCESS_MESSAGES } from '../constants';
import toast from 'react-hot-toast';

const Login = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: ''
  });

  const { login, register, loading, error } = useAuth();

  const validateForm = () => {
    if (!validateUsername(formData.username)) {
      toast.error('Username must be 3-20 characters, alphanumeric and underscores only');
      return false;
    }
    
    if (!validatePassword(formData.password)) {
      toast.error('Password must be at least 6 characters');
      return false;
    }
    
    if (!isLogin) {
      if (!validateEmail(formData.email)) {
        toast.error('Please enter a valid email address');
        return false;
      }
      
      if (!formData.firstName.trim() || !formData.lastName.trim()) {
        toast.error('First name and last name are required');
        return false;
      }
    }
    
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    try {
      if (isLogin) {
        await login({
          username: formData.username,
          password: formData.password
        });
        toast.success(SUCCESS_MESSAGES.LOGIN_SUCCESS);
      } else {
        await register(formData);
        toast.success(SUCCESS_MESSAGES.REGISTER_SUCCESS);
      }
    } catch (err) {
      toast.error(err.message);
    }
  };

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md"
      >
        <div className="card">
          <div className="card-body">
            <div className="text-center mb-8">
              <motion.h1
                initial={{ scale: 0.9 }}
                animate={{ scale: 1 }}
                transition={{ delay: 0.2 }}
                className="text-3xl font-bold text-accent mb-2"
              >
                🚀 MaitriConnect
              </motion.h1>
              <p className="text-secondary">
                {isLogin ? 'Welcome back!' : 'Create your account'}
              </p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="form-group">
                <label className="form-label">Username</label>
                <input
                  type="text"
                  name="username"
                  value={formData.username}
                  onChange={handleInputChange}
                  className="form-input"
                  placeholder="Enter your username"
                  required
                />
              </div>

              {!isLogin && (
                <>
                  <motion.div
                    initial={{ opacity: 0, height: 0 }}
                    animate={{ opacity: 1, height: 'auto' }}
                    exit={{ opacity: 0, height: 0 }}
                    className="form-group"
                  >
                    <label className="form-label">Email</label>
                    <input
                      type="email"
                      name="email"
                      value={formData.email}
                      onChange={handleInputChange}
                      className="form-input"
                      placeholder="Enter your email"
                      required
                    />
                  </motion.div>

                  <div className="flex gap-4">
                    <motion.div
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                      className="form-group flex-1"
                    >
                      <label className="form-label">First Name</label>
                      <input
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleInputChange}
                        className="form-input"
                        placeholder="First name"
                        required
                      />
                    </motion.div>

                    <motion.div
                      initial={{ opacity: 0, x: 20 }}
                      animate={{ opacity: 1, x: 0 }}
                      className="form-group flex-1"
                    >
                      <label className="form-label">Last Name</label>
                      <input
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleInputChange}
                        className="form-input"
                        placeholder="Last name"
                        required
                      />
                    </motion.div>
                  </div>
                </>
              )}

              <div className="form-group">
                <label className="form-label">Password</label>
                <div className="relative">
                  <input
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    value={formData.password}
                    onChange={handleInputChange}
                    className="form-input pr-12"
                    placeholder="Enter your password"
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 transform -translate-y-1/2 text-muted hover:text-primary transition-colors"
                  >
                    {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
                  </button>
                </div>
              </div>

              {error && (
                <motion.div
                  initial={{ opacity: 0, scale: 0.95 }}
                  animate={{ opacity: 1, scale: 1 }}
                  className="p-3 bg-error-light border border-error rounded-md"
                >
                  <p className="text-error text-sm">{error}</p>
                </motion.div>
              )}

              <button
                type="submit"
                disabled={loading}
                className="btn btn-primary w-full"
              >
                {loading ? (
                  <div className="spinner" />
                ) : (
                  <>
                    {isLogin ? <LogIn size={20} /> : <UserPlus size={20} />}
                    {isLogin ? 'Sign In' : 'Create Account'}
                  </>
                )}
              </button>
            </form>

            <div className="mt-6 text-center">
              <p className="text-secondary">
                {isLogin ? "Don't have an account?" : 'Already have an account?'}
                <button
                  type="button"
                  onClick={() => {
                    setIsLogin(!isLogin);
                    setFormData({
                      username: '',
                      email: '',
                      password: '',
                      firstName: '',
                      lastName: ''
                    });
                  }}
                  className="ml-2 text-accent hover:text-accent-hover font-medium"
                >
                  {isLogin ? 'Sign up' : 'Sign in'}
                </button>
              </p>
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
};

export default Login;