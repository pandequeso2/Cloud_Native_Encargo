import { useState, type FormEvent } from 'react';
import { Modal } from './Modal';
import { integrantesApi, type NuevoIntegrante } from '../api/services';
import type { Disponibilidad, Integrante } from '../types/domain';

interface NuevoIntegranteModalProps {
  onClose: () => void;
  onCreated: (integrante: Integrante) => void;
}

export function NuevoIntegranteModal({ onClose, onCreated }: NuevoIntegranteModalProps) {
  const [nombre, setNombre] = useState('');
  const [apellido, setApellido] = useState('');
  const [rutCuerpo, setRutCuerpo] = useState(0);
  const [rutDv, setRutDv] = useState('');
  const [correoElectronico, setCorreoElectronico] = useState('');
  const [idRol, setIdRol] = useState(1);
  const [idGrupo, setIdGrupo] = useState(1);
  const [disponibilidad, setDisponibilidad] = useState<Disponibilidad>('ALTA');
  const [idNota, setIdNota] = useState(1);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);

    if (!nombre.trim() || !apellido.trim()) {
      setError('El nombre y apellido son obligatorios.');
      return;
    }
    if (rutCuerpo < 1 || !rutDv.trim()) {
      setError('El RUT debe incluir cuerpo y digito verificador.');
      return;
    }
    if (!correoElectronico.trim() || !correoElectronico.includes('@')) {
      setError('Ingresa un correo electronico valido.');
      return;
    }
    if (idRol < 1 || idGrupo < 1 || idNota < 1) {
      setError('Los identificadores deben ser mayores que 0.');
      return;
    }

    const nuevoIntegrante: NuevoIntegrante = {
      nombre: nombre.trim(),
      apellido: apellido.trim(),
      rutCuerpo,
      rutDv: rutDv.trim().toUpperCase(),
      correoElectronico: correoElectronico.trim(),
      idRol,
      idGrupo,
      disponibilidad,
      idNota,
    };

    setSaving(true);
    try {
      const integrante = await integrantesApi.crear(nuevoIntegrante);
      onCreated(integrante);
    } catch (err) {
      setError((err as Error).message ?? 'No se pudo crear el integrante.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title="Nuevo integrante" onClose={onClose}>
      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="nombreIntegrante">Nombre</label>
          <input id="nombreIntegrante" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} autoFocus />
        </div>
        <div className="form-field">
          <label htmlFor="apellidoIntegrante">Apellido</label>
          <input id="apellidoIntegrante" type="text" value={apellido} onChange={(e) => setApellido(e.target.value)} />
        </div>
        <div className="form-field">
          <label htmlFor="rutCuerpo">RUT sin digito verificador</label>
          <input id="rutCuerpo" type="number" min={1} value={rutCuerpo || ''} onChange={(e) => setRutCuerpo(Number(e.target.value))} />
        </div>
        <div className="form-field">
          <label htmlFor="rutDv">Digito verificador</label>
          <input id="rutDv" type="text" maxLength={1} value={rutDv} onChange={(e) => setRutDv(e.target.value)} />
        </div>
        <div className="form-field">
          <label htmlFor="correoIntegrante">Correo electronico</label>
          <input id="correoIntegrante" type="email" value={correoElectronico} onChange={(e) => setCorreoElectronico(e.target.value)} />
        </div>
        <div className="form-field">
          <label htmlFor="idRol">ID del rol</label>
          <input id="idRol" type="number" min={1} value={idRol} onChange={(e) => setIdRol(Number(e.target.value))} />
        </div>
        <div className="form-field">
          <label htmlFor="idGrupoIntegrante">ID del grupo</label>
          <input id="idGrupoIntegrante" type="number" min={1} value={idGrupo} onChange={(e) => setIdGrupo(Number(e.target.value))} />
        </div>
        <div className="form-field">
          <label htmlFor="disponibilidad">Disponibilidad</label>
          <select id="disponibilidad" value={disponibilidad} onChange={(e) => setDisponibilidad(e.target.value as Disponibilidad)}>
            <option value="ALTA">Alta</option>
            <option value="MEDIA">Media</option>
            <option value="BAJA">Baja</option>
          </select>
        </div>
        <div className="form-field">
          <label htmlFor="idNota">ID de nota</label>
          <input id="idNota" type="number" min={1} value={idNota} onChange={(e) => setIdNota(Number(e.target.value))} />
        </div>

        {error && <p className="form-error">{error}</p>}
        <div className="form-actions">
          <button type="button" className="btn-secondary" onClick={onClose}>Cancelar</button>
          <button type="submit" className="btn-primary" disabled={saving}>{saving ? 'Guardando...' : 'Crear integrante'}</button>
        </div>
      </form>
    </Modal>
  );
}
