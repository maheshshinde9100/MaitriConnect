const http = require('http');
const fs = require('fs');
const path = require('path');
const { exec } = require('child_process');

const PORT = 3000;

// MIME types for different file extensions
const mimeTypes = {
    '.html': 'text/html',
    '.js': 'text/javascript',
    '.css': 'text/css',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpg',
    '.gif': 'image/gif',
    '.ico': 'image/x-icon',
    '.svg': 'image/svg+xml'
};

const server = http.createServer((req, res) => {
    // Set CORS headers
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

    // Handle OPTIONS requests
    if (req.method === 'OPTIONS') {
        res.writeHead(200);
        res.end();
        return;
    }

    // Default to chat-client.html if no specific file requested
    let filePath = req.url === '/' ? '/chat-client.html' : req.url;
    filePath = path.join(__dirname, filePath);

    // Get file extension
    const extname = String(path.extname(filePath)).toLowerCase();
    const mimeType = mimeTypes[extname] || 'application/octet-stream';

    // Check if file exists
    fs.readFile(filePath, (error, content) => {
        if (error) {
            if (error.code === 'ENOENT') {
                res.writeHead(404, { 'Content-Type': 'text/html' });
                res.end('<h1>404 - File Not Found</h1>', 'utf-8');
            } else {
                res.writeHead(500);
                res.end(`Server Error: ${error.code}`, 'utf-8');
            }
        } else {
            res.writeHead(200, { 'Content-Type': mimeType });
            res.end(content, 'utf-8');
        }
    });
});

server.listen(PORT, () => {
    console.log(`🚀 Chat Client Server running at http://localhost:${PORT}`);
    console.log(`📱 Open http://localhost:${PORT}/chat-client.html in your browser`);
    console.log('⏹️  Press Ctrl+C to stop the server');
    
    // Automatically open the browser (Windows)
    exec(`start http://localhost:${PORT}/chat-client.html`, (error) => {
        if (error) {
            console.log('💡 Please manually open http://localhost:3000/chat-client.html in your browser');
        }
    });
});

// Handle server shutdown gracefully
process.on('SIGINT', () => {
    console.log('\n🛑 Server stopped.');
    server.close();
    process.exit(0);
});