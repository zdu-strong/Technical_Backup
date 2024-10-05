import { timer } from 'rxjs';
import fs from 'fs';
import { generateVideo } from '@/GenerateVideo';

async function main() {
  const cacheDir = process.env.FFCREATOR_CACHE_DIRECTORY;
  const outputVideoFilePath = process.env.FFCREATOR_OUTPUT_VIDEO_FILE_PATH;
  if (!cacheDir || !cacheDir.trim()) {
    throw new Error('CacheDir(FFCREATOR_CACHE_DIRECTORY) can not be empty');
  }
  if (!outputVideoFilePath || !outputVideoFilePath.trim()) {
    throw new Error(
      'OutputVideoFilePath(FFCREATOR_OUTPUT_VIDEO_FILE_PATH) can not be empty',
    );
  }
  const creator = await generateVideo({
    cacheDir,
    outputVideoFilePath,
  });
  const resultPromise = new Promise((resolve, reject) => {
    creator.on('error', (e) => {
      reject(new Error(e.error || `FFCreator has error`));
    });
    creator.on('complete', () => {
      resolve(`FFCreator completed `);
    });
  });
  await creator.start();
  await resultPromise;
  await fs.promises.rm(cacheDir, { recursive: true, force: true });

  console.log('\n');
  for (let i = 20; i > 0; i--) {
    await timer(1).toPromise();
  }

  process.exit();
}

export default main();
