// src/services/fileService.js
import axios from 'axios';

const API_BASE = 'http://localhost:8080';

export const fileService = {
  async uploadFile(file, userId, chatRoomId, onProgress) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('userId', userId);
    formData.append('chatRoomId', chatRoomId);

    try {
      const response = await axios.post(`${API_BASE}/api/chat/files/upload`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        onUploadProgress: (progressEvent) => {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
          if (onProgress) {
            onProgress(percentCompleted);
          }
        },
      });
      return response.data;
    } catch (error) {
      console.error('File upload error:', error);
      throw error;
    }
  },

  getDownloadUrl(fileId) {
    return `${API_BASE}/api/chat/files/download/${fileId}`;
  },

  async getFileMetadata(fileId) {
    try {
      const response = await axios.get(`${API_BASE}/api/chat/files/metadata/${fileId}`);
      return response.data;
    } catch (error) {
      console.error('Error fetching file metadata:', error);
      throw error;
    }
  },

  async deleteFile(fileId) {
    try {
      await axios.delete(`${API_BASE}/api/chat/files/${fileId}`);
    } catch (error) {
      console.error('Error deleting file:', error);
      throw error;
    }
  },

  isImageFile(fileType) {
    return fileType && fileType.startsWith('image/');
  },

  isDocumentFile(fileType) {
    const docTypes = [
      'application/pdf',
      'application/msword',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'application/vnd.ms-excel',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'text/plain',
    ];
    return docTypes.includes(fileType);
  },

  formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  },
};
