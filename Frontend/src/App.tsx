import { Route, Routes } from 'react-router-dom';
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { Grupos } from './pages/Grupos';
import { GrupoDetalle } from './pages/GrupoDetalle';
import { Trabajos } from './pages/Trabajos';
import { Entregas } from './pages/Entregas';
import { ProtectedRoute } from './routes/ProtectedRoute';
import { Layout } from './components/Layout';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route
        path="/*"
        element={
          <ProtectedRoute>
            <Layout>
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/grupos" element={<Grupos />} />
                <Route path="/grupos/:idGrupo" element={<GrupoDetalle />} />
                <Route path="/trabajos" element={<Trabajos />} />
                <Route path="/entregas" element={<Entregas />} />
              </Routes>
            </Layout>
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;
