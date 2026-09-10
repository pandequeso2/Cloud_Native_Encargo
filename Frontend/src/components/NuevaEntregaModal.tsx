import { useEffect, useState, type FormEvent } from 'react';
import { entregasApi, gruposApi, trabajosApi } from '../api/services';
import type { Entrega, EstadoEntrega, Grupo, Trabajo } from '../types/domain';
import { Modal } from './Modal';

interface NuevaEntregaModalProps {
  onClose: () => void;
  onCreated: (entrega: Entrega) => void;
}

const hoy = () => new Date().toISOString().slice(0, 10);

export function NuevaEntregaModal({ onClose, onCreated }: NuevaEntregaModalProps) {
  const [grupos, setGrupos] = useState<Grupo[]>([]);
  const [trabajos, setTrabajos] = useState<Trabajo[]>([]);
  const [idGrupo, setIdGrupo] = useState('');
  const [idTrabajo, setIdTrabajo] = useState('');
  const [fechaEntrega, setFechaEntrega] = useState(hoy());
  const [estado, setEstado] = useState<EstadoEntrega>('PENDIENTE');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    Promise.all([gruposApi.listar(), trabajosApi.listar()])
      .then(([gruposCargados, trabajosCargados]) => {
        setGrupos(gruposCargados);
        setTrabajos(trabajosCargados);
        setIdGrupo(String(gruposCargados[0]?.idGrupo ?? ''));
        setIdTrabajo(String(trabajosCargados[0]?.idTrabajo ?? ''));
      })
      .catch((err: unknown) => {
        setError(err instanceof Error ? err.message : 'No se pudieron cargar grupos y trabajos.');
      });
  }, []);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    if (!idGrupo || !idTrabajo || !fechaEntrega) {
      setError('Selecciona un grupo, un trabajo y una fecha.');
      return;
    }

    setSaving(true);
    try {
      const entrega = await entregasApi.crear({
        idGrupo: Number(idGrupo),
        idTrabajo: Number(idTrabajo),
        fechaEntrega,
        estado,
      });
      onCreated(entrega);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'No se pudo crear la entrega.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title="Nueva entrega" onClose={onClose}>
      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="entregaGrupo">Grupo</label>
          <select id="entregaGrupo" value={idGrupo} onChange={(event) => setIdGrupo(event.target.value)}>
            <option value="">Selecciona un grupo</option>
            {grupos.map((grupo) => (
              <option key={grupo.idGrupo} value={grupo.idGrupo}>
                {grupo.nombreGrupo}
              </option>
            ))}
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="entregaTrabajo">Trabajo</label>
          <select
            id="entregaTrabajo"
            value={idTrabajo}
            onChange={(event) => setIdTrabajo(event.target.value)}
          >
            <option value="">Selecciona un trabajo</option>
            {trabajos.map((trabajo) => (
              <option key={trabajo.idTrabajo} value={trabajo.idTrabajo}>
                {trabajo.nombreTrabajo}
              </option>
            ))}
          </select>
        </div>

        <div className="form-field">
          <label htmlFor="fechaEntrega">Fecha de entrega</label>
          <input
            id="fechaEntrega"
            type="date"
            value={fechaEntrega}
            onChange={(event) => setFechaEntrega(event.target.value)}
          />
        </div>

        <div className="form-field">
          <label htmlFor="estadoEntrega">Estado</label>
          <select
            id="estadoEntrega"
            value={estado}
            onChange={(event) => setEstado(event.target.value as EstadoEntrega)}
          >
            <option value="PENDIENTE">Pendiente</option>
            <option value="ENTREGADO">Entregado</option>
            <option value="ATRASADO">Atrasado</option>
          </select>
        </div>

        {error && <p className="form-error">{error}</p>}

        <div className="form-actions">
          <button type="button" className="btn-secondary" onClick={onClose}>
            Cancelar
          </button>
          <button type="submit" className="btn-primary" disabled={saving || !grupos.length || !trabajos.length}>
            {saving ? 'Guardando...' : 'Crear entrega'}
          </button>
        </div>
      </form>
    </Modal>
  );
}