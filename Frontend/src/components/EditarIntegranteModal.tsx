import { useState, type FormEvent } from 'react';
import { Modal } from './Modal';
import { integrantesApi } from '../api/services';
import type { Disponibilidad, Integrante } from '../types/domain';

interface EditarIntegranteModalProps {
  integrante: Integrante;
  onClose: () => void;
  onUpdated: (integrante: Integrante) => void;
}

export function EditarIntegranteModal({ integrante, onClose, onUpdated }: EditarIntegranteModalProps) {
  const [nombre, setNombre] = useState(integrante.nombre);
  const [apellido, setApellido] = useState(integrante.apellido);
  const [rutCuerpo, setRutCuerpo] = useState(integrante.rutCuerpo);
  const [rutDv, setRutDv] = useState(integrante.rutDv);
  const [correoElectronico, setCorreoElectronico] = useState(integrante.correoElectronico);
  const [idRol, setIdRol] = useState(integrante.idRol);
  const [idGrupo, setIdGrupo] = useState(integrante.idGrupo);
  const [disponibilidad, setDisponibilidad] = useState<Disponibilidad>(integrante.disponibilidad);
  const [idNota, setIdNota] = useState(integrante.idNota);
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
      setError('El RUT debe incluir cuerpo y dígito verificador.');
      return;
    }
    if (!correoElectronico.trim() || !correoElectronico.includes('@')) {
      setError('Ingresa un correo electrónico válido.');
      return;
    }
    if (idRol < 1 || idGrupo < 1 || idNota < 1) {
      setError('Los identificadores deben ser mayores que 0.');
      return;
    }

    const payload: Integrante = {
      idIntegrante: integrante.idIntegrante,
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
      const actualizado = await integrantesApi.actualizar(integrante.idIntegrante, payload);
      onUpdated(actualizado);
    } catch (err) {
      setError((err as Error).message ?? 'No se pudo actualizar el integrante.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Modal title="Editar integrante" onClose={onClose}>
      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="editarNombreIntegrante">Nombre</label>
          <input id="editarNombreIntegrante" type="text" value={nombre} onChange={(e) => setNombre(e.target.value)} autoFocus />
        </div>
        <div className="form-field">
          <label htmlFor="editarApellidoIntegrante">Apellido</label>
          <input id="editarApellidoIntegrante" type="text" value={apellido} onChange={(e) => setApellido(e.target.value)} />
        </div>
        <div className="form-field">
          <label htmlFor="editarRutCuerpo">RUT sin dígito verificador</label>
          <input id="editarRutCuerpo" type="number" min={1} value={rutCuerpo} onChange={(e) => setRutCuerpo(Number(e.target.value))} />
        </div>
        <div className="form-field">
          <label htmlFor="editarRutDv">Dígito verificador</label>
          <input id="editarRutDv" type="text" maxLength={1} value={rutDv} onChange={(e) => setRutDv(e.target.value)} />
        </div>
        <div className="form-field">
          <label htmlFor="editarCorreoIntegrante">Correo electrónico</label>
          <input id="editarCorreoIntegrante" type="email" value={correoElectronico} onChange={(e) => setCorreoElectronico(e.target.value)} />
        </div>
        <div className="form-field">
          <label htmlFor="editarIdRol">ID del rol</label>
          <input id="editarIdRol" type="number" min={1} value={idRol} onChange={(e) => setIdRol(Number(e.target.value))} />
        </div>
        <div className="form-field">
          <label htmlFor="editarIdGrupoIntegrante">ID del grupo</label>
          <input id="editarIdGrupoIntegrante" type="number" min={1} value={idGrupo} onChange={(e) => setIdGrupo(Number(e.target.value))} />
        </div>
        <div className="form-field">
          <label htmlFor="editarDisponibilidad">Disponibilidad</label>
          <select id="editarDisponibilidad" value={disponibilidad} onChange={(e) => setDisponibilidad(e.target.value as Disponibilidad)}>
            <option value="ALTA">Alta</option>
            <option value="MEDIA">Media</option>
            <option value="BAJA">Baja</option>
          </select>
        </div>
        <div className="form-field">
          <label htmlFor="editarIdNota">ID de nota</label>
          <input id="editarIdNota" type="number" min={1} value={idNota} onChange={(e) => setIdNota(Number(e.target.value))} />
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
