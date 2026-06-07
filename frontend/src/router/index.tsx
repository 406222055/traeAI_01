import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AppLayout } from '../layouts/AppLayout';
import { LoginPage } from '../pages/login/LoginPage';
import { DashboardPage } from '../pages/dashboard/DashboardPage';
import { VendorsPage } from '../pages/vendors/VendorsPage';
import { ProjectsPage } from '../pages/projects/ProjectsPage';
import { AdmissionsPage } from '../pages/admissions/AdmissionsPage';
import { AlertsPage } from '../pages/alerts/AlertsPage';
import { CertificatesPage } from '../pages/certificates/CertificatesPage';
import { getToken } from '../utils/auth';

function ProtectedRoute() {
  if (!getToken()) {
    return <Navigate to="/login" replace />;
  }

  return <AppLayout />;
}

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <ProtectedRoute />,
    children: [
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: 'vendors',
        element: <VendorsPage />,
      },
      {
        path: 'projects',
        element: <ProjectsPage />,
      },
      {
        path: 'admissions',
        element: <AdmissionsPage />,
      },
      {
        path: 'alerts',
        element: <AlertsPage />,
      },
      {
        path: 'certificates',
        element: <CertificatesPage />,
      },
    ],
  },
]);
