import { httpClient } from './httpClient';
import type {
  Comentario,
  Entrega,
  Grupo,
  GrupoConIntegrantes,
  GrupoConTrabajo,
  Integrante,
  Profesor,
  Trabajo,
} from '../types/domain';

// Las rutas coinciden exactamente con las expuestas por el Gateway
// (Backend/gateway/src/main/resources/application.yaml).

export interface NuevoGrupo {
  nombreGrupo: string;
  capacidadMaxima: number;
  fechaCreacion: string; // formato ISO yyyy-MM-dd, tal como lo espera LocalDate en el backend
  grupoLleno: boolean;
}

export type NuevoIntegrante = Omit<Integrante, 'idIntegrante'>;

export interface NuevoTrabajo {
  nombreTrabajo: string;
  porcentajeNota: number;
  idGrupo: number;
  tipoTrabajo: Trabajo['tipoTrabajo'];
  semestre: number;
  estado: Trabajo['estado'];
}

export type NuevaEntrega = Omit<Entrega, 'idEntrega'>;

export const gruposApi = {
  listar: () => httpClient.get<Grupo[]>('/api/v1/grupos').then((r) => r.data),
  conIntegrantes: (idGrupo: number) =>
    httpClient
      .get<GrupoConIntegrantes>(`/api/v1/grupos/${idGrupo}/con-integrantes`)
      .then((r) => r.data),
  conTrabajo: (idGrupo: number) =>
    httpClient.get<GrupoConTrabajo>(`/api/v1/grupos/${idGrupo}/con-trabajo`).then((r) => r.data),
  // Solo el rol Admin tiene permiso en el Gateway para este endpoint (POST /api/v1/grupos).
  crear: (nuevo: NuevoGrupo) =>
    httpClient.post<Grupo>('/api/v1/grupos', nuevo).then((r) => r.data),
  actualizar: (id: number, grupo: Grupo) =>
    httpClient.put<Grupo>(`/api/v1/grupos/${id}`, grupo).then((r) => r.data),
  eliminar: (id: number) => httpClient.delete<void>(`/api/v1/grupos/${id}`).then((r) => r.data),
};

export const trabajosApi = {
  listar: () => httpClient.get<Trabajo[]>('/api/v1/trabajos').then((r) => r.data),
  crear: (nuevo: NuevoTrabajo) =>
    httpClient.post<Trabajo>('/api/v1/trabajos', nuevo).then((r) => r.data),
  actualizar: (id: number, trabajo: Trabajo) =>
    httpClient.put<Trabajo>(`/api/v1/trabajos/${id}`, trabajo).then((r) => r.data),
  eliminar: (id: number) => httpClient.delete<void>(`/api/v1/trabajos/${id}`).then((r) => r.data),
};

export const entregasApi = {
  listar: () => httpClient.get<Entrega[]>('/api/v1/entregas').then((r) => r.data),
  crear: (nueva: NuevaEntrega) =>
    httpClient.post<Entrega>('/api/v1/entregas', nueva).then((r) => r.data),
  actualizar: (id: number, entrega: Entrega) =>
    httpClient.put<Entrega>(`/api/v1/entregas/${id}`, entrega).then((r) => r.data),
  eliminar: (id: number) => httpClient.delete<void>(`/api/v1/entregas/${id}`).then((r) => r.data),
  porGrupo: (idGrupo: number) =>
    httpClient
      .get<Entrega[]>('/api/v1/entregas')
      .then((r) => r.data.filter((e) => e.idGrupo === idGrupo)),
};

export const comentariosApi = {
  porEntrega: (idEntrega: number) =>
    httpClient
      .get<Comentario[]>('/api/v1/comentarios')
      .then((r) => r.data.filter((c) => c.idEntrega === idEntrega)),
};

export const integrantesApi = {
  listar: () => httpClient.get<Integrante[]>('/api/v1/integrantes').then((r) => r.data),
  crear: (nuevo: NuevoIntegrante) =>
    httpClient.post<Integrante>('/api/v1/integrantes', nuevo).then((r) => r.data),
  actualizar: (id: number, integrante: Integrante) =>
    httpClient.put<Integrante>(`/api/v1/integrantes/${id}`, integrante).then((r) => r.data),
  eliminar: (id: number) => httpClient.delete<void>(`/api/v1/integrantes/${id}`).then((r) => r.data),
};

export const profesoresApi = {
  listar: () => httpClient.get<Profesor[]>('/api/v1/profesores').then((r) => r.data),
  crear: (nuevo: Omit<Profesor, 'idProfesor'>) =>
    httpClient.post<Profesor>('/api/v1/profesores', nuevo).then((r) => r.data),
  actualizar: (id: number, profesor: Profesor) =>
    httpClient.put<Profesor>(`/api/v1/profesores/${id}`, profesor).then((r) => r.data),
  eliminar: (id: number) => httpClient.delete<void>(`/api/v1/profesores/${id}`).then((r) => r.data),
};