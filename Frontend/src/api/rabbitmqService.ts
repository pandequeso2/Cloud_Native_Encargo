import axios from 'axios';
import { API_BASE_URL } from '../auth/authConfig';

// Dedicated clean client for RabbitMQ endpoints (Public path in Gateway)
const rabbitClient = axios.create({ baseURL: API_BASE_URL });

export interface RabbitStatusResponse {
  backend: string;
  rabbitmq: string;
  timestamp: string;
  ordersQueueMessages?: number;
  dlqMessages?: number;
}

export interface SendOrderResponse {
  status: string;
  orderId: string;
  message: string;
}

export const rabbitmqApi = {
  // Check Status
  getStatus: async (): Promise<RabbitStatusResponse> => {
    const response = await rabbitClient.get<RabbitStatusResponse>('/api/v1/rabbitmq/orders/status');
    return response.data;
  },

  // Direct Exchange Logging
  sendLog: async (level: 'INFO' | 'WARNING' | 'ERROR', message: string): Promise<string> => {
    const response = await rabbitClient.post<string>('/api/v1/rabbitmq/logs', { level, message });
    return response.data;
  },

  // Orders / DLQ Simulation
  sendOrder: async (orderId?: string, customerName?: string): Promise<SendOrderResponse> => {
    const response = await rabbitClient.post<SendOrderResponse>('/api/v1/rabbitmq/orders/send', {
      orderId,
      customerName,
    });
    return response.data;
  },

  // RabbitAdmin Dynamic Resources
  createQueue: async (queueName: string): Promise<string> => {
    const response = await rabbitClient.post<string>(`/api/v1/rabbitmq/admin/queues?queueName=${encodeURIComponent(queueName)}`);
    return response.data;
  },

  createExchange: async (exchangeName: string): Promise<string> => {
    const response = await rabbitClient.post<string>(`/api/v1/rabbitmq/admin/exchanges?exchangeName=${encodeURIComponent(exchangeName)}`);
    return response.data;
  },

  createBinding: async (queueName: string, exchangeName: string, routingKey: string): Promise<string> => {
    const response = await rabbitClient.post<string>(
      `/api/v1/rabbitmq/admin/bindings?queueName=${encodeURIComponent(queueName)}&exchangeName=${encodeURIComponent(exchangeName)}&routingKey=${encodeURIComponent(routingKey)}`
    );
    return response.data;
  },

  purgeQueue: async (queueName: string): Promise<string> => {
    const response = await rabbitClient.post<string>(`/api/v1/rabbitmq/admin/queues/${encodeURIComponent(queueName)}/purge`);
    return response.data;
  },

  // Listeners Management
  getListeners: async (): Promise<string[]> => {
    const response = await rabbitClient.get<string[]>('/api/v1/rabbitmq/listeners');
    return response.data;
  },

  pauseListener: async (listenerId: string): Promise<string> => {
    const response = await rabbitClient.post<string>(`/api/v1/rabbitmq/listeners/${encodeURIComponent(listenerId)}/pause`);
    return response.data;
  },

  resumeListener: async (listenerId: string): Promise<string> => {
    const response = await rabbitClient.post<string>(`/api/v1/rabbitmq/listeners/${encodeURIComponent(listenerId)}/resume`);
    return response.data;
  },
};
