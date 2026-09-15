import { useState, type FormEvent } from 'react';
import { Modal } from './Modal';
import { gruposApi } from '../api/services';
import type { Grupo } from '../types/domain';

interface EditarGrupoModalProps {
  grupo: Grupo;
  onClose: () => void;
  onUpdated: (grupo: Grupo) => void;
}

export function EditarGrupoModal({ grupo, onClose, onUpdated }: EditarGrupoModalProps) {
  const [nombreGrupo, setNombreGrupo] = useState(grupo.nombreGrupo);
  const [capacidadMaxima, setCapacidadMaxima] = useState(grupo.capacidadMaxima);
  const [fechaCreacion, setFechaCreacion] = useState(grupo.fechaCreacion);
  const [grupoLleno, setGrupoLleno] = useState(grupo.grupoLleno);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!nombreGrupo.trim()) {
      setError('El nombre del grupo no puede estar vacío.');
      return;
    }
    if (capacidadMaxima < 1) {
      setError('La capacidad máxima debe ser al menos 1.');
      return;
    }

    const payload: Grupo = {
      idGrupo: grupo.idGrupo,
      nombreGrupo: nombreGrupo.trim(),
      capacidadMaxima,
      fechaCreacion,
      grupoLleno,
    };

    setSaving(true);
    try {
      const actualizado = await gruposApi.actualizar(grupo.idGrupo, payload);
      onUpdated(actualizado);
    } catch (err) {
      setError((err as Error).message ?? 'No se pudo actualizar el grupo.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title="Editar grupo" onClose={onClose}>
      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="editarNombreGrupo">Nombre del grupo</label>
          <input id="editarNombreGrupo" type="text" value={nombreGrupo} onChange={(e) => setNombreGrupo(e.target.value)} autoFocus />
        </div>

        <div className="form-field">
          <label htmlFor="editarCapacidadMaxima">Capacidad máxima</label>
          <input id="editarCapacidadMaxima" type="number" min={1} value={capacidadMaxima} onChange={(e) => setCapacidadMaxima(Number(e.target.value))} />
        </div>

        <div className="form-field">
          <label htmlFor="editarFechaCreacionGrupo">Fecha de creación</label>
          <input id="editarFechaCreacionGrupo" type="date" value={fechaCreacion} onChange={(e) => setFechaCreacion(e.target.value)} />
        </div>

        <div className="form-field form-field--checkbox">
          <input id="editarGrupoLleno" type="checkbox" checked={grupoLleno} onChange={(e) => setGrupoLleno(e.target.checked)} />
          <label htmlFor="editarGrupoLleno">Marcar como cupo lleno</label>
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
