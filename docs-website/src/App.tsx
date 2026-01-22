import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { Layout } from './components/Layout';
import { Home } from './pages/Home';
import { MWAGuide } from './pages/MWAGuide';
import { PublishingGuide } from './pages/PublishingGuide';
import { DigitalAssetLinks } from './pages/DigitalAssetLinks';
import { TWAGuide } from './pages/TWAGuide';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="mwa-guide" element={<MWAGuide />} />
          <Route path="twa-guide" element={<TWAGuide />} />
          <Route path="publishing" element={<PublishingGuide />} />
          <Route path="digital-asset-links" element={<DigitalAssetLinks />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
