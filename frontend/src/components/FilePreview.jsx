import { FileText, Download, Image as ImageIcon } from "lucide-react";

export default function FilePreview({ fileId, fileData }) {
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
                <img
                    src={`http://localhost:8080/api/chat/files/download/${fileId}`}
                    alt={fileData.originalFileName}
                    style={{
                        width: '100%',
                        height: 'auto',
                        display: 'block',
                    }}
                />
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
                    <a
                        href={`http://localhost:8080/api/chat/files/download/${fileId}`}
                        download
                        style={{ padding: 'var(--space-2)', color: 'var(--text-secondary)' }}
                    >
                        <Download size={18} />
                    </a>
                </div>
            )}
        </div>
    );
}
