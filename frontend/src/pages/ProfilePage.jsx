import { useState, useEffect } from "react";
import { useAuthContext } from "../hooks/useAuthContext";
import { ArrowLeft, Camera, Save, User, Loader } from "lucide-react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const API_BASE = "http://localhost:8080";

export default function ProfilePage() {
    const { user, token, setUser } = useAuthContext();
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        status: "",
        profilePicture: ""
    });
    const [loading, setLoading] = useState(false);
    const [uploading, setUploading] = useState(false);
    const [message, setMessage] = useState({ type: "", text: "" });

    useEffect(() => {
        if (user) {
            setFormData({
                firstName: user.firstName || "",
                lastName: user.lastName || "",
                status: user.status || "",
                profilePicture: user.profilePicture || ""
            });
        }
    }, [user]);

    const handleImageUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            setMessage({ type: "error", text: "Please select an image file" });
            return;
        }

        try {
            setUploading(true);
            const formData = new FormData();
            formData.append("file", file);

            // Upload to chat-service to get file ID
            const uploadRes = await axios.post(
                `${API_BASE}/api/chat/files/upload`,
                formData,
                {
                    headers: {
                        "Content-Type": "multipart/form-data",
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            const fileId = uploadRes.data.id;
            const fileUrl = `${API_BASE}/api/chat/files/download/${fileId}`;

            setFormData(prev => ({ ...prev, profilePicture: fileUrl }));
            setMessage({ type: "success", text: "Image uploaded successfully" });
        } catch (error) {
            console.error("Upload failed:", error);
            setMessage({ type: "error", text: "Failed to upload image" });
        } finally {
            setUploading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setMessage({ type: "", text: "" });

        try {
            const res = await axios.put(
                `${API_BASE}/api/auth/user/${user.userId}/profile`,
                formData,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            // Update local user context
            const updatedUser = {
                ...user,
                firstName: res.data.firstName,
                lastName: res.data.lastName,
                status: res.data.status,
                profilePicture: res.data.profilePicture
            };

            // Update context and local storage
            setUser(updatedUser);
            localStorage.setItem("user", JSON.stringify(updatedUser));

            setMessage({ type: "success", text: "Profile updated successfully!" });
        } catch (error) {
            console.error("Update failed:", error);
            setMessage({ type: "error", text: "Failed to update profile" });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="layout-container" style={{ background: 'var(--bg-primary)' }}>
            <div style={{
                maxWidth: '600px',
                margin: '0 auto',
                padding: 'var(--space-6)',
                width: '100%'
            }}>
                {/* Header */}
                <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: 'var(--space-4)',
                    marginBottom: 'var(--space-8)'
                }}>
                    <button
                        onClick={() => navigate('/')}
                        className="btn-ghost"
                        style={{ padding: 'var(--space-2)' }}
                    >
                        <ArrowLeft size={24} />
                    </button>
                    <h1 style={{ fontSize: '24px', fontWeight: '600' }}>Edit Profile</h1>
                </div>

                {/* Profile Image */}
                <div style={{
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    marginBottom: 'var(--space-8)'
                }}>
                    <div style={{ position: 'relative' }}>
                        <div style={{
                            width: '120px',
                            height: '120px',
                            borderRadius: '50%',
                            overflow: 'hidden',
                            background: 'var(--bg-tertiary)',
                            border: '4px solid var(--bg-secondary)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                        }}>
                            {formData.profilePicture ? (
                                <img
                                    src={formData.profilePicture}
                                    alt="Profile"
                                    style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                                />
                            ) : (
                                <User size={48} style={{ color: 'var(--text-tertiary)' }} />
                            )}
                        </div>

                        <label style={{
                            position: 'absolute',
                            bottom: '0',
                            right: '0',
                            background: 'var(--primary-500)',
                            color: 'white',
                            padding: 'var(--space-2)',
                            borderRadius: '50%',
                            cursor: 'pointer',
                            boxShadow: 'var(--shadow-lg)',
                            transition: 'transform 0.2s'
                        }}
                            onMouseOver={e => e.currentTarget.style.transform = 'scale(1.1)'}
                            onMouseOut={e => e.currentTarget.style.transform = 'scale(1)'}
                        >
                            {uploading ? (
                                <Loader size={20} className="animate-spin" />
                            ) : (
                                <Camera size={20} />
                            )}
                            <input
                                type="file"
                                accept="image/*"
                                onChange={handleImageUpload}
                                style={{ display: 'none' }}
                                disabled={uploading}
                            />
                        </label>
                    </div>
                    <p style={{
                        marginTop: 'var(--space-3)',
                        color: 'var(--text-secondary)',
                        fontSize: '14px'
                    }}>
                        Click the camera icon to update your photo
                    </p>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-5)' }}>
                    {message.text && (
                        <div style={{
                            padding: 'var(--space-3)',
                            borderRadius: 'var(--radius-md)',
                            background: message.type === 'error' ? 'rgba(239, 68, 68, 0.1)' : 'rgba(34, 197, 94, 0.1)',
                            color: message.type === 'error' ? '#ef4444' : '#22c55e',
                            fontSize: '14px',
                            textAlign: 'center'
                        }}>
                            {message.text}
                        </div>
                    )}

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
                        <div className="form-group">
                            <label style={{ display: 'block', marginBottom: 'var(--space-2)', fontSize: '14px', fontWeight: '500' }}>
                                First Name
                            </label>
                            <input
                                type="text"
                                className="input"
                                value={formData.firstName}
                                onChange={e => setFormData({ ...formData, firstName: e.target.value })}
                                placeholder="first name"
                            />
                        </div>
                        <div className="form-group">
                            <label style={{ display: 'block', marginBottom: 'var(--space-2)', fontSize: '14px', fontWeight: '500' }}>
                                Last Name
                            </label>
                            <input
                                type="text"
                                className="input"
                                value={formData.lastName}
                                onChange={e => setFormData({ ...formData, lastName: e.target.value })}
                                placeholder="last name"
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label style={{ display: 'block', marginBottom: 'var(--space-2)', fontSize: '14px', fontWeight: '500' }}>
                            Bio / Status
                        </label>
                        <textarea
                            className="input"
                            value={formData.status}
                            onChange={e => setFormData({ ...formData, status: e.target.value })}
                            placeholder="Tell us about yourself..."
                            rows={3}
                            style={{ resize: 'none' }}
                        />
                    </div>

                    <button
                        type="submit"
                        className="btn-primary"
                        disabled={loading || uploading}
                        style={{
                            marginTop: 'var(--space-2)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            gap: 'var(--space-2)'
                        }}
                    >
                        {loading ? (
                            <>
                                <Loader size={20} className="animate-spin" />
                                Saving...
                            </>
                        ) : (
                            <>
                                <Save size={20} />
                                Save Changes
                            </>
                        )}
                    </button>
                </form>
            </div>
        </div>
    );
}
