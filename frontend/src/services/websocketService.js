import { Client } from '@stomp/stompjs';

class WebSocketService {
  constructor() {
    this.client = null;
    this.connected = false;
    this.subscriptions = new Map();
  }

  connect(onMessageReceived, onConnected, onError) {
    try {
      // Use native WebSocket instead of SockJS to avoid the global issue
      this.client = new Client({
        brokerURL: 'ws://localhost:8082/ws',
        connectHeaders: {},
        debug: (str) => {
          console.log('STOMP: ' + str);
        },
        reconnectDelay: 5000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
      });

      this.client.onConnect = (frame) => {
        console.log('Connected: ' + frame);
        this.connected = true;
        
        // Subscribe to public messages
        const subscription = this.client.subscribe('/topic/public', (message) => {
          onMessageReceived(JSON.parse(message.body));
        });
        
        this.subscriptions.set('public', subscription);
        
        if (onConnected) onConnected();
      };

      this.client.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
        this.connected = false;
        if (onError) onError(frame);
      };

      this.client.onWebSocketError = (error) => {
        console.error('WebSocket error:', error);
        this.connected = false;
        if (onError) onError(error);
      };

      this.client.activate();
    } catch (error) {
      console.error('Failed to connect to WebSocket:', error);
      if (onError) onError(error);
    }
  }

  subscribeToRoom(roomId, onMessageReceived) {
    if (this.client && this.connected) {
      const subscription = this.client.subscribe(`/topic/room/${roomId}`, (message) => {
        onMessageReceived(JSON.parse(message.body));
      });
      this.subscriptions.set(roomId, subscription);
    }
  }

  unsubscribeFromRoom(roomId) {
    const subscription = this.subscriptions.get(roomId);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(roomId);
    }
  }

  sendMessage(message) {
    if (this.client && this.connected) {
      this.client.publish({
        destination: '/app/chat.sendMessage',
        body: JSON.stringify(message)
      });
    }
  }

  addUser(username) {
    if (this.client && this.connected) {
      this.client.publish({
        destination: '/app/chat.addUser',
        body: JSON.stringify({
          senderId: username,
          type: 'JOIN'
        })
      });
    }
  }

  disconnect() {
    if (this.client) {
      // Unsubscribe from all subscriptions
      this.subscriptions.forEach(subscription => subscription.unsubscribe());
      this.subscriptions.clear();
      
      this.client.deactivate();
      this.connected = false;
    }
  }
}

export default new WebSocketService();