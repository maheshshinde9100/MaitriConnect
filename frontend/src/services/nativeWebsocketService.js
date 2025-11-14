// Native WebSocket implementation as fallback
class NativeWebSocketService {
  constructor() {
    this.ws = null;
    this.connected = false;
    this.reconnectAttempts = 0;
    this.maxReconnectAttempts = 5;
    this.reconnectDelay = 5000;
    this.messageHandlers = new Set();
    this.connectionHandlers = new Set();
    this.errorHandlers = new Set();
  }

  connect(onMessageReceived, onConnected, onError) {
    try {
      // Use native WebSocket instead of SockJS
      this.ws = new WebSocket('ws://localhost:8082/ws');
      
      this.ws.onopen = (event) => {
        console.log('WebSocket connected:', event);
        this.connected = true;
        this.reconnectAttempts = 0;
        
        if (onConnected) onConnected();
        this.connectionHandlers.forEach(handler => handler());
      };

      this.ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data);
          if (onMessageReceived) onMessageReceived(message);
          this.messageHandlers.forEach(handler => handler(message));
        } catch (error) {
          console.error('Error parsing WebSocket message:', error);
        }
      };

      this.ws.onclose = (event) => {
        console.log('WebSocket disconnected:', event);
        this.connected = false;
        
        // Attempt to reconnect
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
          this.reconnectAttempts++;
          console.log(`Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);
          setTimeout(() => {
            this.connect(onMessageReceived, onConnected, onError);
          }, this.reconnectDelay);
        }
      };

      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error);
        this.connected = false;
        if (onError) onError(error);
        this.errorHandlers.forEach(handler => handler(error));
      };

    } catch (error) {
      console.error('Failed to create WebSocket connection:', error);
      if (onError) onError(error);
    }
  }

  sendMessage(message) {
    if (this.ws && this.connected && this.ws.readyState === WebSocket.OPEN) {
      try {
        // Send message in the format expected by the backend
        const messagePayload = {
          type: 'SEND_MESSAGE',
          payload: message
        };
        this.ws.send(JSON.stringify(messagePayload));
      } catch (error) {
        console.error('Error sending message:', error);
      }
    } else {
      console.warn('WebSocket is not connected. Cannot send message.');
    }
  }

  addUser(username) {
    if (this.ws && this.connected && this.ws.readyState === WebSocket.OPEN) {
      try {
        const joinMessage = {
          type: 'JOIN',
          payload: {
            senderId: username,
            type: 'JOIN'
          }
        };
        this.ws.send(JSON.stringify(joinMessage));
      } catch (error) {
        console.error('Error sending join message:', error);
      }
    }
  }

  disconnect() {
    if (this.ws) {
      this.ws.close();
      this.ws = null;
      this.connected = false;
    }
  }

  // Additional methods for event handling
  onMessage(handler) {
    this.messageHandlers.add(handler);
    return () => this.messageHandlers.delete(handler);
  }

  onConnection(handler) {
    this.connectionHandlers.add(handler);
    return () => this.connectionHandlers.delete(handler);
  }

  onError(handler) {
    this.errorHandlers.add(handler);
    return () => this.errorHandlers.delete(handler);
  }
}

export default new NativeWebSocketService();