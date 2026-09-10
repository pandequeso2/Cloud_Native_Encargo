import { httpClient } from './httpClient';
import type {
  Comentario,
  Entrega,
  Grupo,
  GrupoConIntegrantes,
  GrupoConTrabajo,
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
};

export const trabajosApi = {
  listar: () => httpClient.get<Trabajo[]>('/api/v1/trabajos').then((r) => r.data),
  crear: (nuevo: NuevoTrabajo) =>
    httpClient.post<Trabajo>('/api/v1/trabajos', nuevo).then((r) => r.data),
};

export const entregasApi = {
  listar: () => httpClient.get<Entrega[]>('/api/v1/entregas').then((r) => r.data),
  crear: (nueva: NuevaEntrega) =>
    httpClient.post<Entrega>('/api/v1/entregas', nueva).then((r) => r.data),
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