import { useState } from 'react';
import { Modal } from './Modal';
import { gruposApi } from '../api/services';
import type { Grupo } from '../types/domain';

interface NuevoGrupoModalProps {
  onClose: () => void;
  onCreated: (grupo: Grupo) => void;
}

const hoy = () => new Date().toISOString().slice(0, 10);

export function NuevoGrupoModal({ onClose, onCreated }: NuevoGrupoModalProps) {
  const [nombreGrupo, setNombreGrupo] = useState('');
  const [capacidadMaxima, setCapacidadMaxima] = useState(4);
  const [fechaCreacion, setFechaCreacion] = useState(hoy());
  const [grupoLleno, setGrupoLleno] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
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

    setSaving(true);
    try {
      const grupo = await gruposApi.crear({
        nombreGrupo: nombreGrupo.trim(),
        capacidadMaxima,
        fechaCreacion,
        grupoLleno,
      });
      onCreated(grupo);
    } catch (err) {
      // El Gateway devuelve 403 si la cuenta no tiene rol Admin — ver
      // SecurityConfig.java (CATALOGO_PATHS solo permite escritura a Admin).
      setError((err as Error).message ?? 'No se pudo crear el grupo.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title="Nuevo grupo" onClose={onClose}>
      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="nombreGrupo">Nombre del grupo</label>
          <input
            id="nombreGrupo"
            type="text"
            value={nombreGrupo}
            onChange={(e) => setNombreGrupo(e.target.value)}
            placeholder="ej. Grupo 4 - DSY1107"
            autoFocus
          />
        </div>

        <div className="form-field">
          <label htmlFor="capacidadMaxima">Capacidad máxima</label>
          <input
            id="capacidadMaxima"
            type="number"
            min={1}
            value={capacidadMaxima}
            onChange={(e) => setCapacidadMaxima(Number(e.target.value))}
          />
        </div>

        <div className="form-field">
          <label htmlFor="fechaCreacion">Fecha de creación</label>
          <input
            id="fechaCreacion"
            type="date"
            value={fechaCreacion}
            onChange={(e) => setFechaCreacion(e.target.value)}
          />
        </div>

        <div className="form-field form-field--checkbox">
          <input
            id="grupoLleno"
            type="checkbox"
            checked={grupoLleno}
            onChange={(e) => setGrupoLleno(e.target.checked)}
          />
          <label htmlFor="grupoLleno">Marcar como cupo lleno</label>
        </div>

        {error && <p className="form-error">{error}</p>}

        <div className="form-actions">
          <button type="button" className="btn-secondary" onClick={onClose}>
            Cancelar
          </button>
          <button type="submit" className="btn-primary" disabled={saving}>
            {saving ? 'Guardando…' : 'Crear grupo'}
          </button>
        </div>
      </form>
    </Modal>
  );
}