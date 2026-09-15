import { useState, type FormEvent } from 'react';
import { Modal } from './Modal';
import { trabajosApi } from '../api/services';
import type { Trabajo, TipoTrabajo } from '../types/domain';

interface EditarTrabajoModalProps {
  trabajo: Trabajo;
  onClose: () => void;
  onUpdated: (trabajo: Trabajo) => void;
}

export function EditarTrabajoModal({ trabajo, onClose, onUpdated }: EditarTrabajoModalProps) {
  const [nombreTrabajo, setNombreTrabajo] = useState(trabajo.nombreTrabajo);
  const [porcentajeNota, setPorcentajeNota] = useState(trabajo.porcentajeNota);
  const [idGrupo, setIdGrupo] = useState(trabajo.idGrupo);
  const [tipoTrabajo, setTipoTrabajo] = useState<TipoTrabajo>(trabajo.tipoTrabajo);
  const [semestre, setSemestre] = useState(trabajo.semestre);
  const [estado, setEstado] = useState(trabajo.estado);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    if (!nombreTrabajo.trim()) {
      setError('El nombre del trabajo no puede estar vacío.');
      return;
    }
    if (porcentajeNota <= 0 || porcentajeNota > 100) {
      setError('El porcentaje debe estar entre 1 y 100.');
      return;
    }
    if (idGrupo < 1) {
      setError('El ID del grupo debe ser mayor que 0.');
      return;
    }
    if (semestre < 1) {
      setError('El semestre debe ser mayor que 0.');
      return;
    }

    const payload: Trabajo = {
      idTrabajo: trabajo.idTrabajo,
      nombreTrabajo: nombreTrabajo.trim(),
      porcentajeNota,
      idGrupo,
      tipoTrabajo,
      semestre,
      estado,
    };

    setSaving(true);
    try {
      const actualizado = await trabajosApi.actualizar(trabajo.idTrabajo, payload);
      onUpdated(actualizado);
    } catch (err) {
      setError((err as Error).message ?? 'No se pudo actualizar el trabajo.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title="Editar trabajo" onClose={onClose}>
      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="editarNombreTrabajo">Nombre del trabajo</label>
          <input id="editarNombreTrabajo" type="text" value={nombreTrabajo} onChange={(e) => setNombreTrabajo(e.target.value)} autoFocus />
        </div>

        <div className="form-field">
          <label htmlFor="editarTipoTrabajo">Tipo de trabajo</label>
          <select id="editarTipoTrabajo" value={tipoTrabajo} onChange={(e) => setTipoTrabajo(e.target.value as TipoTrabajo)}>
            <option value="ENCARGO">Encargo</option>
            <option value="PRESENTACION">Presentación</option>
            <option value="OTRO">Otro</option>
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="editarIdGrupoTrabajo">ID del grupo</label>
          <input id="editarIdGrupoTrabajo" type="number" min={1} value={idGrupo} onChange={(e) => setIdGrupo(Number(e.target.value))} />
        </div>

        <div className="form-field">
          <label htmlFor="editarPorcentajeNota">Porcentaje de la nota</label>
          <input id="editarPorcentajeNota" type="number" min={1} max={100} value={porcentajeNota} onChange={(e) => setPorcentajeNota(Number(e.target.value))} />
        </div>

        <div className="form-field">
          <label htmlFor="editarSemestreTrabajo">Semestre</label>
          <input id="editarSemestreTrabajo" type="number" min={1} value={semestre} onChange={(e) => setSemestre(Number(e.target.value))} />
        </div>

        <div className="form-field">
          <label htmlFor="editarEstadoTrabajo">Estado</label>
          <select id="editarEstadoTrabajo" value={estado} onChange={(e) => setEstado(e.target.value as Trabajo['estado'])}>
            <option value="PENDIENTE">Pendiente</option>
            <option value="ENTREGADO">Entregado</option>
            <option value="EVALUADO">Evaluado</option>
          </select>
        </div>

        {error && <p className="form-error">{error}</p>}

        <div className="form-actions">
          <button type="button" className="btn-secondary" onClick={onClose}>Cancelar</button>
          <button type="submit" className="btn-primary" disabled={saving}>{saving ? 'Guardando…' : 'Guardar cambios'}</button>
        </div>
      </form>
    </Modal>
  );
}
