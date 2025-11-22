import { useState, useEffect } from "react";
import { FileText, Download, Eye, X } from "lucide-react";
import axios from "axios";
import { useAuthContext } from "../hooks/useAuthContext";

export default function FilePreview({ fileId, fileData }) {
    const [objectUrl, setObjectUrl] = useState(null);
    const [loading, setLoading] = useState(false);
    const [showPreview, setShowPreview] = useState(false);
    const { user } = useAuthContext();
    const token = localStorage.getItem("authToken");

    // Cleanup object URL on unmount
    useEffect(() => {
        return () => {
            if (objectUrl) URL.revokeObjectURL(objectUrl);
        };
    }, [objectUrl]);

    const fetchFile = async () => {
        if (!fileId || !token || objectUrl) return;

        try {
            setLoading(true);
            const response = await axios.get(
                `http://localhost:8080/api/chat/files/download/${fileId}`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                    responseType: 'blob'
                }
            );
            const url = URL.createObjectURL(response.data);
            setObjectUrl(url);
        } catch (error) {
            console.error("Failed to load file:", error);
            alert("Failed to load file");
        } finally {
            setLoading(false);
        }
    };

    const handlePreview = async () => {
        setShowPreview(true);
        await fetchFile();
    };

    const handleDownload = async (e) => {
        e.preventDefault();
        // If we already have the blob, use it
        if (objectUrl) {
            const link = document.createElement('a');
            link.href = objectUrl;
            link.download = fileData?.originalFileName || 'download';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            return;
        }

        // Otherwise fetch it
        try {
            const response = await axios.get(
                `http://localhost:8080/api/chat/files/download/${fileId}`,
                {
                    headers: { Authorization: `Bearer ${token}` },
                    responseType: 'blob'
                }
            );

            const url = URL.createObjectURL(response.data);
            const link = document.createElement('a');
            link.href = url;
            link.download = fileData?.originalFileName || 'download';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(url); // Clean up the temporary URL
        } catch (error) {
            console.error("Download failed:", error);
            alert("Failed to download file");
        }
    };

    if (!fileData) {
        return (
            <div style={{
                padding: 'var(--space-3)',
                background: 'var(--bg-tertiary)',
                borderRadius: 'var(--radius-md)',
                display: 'flex',
                alignItems: 'center',
                gap: 'var(--space-2)',
            }}>
                <FileText size={20} />
                <span style={{ fontSize: '14px' }}>Loading file...</span>
            </div>
        );
    }

    const isImage = fileData.fileType?.startsWith('image/');

    return (
        <>
            <div style={{
                borderRadius: 'var(--radius-md)',
                overflow: 'hidden',
                maxWidth: '300px',
                padding: 'var(--space-3)',
                background: 'var(--bg-tertiary)',
                border: '1px solid var(--border-primary)',
                display: 'flex',
                alignItems: 'center',
                gap: 'var(--space-3)',
            }}>
                <FileText size={24} style={{ color: 'var(--primary-500)' }} />
                <div style={{ flex: 1 }}>
                    <p className="font-medium truncate" style={{ fontSize: '14px' }}>
                        {fileData.originalFileName}
                    </p>
                    <p style={{ fontSize: '12px', color: 'var(--text-tertiary)' }}>
                        {(fileData.fileSize / 1024).toFixed(1)} KB
                    </p>
                </div>

                {isImage && (
                    <button
                        onClick={handlePreview}
                        className="btn-ghost"
                        style={{ padding: 'var(--space-2)', color: 'var(--text-secondary)' }}
                        title="Preview"
                    >
                        <Eye size={18} />
                    </button>
                )}

                <button
                    onClick={handleDownload}
                    className="btn-ghost"
                    style={{ padding: 'var(--space-2)', color: 'var(--text-secondary)' }}
                    title="Download"
                >
                    <Download size={18} />
                </button>
            </div>

            {/* Image Preview Modal */}
            {showPreview && (
                <div style={{
                    position: 'fixed',
                    inset: 0,
                    zIndex: 100,
                    background: 'rgba(0,0,0,0.8)',
                    backdropFilter: 'blur(4px)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    padding: 'var(--space-4)',
                }} onClick={() => setShowPreview(false)}>
                    <div style={{
                        position: 'relative',
                        maxWidth: '90vw',
                        maxHeight: '90vh',
                    }} onClick={e => e.stopPropagation()}>
                        <button
                            onClick={() => setShowPreview(false)}
                            style={{
                                position: 'absolute',
                                top: '-40px',
                                right: 0,
                                background: 'transparent',
                                border: 'none',
                                color: 'white',
                                cursor: 'pointer',
                            }}
                        >
                            <X size={24} />
                        </button>

                        {loading ? (
                            <div className="animate-spin" style={{
                                width: '40px',
                                height: '40px',
                                border: '4px solid rgba(255,255,255,0.3)',
                                borderTopColor: 'white',
                                borderRadius: '50%'
                            }}></div>
                        ) : (
                            <img
                                src={objectUrl}
                                alt={fileData.originalFileName}
                                style={{
                                    maxWidth: '100%',
                                    maxHeight: '90vh',
                                    borderRadius: 'var(--radius-lg)',
                                    boxShadow: 'var(--shadow-2xl)',
                                }}
                            />
                        )}
                    </div>
                </div>
            )}
        </>
    );
}
