// Tipos alineados a las entidades expuestas por los microservicios
// (ver Backend/*/model/*.java). Se mantienen mínimos: solo los campos
// que la UI realmente consume.

export interface Grupo {
  idGrupo: number;
  nombreGrupo: string;
  capacidadMaxima: number;
  fechaCreacion: string;
  grupoLleno: boolean;
}

export interface Integrante {
  idIntegrante: number;
  nombre: string;
  apellido: string;
  correoElectronico: string;
  idGrupo: number;
  disponibilidad: 'BAJA' | 'MEDIA' | 'ALTA';
}

export type EstadoTrabajo = 'PENDIENTE' | 'ENTREGADO' | 'EVALUADO';
export type TipoTrabajo = 'ENCARGO' | 'PRESENTACION' | 'OTRO';

export interface Trabajo {
  idTrabajo: number;
  nombreTrabajo: string;
  porcentajeNota: number;
  idGrupo: number;
  tipoTrabajo: TipoTrabajo;
  semestre: number;
  estado: EstadoTrabajo;
}

export interface Entrega {
  idEntrega: number;
  idGrupo: number;
  idTrabajo: number;
  fechaEntrega: string;
  estado: string;
}

export interface Comentario {
  idComentario: number;
  idEntrega: number;
  idProfesor: number;
  contenido: string;
  fechaComentario: string;
  tipoComentario: string;
}

export interface Nota {
  idNota: number;
  nota: number;
  idPonderacion: number;
  idIntegrante: number;
}

export interface GrupoConIntegrantes extends Grupo {
  integrantes: Integrante[];
}

export interface GrupoConTrabajo extends Grupo {
  trabajo: Trabajo | null;
}
