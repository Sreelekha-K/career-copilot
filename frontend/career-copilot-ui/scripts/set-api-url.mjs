import { mkdirSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));
const apiUrl = process.env.API_URL || process.env.BACKEND_URL || process.env.RENDER_BACKEND_URL;

if (!apiUrl) {
  console.log('API_URL was not set. Using the existing production environment API URL.');
  process.exit(0);
}

const environmentPath = resolve(__dirname, '../src/environments/environment.prod.ts');
const normalizedApiUrl = apiUrl.replace(/\/+$/, '');
const fileContent = `export const environment = {
  production: true,
  apiUrl: '${normalizedApiUrl}'
};
`;

mkdirSync(dirname(environmentPath), { recursive: true });
writeFileSync(environmentPath, fileContent, 'utf8');
console.log(`Production API URL configured: ${normalizedApiUrl}`);
