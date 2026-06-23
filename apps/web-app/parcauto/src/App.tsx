import { KlemButton } from '@klem/ui';
import { formatDate, logKlemAction } from '@klem/utils';

export default function App() {
  const handlePress = () => {
    logKlemAction('Bouton cliqué');
    alert(`La date est : ${formatDate(new Date())}`);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>Dashboard KLEM</h1>
      <KlemButton label="Tester la connexion" onClick={handlePress} />
    </div>
  );
}