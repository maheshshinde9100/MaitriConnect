import { useState } from 'react';
import { Download, File, X, ZoomIn, FileText } from 'lucide-react';
import { fileService } from '../services/fileService';
import { motion, AnimatePresence } from 'framer-motion';

export default function FilePreview({ file, onClose }) {
    const [showLightbox, setShowLightbox] = useState(false);
    const isImage = fileService.isImageFile(file.fileType);
    const downloadUrl = fileService.getDownloadUrl(file.fileId);

    const handleDownload = () => {
        window.open(downloadUrl, '_blank');
    };

    return (
        <>
            <div className="file-preview rounded-xl overflow-hidden max-w-xs">
                {isImage ? (
                    <div className="relative group">
                        <img
                            src={downloadUrl}
                            alt={file.fileName}
                            className="w-full h-auto rounded-lg cursor-pointer"
                            onClick={() => setShowLightbox(true)}
                            style={{ maxHeight: '300px', objectFit: 'cover' }}
                        />
                        <div className="absolute inset-0 bg-black bg-opacity-0 group-hover:bg-opacity-30 transition-all flex items-center justify-center">
                            <button
                                onClick={() => setShowLightbox(true)}
                                className="opacity-0 group-hover:opacity-100 p-3 rounded-full transition-all"
                                style={{ background: 'rgba(0,0,0,0.5)' }}
                            >
                                <ZoomIn className="w-5 h-5 text-white" />
                            </button>
                        </div>
                    </div>
                ) : (
                    <div
                        className="flex items-center gap-3 p-3 rounded-lg"
                        style={{ background: 'var(--bg-hover)' }}
                    >
                        <div
                            className="w-12 h-12 rounded-lg flex items-center justify-center flex-shrink-0"
                            style={{ background: 'var(--bg-tertiary)' }}
                        >
                            <FileText className="w-6 h-6" style={{ color: 'var(--text-secondary)' }} />
                        </div>
                        <div className="flex-1 min-w-0">
                            <p className="text-sm font-medium truncate" style={{ color: 'var(--text-primary)' }}>
                                {file.fileName}
                            </p>
                            <p className="text-xs" style={{ color: 'var(--text-tertiary)' }}>
                                {fileService.formatFileSize(file.fileSize)}
                            </p>
                        </div>
                    </div>
                )}

                <button
                    onClick={handleDownload}
                    className="w-full mt-2 flex items-center justify-center gap-2 py-2 rounded-lg text-sm font-medium hover-lift"
                    style={{
                        background: 'var(--bg-hover)',
                        color: 'var(--text-secondary)',
                    }}
                >
                    <Download className="w-4 h-4" />
                    Download
                </button>
            </div>

            {/* Lightbox for images */}
            <AnimatePresence>
                {showLightbox && isImage && (
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        className="fixed inset-0 bg-black bg-opacity-95 z-50 flex items-center justify-center p-4"
                        onClick={() => setShowLightbox(false)}
                    >
                        <button
                            onClick={() => setShowLightbox(false)}
                            className="absolute top-4 right-4 p-3 rounded-full hover-lift"
                            style={{ background: 'rgba(255,255,255,0.1)' }}
                        >
                            <X className="w-6 h-6 text-white" />
                        </button>
                        <motion.img
                            initial={{ scale: 0.9 }}
                            animate={{ scale: 1 }}
                            src={downloadUrl}
                            alt={file.fileName}
                            className="max-w-full max-h-full object-contain rounded-lg"
                            onClick={(e) => e.stopPropagation()}
                        />
                    </motion.div>
                )}
            </AnimatePresence>
        </>
    );
}
