import { useState, useRef } from 'react';
import { Upload, X, File, Image } from 'lucide-react';
import { fileService } from '../services/fileService';

export default function FileUpload({ onFileSelect, onUploadComplete, chatRoomId, userId }) {
    const [selectedFile, setSelectedFile] = useState(null);
    const [preview, setPreview] = useState(null);
    const [uploading, setUploading] = useState(false);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [dragActive, setDragActive] = useState(false);
    const fileInputRef = useRef(null);

    const handleFileChange = (e) => {
        const file = e.target.files?.[0];
        if (file) {
            processFile(file);
        }
    };

    const processFile = (file) => {
        // Validate file size (25MB max)
        if (file.size > 25 * 1024 * 1024) {
            alert('File size exceeds 25MB limit');
            return;
        }

        setSelectedFile(file);

        // Create preview for images
        if (file.type.startsWith('image/')) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreview(reader.result);
            };
            reader.readAsDataURL(file);
        } else {
            setPreview(null);
        }

        if (onFileSelect) {
            onFileSelect(file);
        }
    };

    const handleUpload = async () => {
        if (!selectedFile || !userId || !chatRoomId) return;

        setUploading(true);
        try {
            const response = await fileService.uploadFile(
                selectedFile,
                userId,
                chatRoomId,
                (progress) => setUploadProgress(progress)
            );

            if (onUploadComplete) {
                onUploadComplete(response);
            }

            // Reset
            setSelectedFile(null);
            setPreview(null);
            setUploadProgress(0);
        } catch (error) {
            console.error('Upload failed:', error);
            alert('Failed to upload file');
        } finally {
            setUploading(false);
        }
    };

    const handleDrag = (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (e.type === 'dragenter' || e.type === 'dragover') {
            setDragActive(true);
        } else if (e.type === 'dragleave') {
            setDragActive(false);
        }
    };

    const handleDrop = (e) => {
        e.preventDefault();
        e.stopPropagation();
        setDragActive(false);

        const file = e.dataTransfer.files?.[0];
        if (file) {
            processFile(file);
        }
    };

    const clearFile = () => {
        setSelectedFile(null);
        setPreview(null);
        setUploadProgress(0);
        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
    };

    return (
        <div className="file-upload-container">
            {!selectedFile ? (
                <div
                    className={`border-2 border-dashed rounded-2xl p-6 text-center cursor-pointer transition-all ${dragActive
                            ? 'border-indigo-500 bg-indigo-500/10'
                            : 'border-white/20 hover:border-white/40 bg-white/5'
                        }`}
                    onDragEnter={handleDrag}
                    onDragLeave={handleDrag}
                    onDragOver={handleDrag}
                    onDrop={handleDrop}
                    onClick={() => fileInputRef.current?.click()}
                >
                    <Upload className="w-12 h-12 mx-auto mb-3 text-white/60" />
                    <p className="text-white/80 mb-1">Drop files here or click to browse</p>
                    <p className="text-xs text-white/50">Max file size: 25MB</p>
                    <input
                        ref={fileInputRef}
                        type="file"
                        className="hidden"
                        onChange={handleFileChange}
                        accept="image/*,.pdf,.doc,.docx,.xls,.xlsx,.txt"
                    />
                </div>
            ) : (
                <div className="bg-white/10 rounded-2xl p-4">
                    <div className="flex items-start gap-3">
                        {preview ? (
                            <img src={preview} alt="Preview" className="w-16 h-16 object-cover rounded-lg" />
                        ) : (
                            <div className="w-16 h-16 bg-white/10 rounded-lg flex items-center justify-center">
                                <File className="w-8 h-8 text-white/60" />
                            </div>
                        )}

                        <div className="flex-1 min-w-0">
                            <p className="text-white font-medium truncate">{selectedFile.name}</p>
                            <p className="text-xs text-white/60">{fileService.formatFileSize(selectedFile.size)}</p>

                            {uploading && (
                                <div className="mt-2">
                                    <div className="w-full bg-white/20 rounded-full h-2">
                                        <div
                                            className="bg-indigo-500 h-2 rounded-full transition-all duration-300"
                                            style={{ width: `${uploadProgress}%` }}
                                        />
                                    </div>
                                    <p className="text-xs text-white/60 mt-1">{uploadProgress}%</p>
                                </div>
                            )}
                        </div>

                        {!uploading && (
                            <button
                                onClick={clearFile}
                                className="p-2 hover:bg-white/10 rounded-lg transition"
                            >
                                <X className="w-5 h-5 text-white/60" />
                            </button>
                        )}
                    </div>

                    {!uploading && (
                        <button
                            onClick={handleUpload}
                            className="w-full mt-3 bg-indigo-500 hover:bg-indigo-600 text-white py-2 rounded-lg transition font-medium"
                        >
                            Upload File
                        </button>
                    )}
                </div>
            )}
        </div>
    );
}
