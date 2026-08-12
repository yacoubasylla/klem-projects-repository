import { KlemButton } from '@klem/ui';
import { formatDate, logKlemAction } from '@klem/utils';
import { KlemProvider, useKlemLicense } from '@klem/license';

// Jetons de démonstration signés par scripts/generate-demo-license.mjs (packages/license) avec
// une paire de clés jetable — pas de secret réel, illustrent simplement les statuts 'valid' et
// 'expired' de @klem/license en mode avertissement seul (jamais de blocage de rendu).
const DEMO_VALID_LICENSE_KEY =
  'eyJjbGllbnRJZCI6IktMRU0tREVNTyIsImFwcElkIjoicGFyY2F1dG8iLCJpc3N1ZWRBdCI6IjIwMjYtMDgtMTJUMTE6MjU6MzguMjkzWiIsImV4cGlyZXNBdCI6IjIwMjctMDgtMTJUMTE6MjU6MzguMjkzWiJ9.FrR-bNlqq9MHJGeIGNFgF-YCJBsmjC9JBMgC9fnfLcvYN23oMQXJiuoXECGNZ-MifzWBwE8JEdt33s7ZM-4plg';
const DEMO_EXPIRED_LICENSE_KEY =
  'eyJjbGllbnRJZCI6IktMRU0tREVNTyIsImFwcElkIjoicGFyY2F1dG8iLCJpc3N1ZWRBdCI6IjIwMjYtMDgtMTFUMTE6MjU6MzguMjkzWiIsImV4cGlyZXNBdCI6IjIwMjYtMDgtMTFUMTE6MjU6MzguMjkzWiJ9.AshHcJ-N4F3mjqrxHWzsX1osIqTp0y8P8CH5APlk1wy0zRMiXCyHooCMb5sRGY9a-PEcdWqKO04XUJ5nLOK-hw';

function LicenseStatusBadge({ label }: { label: string }) {
  const { status } = useKlemLicense();
  return (
    <p style={{ fontFamily: 'monospace', fontSize: 13 }}>
      {label} → statut licence : <strong>{status}</strong>
    </p>
  );
}

export default function App() {
  const handlePress = () => {
    logKlemAction('Bouton cliqué');
    alert(`La date est : ${formatDate(new Date())}`);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>Dashboard KLEM</h1>
      <KlemButton label="Tester la connexion" onClick={handlePress} />

      {/*
        Démo d'intégration de @klem/license : en production, remplacer les deux jetons de démo
        ci-dessus par une seule clé réelle lue depuis les variables d'environnement, ex.
        `licenseKey={import.meta.env.VITE_KTS_LICENSE_KEY}`.
      */}
      <section style={{ marginTop: 32 }}>
        <h2>Démo @klem/license (mode avertissement seul, aucun blocage)</h2>
        <KlemProvider licenseKey={DEMO_VALID_LICENSE_KEY} appId="parcauto">
          <LicenseStatusBadge label="Clé valide" />
        </KlemProvider>
        <KlemProvider licenseKey={DEMO_EXPIRED_LICENSE_KEY} appId="parcauto">
          <LicenseStatusBadge label="Clé expirée" />
        </KlemProvider>
        <KlemProvider licenseKey={undefined} appId="parcauto">
          <LicenseStatusBadge label="Clé absente" />
        </KlemProvider>
      </section>
    </div>
  );
}
