import { useState, type FormEvent } from 'react';
import { Modal } from './Modal';
import { trabajosApi, type NuevoTrabajo } from '../api/services';
import type { Trabajo, TipoTrabajo } from '../types/domain';

interface NuevoTrabajoModalProps {
  onClose: () => void;
  onCreated: (trabajo: Trabajo) => void;
}

export function NuevoTrabajoModal({ onClose, onCreated }: NuevoTrabajoModalProps) {
  const [nombreTrabajo, setNombreTrabajo] = useState('');
  const [porcentajeNota, setPorcentajeNota] = useState(25);
  const [idGrupo, setIdGrupo] = useState(1);
  const [tipoTrabajo, setTipoTrabajo] = useState<TipoTrabajo>('ENCARGO');
  const [semestre, setSemestre] = useState(new Date().getFullYear());
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

    const nuevoTrabajo: NuevoTrabajo = {
      nombreTrabajo: nombreTrabajo.trim(),
      porcentajeNota,
      idGrupo,
      tipoTrabajo,
      semestre,
      estado: 'PENDIENTE',
    };

    setSaving(true);
    try {
      const trabajo = await trabajosApi.crear(nuevoTrabajo);
      onCreated(trabajo);
    } catch (err) {
      setError((err as Error).message ?? 'No se pudo crear el trabajo.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title="Nuevo trabajo" onClose={onClose}>
      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="nombreTrabajo">Nombre del trabajo</label>
          <input
            id="nombreTrabajo"
            type="text"
            value={nombreTrabajo}
            onChange={(event) => setNombreTrabajo(event.target.value)}
            placeholder="ej. Informe de arquitectura"
            autoFocus
          />
        </div>

        <div className="form-field">
          <label htmlFor="tipoTrabajo">Tipo de trabajo</label>
          <select
            id="tipoTrabajo"
            value={tipoTrabajo}
            onChange={(event) => setTipoTrabajo(event.target.value as TipoTrabajo)}
          >
            <option value="ENCARGO">Encargo</option>
            <option value="PRESENTACION">Presentación</option>
            <option value="OTRO">Otro</option>
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="idGrupo">ID del grupo</label>
          <input
            id="idGrupo"
            type="number"
            min={1}
            value={idGrupo}
            onChange={(event) => setIdGrupo(Number(event.target.value))}
          />
        </div>

        <div className="form-field">
          <label htmlFor="porcentajeNota">Porcentaje de la nota</label>
          <input
            id="porcentajeNota"
            type="number"
            min={1}
            max={100}
            value={porcentajeNota}
            onChange={(event) => setPorcentajeNota(Number(event.target.value))}
          />
        </div>

        <div className="form-field">
          <label htmlFor="semestre">Semestre</label>
          <input
            id="semestre"
            type="number"
            min={1}
            value={semestre}
            onChange={(event) => setSemestre(Number(event.target.value))}
          />
        </div>

        {error && <p className="form-error">{error}</p>}

        <div className="form-actions">
          <button type="button" className="btn-secondary" onClick={onClose}>
            Cancelar
          </button>
          <button type="submit" className="btn-primary" disabled={saving}>
            {saving ? 'Guardando…' : 'Crear trabajo'}
          </button>
        </div>
      </form>
    </Modal>
  );
}
