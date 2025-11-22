import { useState, useEffect } from "react";
import { FileText, Download, Image as ImageIcon } from "lucide-react";
import axios from "axios";
import { useAuthContext } from "../hooks/useAuthContext";

export default function FilePreview({ fileId, fileData }) {
    const [objectUrl, setObjectUrl] = useState(null);
    const [loading, setLoading] = useState(false);
    const { user } = useAuthContext();
    const token = localStorage.getItem("authToken");

    useEffect(() => {
        if (!fileId || !token) return;

        const fetchFile = async () => {
            // Only auto-fetch images for preview
            const isImage = fileData?.fileType?.startsWith('image/');
            if (!isImage) return;

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
                console.error("Failed to load image preview:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchFile();

        return () => {
            if (objectUrl) URL.revokeObjectURL(objectUrl);
        };
    }, [fileId, token, fileData]);

    const handleDownload = async (e) => {
        e.preventDefault();
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
            URL.revokeObjectURL(url);
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
        <div style={{
            borderRadius: 'var(--radius-md)',
            overflow: 'hidden',
            maxWidth: '300px',
        }}>
            {isImage ? (
                <div style={{ position: 'relative', minHeight: '100px', background: 'var(--bg-tertiary)' }}>
                    {loading && (
                        <div style={{
                            position: 'absolute',
                            inset: 0,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                        }}>
                            <div className="animate-spin" style={{
                                width: '20px',
                                height: '20px',
                                border: '2px solid var(--text-muted)',
                                borderTopColor: 'transparent',
                                borderRadius: '50%'
                            }}></div>
                        </div>
                    )}
                    {objectUrl && (
                        <img
                            src={objectUrl}
                            alt={fileData.originalFileName}
                            style={{
                                width: '100%',
                                height: 'auto',
                                display: 'block',
                            }}
                        />
                    )}
                </div>
            ) : (
                <div style={{
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
                    <button
                        onClick={handleDownload}
                        className="btn-ghost"
                        style={{ padding: 'var(--space-2)', color: 'var(--text-secondary)' }}
                        title="Download"
                    >
                        <Download size={18} />
                    </button>
                </div>
            )}
        </div>
    );
}
